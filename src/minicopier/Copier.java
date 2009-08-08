/*
  Copier.java / MiniCopier
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
import java.net.*;
import java.util.*;

import minicopier.gui.DialogMsg;
import minicopier.gui.MainFrame;
import minicopier.i18n.Language;

public class Copier {
	
	//Application name and version
	public static String name = "MiniCopier";
	public static String version = "0.5";
	
	//The basket contains string paths to the files added by a user
	//for a copy. A 'paste' will empty the basket by transferring items
	//to the main queue.
	private Basket basket;
	
	//The path of the file being copied
	private String currentFileSource;
	
	//The path of the destination file
	private String currentFileDestination;
	
	//True if the Copier is working on a copy
	private Boolean busy;
	
	//True if the Copier is paused
	private Boolean paused;
	
	//The main queue contains the remaining file-only transfers to be done.
	public static MainQueue mainQueue;
	
	//The failed items
	public static FailedQueue failedItems;
	
	//Thread copying a file
	private TransferTask copyOp;
	
	//Transfer sizes sum
	private long queueTotalSize;
	
	//Bytes already copied
	private long queueCurrentSize;
	
	//Current treated file total size
	private long fileTotalSize;
	
	//Bytes already copied for current file
	private long fileCurrentSize;
	
	//GUI of the copier
	private MainFrame gui;
	
	private long bytesCounter;
	
	
	
	/* Create a new Copier */
	public Copier() {
		this.basket = new Basket();
		this.mainQueue = new MainQueue(this);
		this.failedItems = new FailedQueue(this);
		this.busy = false;
		this.paused = false;
		this.copyOp = null;
		this.currentFileSource = "";
		this.currentFileDestination = "";
		this.queueTotalSize = 0;
		this.queueCurrentSize = 0;
		this.fileTotalSize = 0;
		this.fileCurrentSize = 0;
		this.gui = null;
		this.bytesCounter = 0;
	}
	
	
	/* Add a file/folder path to the basket
	 * @param path path of the file or folder to be copied
	 */
	public void add2basket (String path) {
		this.basket.add(path);
	}
	
	public String getCurrentFileSource() {
		return this.currentFileSource;
	}
	
	public String getCurrentFileDestination() {
		return this.currentFileDestination;
	}
	
	private void setCurrentFileStrings(String sourcePath, String destPath) {
		this.currentFileSource = sourcePath;
		this.currentFileDestination = destPath;
		this.gui.changeCurrentFileSignal();
	}
	
	public void setGui(MainFrame view){
		this.gui = view;
	}
	
	public void setPause(boolean b){
		this.paused = b;
		this.gui.changePauseStateSignal(b);
	}
	
	public boolean getPause(){
		return this.paused;
	}
	
	public boolean getBusy(){
		return this.busy;
	}
	
	
	public void skip(boolean b){
		this.copyOp.setCancelAndQueue(b);
	}
	
	public void addURIString2basket (String uriString) {
		String file2add = this.stringURI2StringPath(uriString);
		if (file2add != null) {
			this.add2basket(file2add);
		}
	}
	
	public void paste2URIString(String uriString) {
		String folder2Paste = this.stringURI2StringPath(uriString);
		if (folder2Paste != null) {
			this.paste(folder2Paste);
		}
	}
	
	public String stringURI2StringPath (String uriString) {
		URI uri = null;
		try{
			uri = new URI(uriString);
		} catch (URISyntaxException e){
			System.err.println(uriString+" is not a valid URI.");
		}
		return (uri.getPath());
	}
	/* Add a FileToTransfer to the main queue
	 * @param transfer The file to be put into main queue
	 */
	public void addFile2Queue(FileToTransfer file){
	
		this.mainQueue.addFile(file);
		//System.out.println("Transfer\n"+  file.getSourcePath() + "\nto folder " 
		//		+ file.getDestinationFilePath() + "\nadded in queue. ");

	}
	
	/* Add the items of the folder in the queue */
	public void addFolder2Queue(File parentFolder, String destinationFolder) {
		
		
		//Listing directory content
		File[] dirlist = parentFolder.listFiles();
		
		File currentItem;
		FileToTransfer transfer;
		String newDestinationPath = "";
		
        if (dirlist != null){ //input item is really a folder
        	
            for ( int i = 0; i < dirlist.length; i++) {
            	//Dealing with directory content
            	
            	currentItem = dirlist[i];
            	

            	if (currentItem.isDirectory()) {
            		
            		newDestinationPath = destinationFolder + 
    				File.separator + parentFolder.getName();
            		//Recursive call on subfolder
                    addFolder2Queue(currentItem, newDestinationPath);
                    
            	} else { //item is a file
            		
            		newDestinationPath = destinationFolder + 
    				File.separator + parentFolder.getName() ;
            		
            		transfer = new FileToTransfer(currentItem.getPath(),
            				newDestinationPath);
            		this.addFile2Queue(transfer);
            	}
            } 
        } else {
        	System.err.println("Error reading directory : "+parentFolder);
        }
	} 

		
	
	/** Transforms basket items into FileToTransfer (even directories
	 * content) and transfer them to the main queue.
	 * @param destinationPath The path to the directory in which basket
	 * items must be copied. 
	 */
	
	public void paste(String destinationPath) {
		
		System.out.println("Paste requested to : " + destinationPath);
		File destination = new File(destinationPath);
		if (!destination.isDirectory()) {return;}
		
		//If it is a new paste, we restart the queue stats
		if (!busy) {
			this.queueTotalSize = 0;
			this.queueCurrentSize = 0;
		}
		
		Basket basketBackup = this.basket ;
		this.basket = new Basket();
		Iterator<String> basketContent = basketBackup.getIterator();
		
		String filePath;
		
		File item2add;
		
		while (basketContent.hasNext()) {
			filePath = basketContent.next();
			
			item2add = new File(filePath);
			
			if ((Configuration.symbolicLinkAction == 0) || isNotLink(item2add)) {
				if (!item2add.isDirectory()) { //if the item is a file
					
					System.out.println("adding file to main queue");
					addFile2Queue(new FileToTransfer(filePath,destinationPath));
					
				} else { //the item is a folder
					//System.out.println("adding folder to main queue");
					this.addFolder2Queue(item2add,destinationPath);
				}
			}
		}

		//We launch the treatment anyway, it will exit by
		//itself if necessary
		forceStart();
	}
	
	public void forceStart(){
		ForceTreatment t = new ForceTreatment();
		t.start();
	}
	
	public void treatQueue() {
		
		synchronized(this){
			if (busy) {return;} //other instance running...
			
			this.busy = true;
		}
		
		boolean proceed = true;
		boolean append = false;
		
		
		FileToTransfer transfer;
					
		while (!this.mainQueue.isEmpty()) {
		
			//Pick & extract first transfer in queue
			synchronized (mainQueue) {
			transfer = mainQueue.extractFirst();
			}
			
			this.setCurrentFileStrings(transfer.getSourcePath()
					,transfer.getDestinationFilePath());
			
			
			//default : we proceed to the copy without append
			proceed = true;
			append = false;
			
			//target already exists ?

			File destFile = transfer.getDestinationFile();
			

			
			if (destFile.length() > 0) { //target exists
				
				boolean acceptableAction = false;
				
				int actionToDo = Configuration.collisionAction;
				
				while (!acceptableAction) {
					
					long sourceSize = transfer.getSourceFile().length();
					long destSize = transfer.getDestinationFile().length();
				
					switch (actionToDo) {
					
					case (0) : //ask
						int choice = DialogMsg.alreadyExistsChoice(
								transfer.getSourcePath(),
								sourceSize,
								transfer.getDestinationFilePath(),
								destSize);
						
						if (choice==0) { //overwrite
							actionToDo = 2;
						}
					
						if (choice==1) { //resume copy
							actionToDo = 4;
						} 
						
						if (choice==2) { //Rename
							actionToDo = 5;
						}
						
						if (choice==3) { //cancel copy
							actionToDo = 1;						
						}
						
						break;
						
					case (1) : //ignore
						proceed = false;
						acceptableAction = true;
						break;
						
					case (2) : //overwrite
						
						//We just make sure the source path is different
						//from the destination path. 
						
						if (transfer.getSourcePath().equals(transfer.getDestinationFilePath())) {
							DialogMsg.errorOverwrite();
							acceptableAction = false;
							actionToDo = 0;
						} else {
							//nothing to do : append==false proceed==true
							acceptableAction = true;
						}
						
						break;
					
					case (3): //overwrite if older
						proceed = (transfer.getSourceFile().lastModified()
							> destFile.lastModified());
						acceptableAction = true;
						break;	
						
					case (4) : //resume
						//dest file might be a different file but with the same name
						//as source. User wanted to resume, so we resume anyway, but
						//only if the size of file permit it.
						append = (destFile.length() <= transfer.getSize());
						acceptableAction = true;
					
					
						break;
					
					case (5) : //rename
						
						String newName = DialogMsg.askNewName(transfer.getName());
					
						boolean leaveRename = (newName==null);
					
						while (newName != null 
								&& (newName.equals(transfer.getName()) || !nameIsAcceptable(newName,transfer.getDestinationFolderPath()))) { 
							//User confirms a new name
							DialogMsg.renameError();
							newName = DialogMsg.askNewName(transfer.getName());
				
						}
						
						if (newName != null) { 
							//The name is correct, we can change destination path
							transfer.changeTargetName(newName);
							
							//The action will be acceptable if the file doesn't already
							//exists, else we ask what to do. 
							acceptableAction = !(transfer.getDestinationFile().exists());
						} else { //The user has canceled renaming
							acceptableAction = false;
						}
						
						actionToDo = 0; //we ask for a new choice
						
						
						break;
						
					default : break;
						
					}
				
				}
				
				
			}
			
			if (proceed) { //So we must proceed to the copy
				
				this.setCurrentFileStrings(transfer.getSourcePath()
						,transfer.getDestinationFilePath());
			
				//System.out.println("Treating " + transfer.getSourcePath());
				//System.out.println("-> " + transfer.getDestinationFilePath());
				
				//Creation of thread
				this.copyOp = new TransferTask(this,transfer,append);
				
				//System.out.println("Beginning transfer...");
				copyOp.start();
				
				//Waiting for transfer to finish
				try {
					copyOp.join();
				}
				catch (Exception e) {
					System.out.println("Copy Thread Error " + e);
				}
				
				//System.out.println("Copy thread terminated");
				//Transfer has ended (maybe not completed)
				if (!copyOp.getCompleted()) { //incomplete transfer
					System.out.println("Transfer NOT completed");
					
					//Has it been canceled by a skip action ?
					if (copyOp.getCancel()) {
						System.out.println("(has been skipped)");
						if (copyOp.getPutInQueue()) { //user wants to resume later
							mainQueue.addFile(transfer);
						} else { //user does not want to resume later 
							//we add the transfer to the "failed items" list
							this.failedItems.addFile(transfer);							
						}
							
					} else { //it's an IO exception
						System.out.println("(error occured)");
						boolean ioPutInQueue = DialogMsg.transferErrorChoice();
						if (ioPutInQueue) {
							//user wants to resume later
							mainQueue.addFile(transfer);
						} else {
							//transfer has failed
							this.failedItems.addFile(transfer);
						}
	
					}
						
					
					this.decreaseQueueTotalSize(this.fileTotalSize);
					this.decreaseQueueCurrentSize(this.fileCurrentSize);
					this.setFileCurrentSize(0);
					
				}
				this.copyOp = null;
			
			} else { //destination file already exists, and user canceled
				this.decreaseQueueTotalSize(transfer.getSize());
				this.setFileTotalSize(0);
				this.setFileCurrentSize(0);
				this.failedItems.addFile(transfer);
			}
		
		}
		
		//MainQueue SEEMS to be empty... 
		//Let's just check other transfers were not added after the test. 
		
		synchronized(this){
			busy=false;
			
			if (this.mainQueue.isEmpty()) { //empty, indeed
				this.setCurrentFileStrings("","");
				return;
				} else { //still some transfers remaining
					//we launch the treatment once again
					forceStart();
					return;
				}
		}
		
	}
	
	public void increaseQueueTotalSize(long nb) {
		this.queueTotalSize += nb;
		gui.totalSizeSignal();
	}
	
	public void decreaseQueueTotalSize(long nb){
		this.queueTotalSize -= nb;
		gui.totalSizeSignal();
	}
	
	public long readAndInitBytesCounter(){
		long result = this.bytesCounter;
		this.bytesCounter -= result;
		return result;
	}

	
	public void increaseQueueCurrentSize(long nb) {
		this.queueCurrentSize += nb;
		//For speed measures
		this.bytesCounter += nb;
	}
	
	public void decreaseQueueCurrentSize(long nb){
		this.queueCurrentSize -= nb;
	}
	
	public void setFileCurrentSize(long s){
		this.fileCurrentSize = s;
	}
	
	public void increaseFileCurrentSize(long nb) {
		this.fileCurrentSize += nb;
		increaseQueueCurrentSize(nb);
	}

	public void setFileTotalSize(long s) {
		this.fileTotalSize = s;
		gui.currentSizeSignal();
	}
	
	public long getTotalBytesRemaining(){
		return this.queueTotalSize - this.queueCurrentSize;
	}
	
	public long getCurrentBytesRemaining(){
		return this.fileTotalSize - this.fileCurrentSize;
	}
	
	public int getTotalPercent() {
		int result = 0;
		if (this.queueTotalSize != 0) {
			Double tmp = (((double)this.queueCurrentSize)/((double)this.queueTotalSize)*100);
			result = tmp.intValue();
		}
		return result;
	}
	
	public int getCurrentPercent() {
		int result = 0;
		if (this.fileTotalSize != 0) {
			Double tmp =((double)this.fileCurrentSize)/((double)this.fileTotalSize)*100;
			result = tmp.intValue();
		}
		return result;
	}
	
	public long getQueueTotalSize() {
		return this.queueTotalSize;
	}
	
	public long getFileTotalSize() {
		return this.fileTotalSize;
	}
	
	public boolean pause() {
		if (this.copyOp != null) {
			this.copyOp.setPause();
			return true;
		} else {
			return false;
		}
	}
	
	public void unpause() {
		if (this.copyOp != null) {
			this.copyOp.unpause();
		}
	}
	
	public int basketLength(){
		return this.basket.getLength();
	}
	
	public void clearBasket() {
		this.basket = new Basket();
	}
	
	
	/** Depending on platform, some characters in file's name are
	 * forbidden. So we just create a temp file and try to rename
	 * it to know if the name is accepted by the host OS. 
	 * @param fileName new name the user wants to use
	 * @param dirPath the destination directory path
	 */
	public boolean nameIsAcceptable(String fileName,String dirPath){
		
		boolean result = false;
		
		try {
			
			File temp = File.createTempFile("MiniCopier",".tmp");
			
			String newPath = temp.getParentFile().getAbsolutePath()+
			File.separator + fileName;
			
			File newFile = new File(newPath);
			
			if (temp.renameTo(newFile)) {
				result = true; //name is correct for the OS
			}
			
			temp.delete();
			newFile.delete();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Just making sure the new target isn't a directory
		if (result) {
			File dirCheck = new File(dirPath + File.separator + fileName);
			if (dirCheck.isDirectory()) result = false;
		}
		
		return result;
	}
	
	public static boolean isNotLink(File file)
	  {
	    try {
	      if (!file.exists())
		return true;
	      else
		{
		  String canonpath = file.getCanonicalPath();
		  //System.out.println(canonpath);
		  String abspath = file.getAbsolutePath();
		  //System.out.println(abspath);
		  return abspath.equals(canonpath);
		}
	    }
	    catch(IOException ex) {
	      System.err.println(ex);
	      return true;
	    }
	  }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Initialization
		Configuration conf = new Configuration();
		Language.init(Configuration.language);
		
		for (String arg : args) {
			//deprecated
			//if (arg.equals("-nolnf")) Configuration.nativeLnF = false;
			if (arg.equals("-nolnf")) System.out.println("-nolnf : Option no longer available! \n" +
					"Please edit \"configuration.ini\" to set the look and feel.");
		}
		
		Copier copier = new Copier();
		MainFrame frame = new MainFrame(copier);
		
		//Display a welcome dialog to the user for the first launch
		if (conf.wasFirstLaunch()) DialogMsg.displayWelcome();
		
		frame.setAlwaysOnTop(Configuration.alwaysOnTop);
		
	}

	
	private class ForceTreatment extends Thread {
		public void run() {
			treatQueue();
		}
	}
}


