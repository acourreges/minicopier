/*
  TransferTask.java / MiniCopier
  Copyright (C) 2007-2009 Adrian Courrèges

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 2 of
  the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package minicopier;
import java.io.*;


public class TransferTask extends Thread{

	private Copier copier;
	
	private File source;
	
	private File destination;
	
	private File destinationFolder;
	
	private Boolean append;
	
	private Boolean paused;
	
	private Boolean cancel;
	
	private long totalSize;
	
	private long currentSize;
	
	boolean completed;
	
	boolean putInQueue;
	

	
	public TransferTask(Copier c, FileToTransfer file, boolean _app){
		this.copier = c;
		this.source=file.getSourceFile();
		this.destination=file.getDestinationFile();
		this.destinationFolder=file.getDestinationFolder();
		this.append=_app;
		this.paused = false;
		this.cancel = false;
		this.totalSize = file.getSize();
		this.currentSize = 0;
		this.completed = false;
		this.putInQueue = false;
	}
	
	
	public boolean getCompleted(){
		return this.completed;
	}
	
	
	public void setPause() {
		this.paused = true;
	}
	
	public void unpause() {
		synchronized(this) {
		this.paused = false;
		this.notifyAll();
		}
	}
	
	public void setCancelAndQueue(boolean _putInQueue){
		this.cancel = true;
		this.putInQueue = _putInQueue;
	}
	
	public boolean getPutInQueue(){
		return this.putInQueue;
	}
	
	public boolean getCancel() {
		return this.cancel;
	}
	
	public void run() {
		// Streams declarations
		ThroughputLimitInputStream sourceFile = null;
        java.io.FileOutputStream destinationFile = null;
        
        // Read by segment of 0.5Mo 
        int bufferSize = 512*1024;
        
        this.copier.setFileTotalSize(this.totalSize);
        this.copier.setFileCurrentSize(0);
        
        try {
        		//creation of directories (if needed)
        		this.destinationFolder.mkdirs();
                // Creation of file (nothing done if already present)
                this.destination.createNewFile();
                
                // Opening streams
                sourceFile = new ThroughputLimitInputStream(new java.io.FileInputStream(source));
                destinationFile = new java.io.FileOutputStream(destination,this.append);

                if (append) {
                // Skip already copied bytes
	                long already_copied = destination.length();
	                long jump_source;
	                jump_source = sourceFile.skip(already_copied);
	                
	                //Might be useless (?)
	                //risk of skipping too much (javadoc)
	                while (jump_source != already_copied) {
	                	sourceFile = new ThroughputLimitInputStream(new java.io.FileInputStream(source));
	                	jump_source = sourceFile.skip(already_copied);
	                }
	                
	                this.copier.increaseFileCurrentSize(jump_source);
                }

                // Read by segment of 0.5Mo 
                byte buffer[]=new byte[bufferSize];
                int nbReads;
                
                while( (nbReads = sourceFile.read(buffer)) != -1 ) {
                        destinationFile.write(buffer, 0, nbReads);
                        
                        this.copier.increaseFileCurrentSize(nbReads);
                        
                        if (this.paused) {
                        	synchronized(this) {
	                        	try{
	                        		this.copier.setPause(true);
	                        		System.out.println("COPY PAUSED");
	                        		wait();	
	                        		this.copier.setPause(false);
	                        		System.out.println("COPY RESUMED");
	                        	}
	                        	catch (InterruptedException e) {}
                        	}
                        	
                        }
                        
                        if (this.cancel) { //Skip or cancel requested by user
                        	return;
                        }
                        
                } 
                
                // Copy successful
                this.completed = true;
                //System.out.println("Transfer completed !");
               
        } catch( java.io.FileNotFoundException f ) {
                
        } catch( java.io.IOException e ) {
        	System.out.println("IO Exception !");
                
        } finally {
                // Whatever happens, closing streams
                try {
                        sourceFile.close();
                } catch(Exception e) { 
                	System.out.println("Error closing source file !");
                }
                try {
                        destinationFile.close();
                } catch(Exception e) {
                	System.out.println("Error closing destination file !");
                }
        } 
		
	}
	
}
