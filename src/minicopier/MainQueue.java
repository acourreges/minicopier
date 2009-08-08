/*
  MainQueue.java / MiniCopier
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
import javax.swing.table.*;

import minicopier.gui.DialogMsg;

import minicopier.i18n.Language;

public class MainQueue {
	
	private Copier copier;
	
	private Vector<FileToTransfer> mainQueue;
	
	private DefaultTableModel tableModel;


	public MainQueue(Copier c) {
		this.copier = c;
		this.mainQueue = new Vector<FileToTransfer>();
		
		String[] columnsNames = {Language.get("MainFrame.TransfersPane.sourceRow"),
				Language.get("MainFrame.TransfersPane.sizeRow"),
				Language.get("MainFrame.TransfersPane.destinationRow")};
		this.tableModel = new TransfersModel(columnsNames,0);
	}

	public synchronized void addFile(FileToTransfer file){
		this.mainQueue.add(file);
		String[] data = {file.getSourcePath(),
				DialogMsg.prettySize(file.getSize()),
				file.getDestinationFolderPath()
		};
		this.tableModel.addRow(data);
		this.copier.increaseQueueTotalSize(file.getSize());
	}
	
	public DefaultTableModel getTableModel() {
		return this.tableModel;
	}
	
	/** Remove and send the first element in queue */
	public synchronized FileToTransfer extractFirst(){
		FileToTransfer first = this.mainQueue.firstElement();
		this.mainQueue.remove(0);
		this.tableModel.removeRow(0);
		return first;
	}

	public synchronized boolean isEmpty(){
		return this.mainQueue.isEmpty();
	}
	
	public int[] putFirst(int[] i){
		int[] result = {-1,-1};
		int length = i.length;
		int k = length-1;
		int ind;
		int del = 0;

		for (k=length-1; k>=0; k--){
			ind = i[k] + del;
			this.mainQueue.add(0,mainQueue.elementAt(ind));
			this.mainQueue.remove(ind+1);
			this.tableModel.moveRow(ind,ind,0);		
			del++;
		}
		result[0] = 0;
		result[1] = length-1;
		
		/*
		if (i < mainQueue.size() && i >= 0) {
			this.mainQueue.add(0,mainQueue.elementAt(i));
			this.tableModel.insertRow(0,(Vector)tableModel.getDataVector().elementAt(i));
			this.mainQueue.remove(i+1);
			this.tableModel.removeRow(i+1);
			result = 0;
		}
		//System.out.println("Sortie putFirst");
		 * 
		 */
		return result;
	}
	
	public int[] putLast(int i[]){
		
		int[] result = {-1,-1};
		int length = i.length;
		int k = 0;
		int ind;
		int del = 0;
		
		for (k=0; k<=length-1; k++){
			ind = i[k] - del;
			this.mainQueue.add(mainQueue.elementAt(ind));
			this.mainQueue.remove(ind);
			this.tableModel.addRow((Vector)tableModel.getDataVector().elementAt(ind));
			this.tableModel.removeRow(ind);	
			del++;
		}
		
		result[1] = tableModel.getRowCount()-1;
		result[0] = result[1]-(length-1);
		
		/*
		int result = -1;
		if (i < mainQueue.size() && i >= 0) {
			this.mainQueue.add(mainQueue.elementAt(i));
			this.tableModel.addRow((Vector)tableModel.getDataVector().elementAt(i));
			this.mainQueue.removeElementAt(i);
			this.tableModel.removeRow(i);
			result = (tableModel.getRowCount()-1);
		}
		*/
		
		return result;
	}
	
	public int[] putUp(int[] i) {
		
		int[] result = {-1,-1};
		int length = i.length;
		int start = i[0];
		
		int k = length-1;
		int ind;
		//we usually start inserting at "start-1" except when
		//start==0 (first element selected)
		int toInsert = (start == 0 ? 0 : start-1);
		int del = 0;
		
		for (k=length-1; k>=0; k--){
			ind = i[k] + del;
			this.mainQueue.add(toInsert,mainQueue.elementAt(ind));
			this.mainQueue.remove(ind+1);
			this.tableModel.moveRow(ind,ind,toInsert);		
			del++;
		}
		
		result[0]=toInsert;
		result[1]=toInsert+length-1;
		
		/*
		int result = -1;
		//we do it for all, except for the first one
		if (i < mainQueue.size() || i > 0) {
			FileToTransfer f = mainQueue.elementAt(i-1);
			mainQueue.setElementAt(mainQueue.elementAt(i),i-1);
			mainQueue.setElementAt(f,i);
			tableModel.moveRow(i,i,i-1);

			result = i-1;
		}
		*/
	
		return result;
	}
	
	public int[] putDown(int i[]) {
		
		int[] result = {-1,-1};
		int length = i.length;
		int last = i[length-1];
		
		int k = 0;
		int ind;
		
		//we usually start inserting at "last+2" except when
		//last==length-1 (last element selected)
		int toInsert = (last == mainQueue.size()-1 ? mainQueue.size() : last+2);
		int del =0;
		
		for (k=0; k<=length-1; k++){
			ind = i[k] - del;
			this.mainQueue.add(toInsert,mainQueue.elementAt(ind));
			this.mainQueue.remove(ind);
			this.tableModel.moveRow(ind,ind,toInsert-1);
			del++;
		}
		
		result[1] = toInsert-1;
		result[0] = result[1]-(length-1);

		
		/*
		int result = -1;
		//don't consider the last one
		if (i < (mainQueue.size()-1) || i >= 0) {
			FileToTransfer f = mainQueue.elementAt(i+1);
			mainQueue.setElementAt(mainQueue.elementAt(i),i+1);
			mainQueue.setElementAt(f,i);
			tableModel.moveRow(i,i,i+1);

			result = i+1;
		}*/
		
		return result;
	}
	
	public int[] remove(int i[]) {
		
		int[] result = {-1,-1};
		int length = i.length;
		
		int k = 0;
		int ind;
		int del = 0;
		
		for (k=0; k<=length-1; k++){
			ind = i[k] - del;
			this.copier.decreaseQueueTotalSize(mainQueue.elementAt(ind).getSize());
			this.mainQueue.removeElementAt(ind);
			this.tableModel.removeRow(ind);
			del++;
		}
		
		
		/*
		int result = -1;
		if (i < mainQueue.size() && i >= 0) {
			this.copier.decreaseQueueTotalSize(mainQueue.elementAt(i).getSize());
			this.mainQueue.removeElementAt(i);
			this.tableModel.removeRow(i);
			result = i;
		}*/
		
		
		return result;
	}
	
	public class TransfersModel extends DefaultTableModel{
		
		public TransfersModel(Object[] s,int i){
			super(s,i);
		}
		
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
	}
	
}
