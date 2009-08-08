/*
  FileToTransfer.java / MiniCopier
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

/** FileToTransfer represents a tranfer requested by the user. 
 * It contains all the informations about the source file, and where
 * it must be copied, according to the user.
 */

public class FileToTransfer {
	
	//Source file path
	private String sourcePath;

	//File's copy destination path (folder)
	private String destinationPath;
	
	//Destination File Path
	private String destinationFilePath;
	
	//Name of source file
	private String name;
	
	//Size of source file
	private long size;
	
	/** Creation of a FileToTransfer
	 * @param _path2source system path to the source file
	 * @param _path2destinationfolder system path to the destination folder
	 */
	public FileToTransfer(String _path2source, 
			String _path2destinationfolder) {
		
		//Using the argument Strings directly work, but the
		//display is not fine for Windows, so we create Files
		//and get the path (OS-dependent).

		//this.sourcePath = _path2source;
		
		File sourceFile = new File(_path2source);
		
		this.sourcePath = sourceFile.getPath();		
		this.name = sourceFile.getName();
		this.size = sourceFile.length();
		
		//this.destinationPath = _path2destinationfolder;
		
		
		File destFolder = new File(_path2destinationfolder);
		this.destinationPath = destFolder.getPath();
		
		this.destinationFilePath = this.destinationPath 
		+ File.separator + this.name;
			
		
	}
	
	public String getSourcePath() {
		return this.sourcePath;
	}
	
	public File getSourceFile() {
		return new File(this.sourcePath);
	}
	
	public String getDestinationFolderPath() {
		return this.destinationPath;
	}
	
	public File getDestinationFolder() {
		return new File(this.getDestinationFolderPath());
	}
	
	public String getDestinationFilePath() {
		return this.destinationFilePath;

	}
	
	public File getDestinationFile() {
		return new File(this.getDestinationFilePath());
	}
	
	public String getName() {
		return this.name;
	}
	
	public long getSize() {
		return this.size;
	}
	
	public void changeTargetName(String newName){
		this.destinationFilePath = this.destinationPath + File.separator +
			newName;
	}

}
