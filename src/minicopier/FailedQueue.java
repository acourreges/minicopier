/*
  FailedQueue.java / MiniCopier
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

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import minicopier.gui.DialogMsg;
import minicopier.i18n.Language;

public class FailedQueue {
	
private Copier copier;
	
	private Vector<FileToTransfer> failedItems;
	private DefaultTableModel tableModel;


	public FailedQueue(Copier c) {
		this.copier = c;
		this.failedItems = new Vector<FileToTransfer>();
		
		String[] columnsNames = {Language.get("MainFrame.TransfersPane.sourceRow"),
				Language.get("MainFrame.TransfersPane.sizeRow"),
				Language.get("MainFrame.TransfersPane.destinationRow")};
		this.tableModel = new FailedModel(columnsNames,0);
	}
	
	public DefaultTableModel getTableModel() {
		return this.tableModel;
	}
	
	
	public synchronized boolean isEmpty(){
		return this.failedItems.isEmpty();
	}
	
	
	public synchronized void addFile(FileToTransfer file){
		this.failedItems.add(file);
		String[] data = {file.getSourcePath(),
				DialogMsg.prettySize(file.getSize()),
				file.getDestinationFolderPath()
		};
		this.tableModel.addRow(data);
	}
	
	public int[] remove(int i[]) {
		
		int[] result = {-1,-1};
		int length = i.length;
		
		int k = 0;
		int ind;
		int del = 0;
		
		for (k=0; k<=length-1; k++){
			ind = i[k] - del;
			this.failedItems.removeElementAt(ind);
			this.tableModel.removeRow(ind);
			del++;
		}
		return result;
	}
	
	public int[] retry(int i[]) {
		
		int[] result = {-1,-1};
		int length = i.length;
		
		int k = 0;
		int ind;
		int del = 0;
		
		for (k=0; k<=length-1; k++){
			ind = i[k] - del;
			copier.addFile2Queue(this.failedItems.get(ind));
			this.failedItems.removeElementAt(ind);
			this.tableModel.removeRow(ind);
			del++;
		}
		
		copier.forceStart();
		return result;
	}
	


	
	private class FailedModel extends DefaultTableModel{
		
		public FailedModel(Object[] s,int i){
			super(s,i);
		}
		
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

}
