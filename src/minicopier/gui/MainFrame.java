/*
  MainFrame.java / MiniCopier
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

package minicopier.gui;

import minicopier.i18n.Language;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import minicopier.Configuration;
import minicopier.Copier;


public class MainFrame extends JFrame{
	
	public static MainFrame frame;
	
	protected Copier copier;
	
	private SupPanel supPanel;
	
	private TransfersPanel transfersPanel;
	
	private FailedPanel failedPanel;
	
	private OptionsPanel optionsPanel;
	

	public MainFrame(Copier cop)  {

		super();
		
		frame = this;
		
		DialogMsg.init();
		
		//Look And Feel
		
		if (Configuration.lookAndFeel.equals("pgs")){
			try {
				UIManager.setLookAndFeel("com.pagosoft.plaf.PgsLookAndFeel");
			} catch(Exception e) {}
		}
		
		//Classic swing
		else {
			UIManager.put("swing.boldMetal", Boolean.FALSE);
			if (Configuration.lookAndFeel.equals("native")) {
			try {
				   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				   //SwingUtilities.updateComponentTreeUI(this);
				} catch (Exception e) {} 
			}
		}
		this.copier = cop;
		copier.setGui(this);
		
		this.setTitle(copier.name);
		this.setSize(470, 400);
		//this.setAlwaysOnTop(Configuration.alwaysOnTop);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setIconImage(imageFromJar("img/icon.jpg"));
		//this.setUndecorated(true);
		
		this.supPanel = new SupPanel(this);
		
		this.transfersPanel = new TransfersPanel(this);
		
		this.failedPanel = new FailedPanel(this);
		
		this.optionsPanel = new OptionsPanel(this);
		

		
		//mainPanel includes a JPanel (top) and a JTabbedPane
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		mainPanel.add(this.supPanel);
		
		// Linking the Panel to the controllers
		RequestPauseResume actList = new RequestPauseResume();
		ActionSkip skipList = new ActionSkip();
		ActionCancel cancelList = new ActionCancel();
		ActionCredits creditsList = new ActionCredits();
		ActionBasket basketList = new ActionBasket();
		
		supPanel.basket.addMouseListener(basketList);
		supPanel.pause.addActionListener(actList);
		supPanel.skip.addActionListener(skipList);
		supPanel.cancel.addActionListener(cancelList);
		
		DropTargetListener dropL = new Drop2Basket();
		DropTargetListener dropFL = new Drop2Folder();
		DropTarget dropT = new DropTarget(this.supPanel.basket, dropL);
		DropTarget dropFT = new DropTarget(this.supPanel.paste, dropFL);
	
		
		
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//Adding the transfers list to the first tab
		tabbedPane.addTab(Language.get("MainFrame.TransfersPane.title"), new ImageIcon(imageFromJar("img/list.png")) ,transfersPanel);
		//Linking the Panel to the controllers
        ActionQueueButtons queueButtonsListener = new ActionQueueButtons();
        transfersPanel.putFirst.addActionListener(queueButtonsListener);
        transfersPanel.putUp.addActionListener(queueButtonsListener);
        transfersPanel.putDown.addActionListener(queueButtonsListener);
        transfersPanel.putLast.addActionListener(queueButtonsListener);
        transfersPanel.delete.addActionListener(queueButtonsListener);
		
        tabbedPane.addTab(Language.get("MainFrame.FailedPane.title"), new ImageIcon(imageFromJar("img/failed.gif")) ,failedPanel);
        //Linking the Panel to the controllers
        ActionFailedButtons failedButtonsListener = new ActionFailedButtons();
        failedPanel.retry.addActionListener(failedButtonsListener);
        failedPanel.removeFailed.addActionListener(failedButtonsListener);
        
        //Adding the options panel to the second tab
		tabbedPane.addTab(Language.get("MainFrame.OptionsPane.title"),new ImageIcon(imageFromJar("img/options.png")),optionsPanel);
		//Linking the Panel to the controllers
		ActionCollision collisionList = new ActionCollision();
		ActionSymbolic symbolicList = new ActionSymbolic();
		
		optionsPanel.collisionBox.addActionListener(collisionList);
		optionsPanel.symbolicBox.addActionListener(symbolicList);
		optionsPanel.credits.addActionListener(creditsList);
        optionsPanel.speedLimitBox.addActionListener(new ActionLimitSpeed());
        ActionSpeedChange speedLimitChange = new ActionSpeedChange();
        optionsPanel.speedValue.addChangeListener(speedLimitChange);
        optionsPanel.speedUnitBox.addActionListener(speedLimitChange);
		
		mainPanel.add(tabbedPane);
		
		
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
		
		
		RefreshStats refresh = new RefreshStats(500);
		refresh.start();	
		
	}
	
	public Image imageFromJar(String path){
		return Toolkit.getDefaultToolkit()
		.getImage(getClass().getClassLoader().getResource(path));
	}


	private class RequestPauseResume implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			// to synchro (?)
			if(copier.getPause()) { //currently paused
				copier.unpause();
			} else { //currently copying
				copier.pause();
			}
		}
	}

	
	private class Drop2Basket implements DropTargetListener {
		public void drop(DropTargetDropEvent dtde) {
			
			
			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
			
			try {
				
				Transferable tr = dtde.getTransferable();
			    DataFlavor[] flavors = tr.getTransferDataFlavors();
			      
			      for (int i = 0; i < flavors.length; i++) {
			    	  
			    	  DataFlavor fl = flavors[i];
			    	  
			    	  Object obj = tr.getTransferData(flavors[i]);
			    	  
			    	  
			    	  //For Windows and Mac
			    	  if (DataFlavor.javaFileListFlavor.equals(fl)) {
							List<File> files = (List<File>)tr.getTransferData(fl);
							
							for (File fi : files){
								System.out.println(fi.toURI().toString());
								copier.addURIString2basket(fi.toURI().toString());
							}
							
							dtde.dropComplete(true);
							basketSizeSignal();
							return;
			    	  }
			    	  
			    	  //For Linux and Mac
			    	  else if (obj instanceof String && "uri-list".equals(flavors[i].getSubType()) ){
			            	
			            	String allFilesPath;
			            	allFilesPath = (String)obj;
			            	
			            	Scanner scan = new Scanner(allFilesPath.trim());
			            	
			            	while (scan.hasNextLine()) {					
								copier.addURIString2basket(scan.nextLine());
							}
			            	
			            	dtde.dropComplete(true);
			    			basketSizeSignal();
			    			return;
			            	
			            }
			    	  
			      }
     
			} catch (Exception e) {
			      e.printStackTrace();
			      dtde.rejectDrop();
			    }
	    
		      dtde.rejectDrop();

		}
		
		public void dragExit(DropTargetEvent e){}
		public void dropActionChanged(DropTargetDragEvent e){}
		public void dragOver(DropTargetDragEvent e){}
		public void dragEnter(DropTargetDragEvent e){}
		
		
	}

	private class Drop2Folder implements DropTargetListener {
		public void drop(DropTargetDropEvent dtde) {
			
			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
			
			try {
				
				Transferable tr = dtde.getTransferable();
			    DataFlavor[] flavors = tr.getTransferDataFlavors();
			      
			      for (int i = 0; i < flavors.length; i++) {
			    	  
			    	  DataFlavor fl = flavors[i];
			    	  
			    	  Object obj = tr.getTransferData(flavors[i]);
			    	  
			    	  //For Windows and Mac
			    	  if (DataFlavor.javaFileListFlavor.equals(fl)) {
							List<File> files = (List<File>)tr.getTransferData(fl);
						
							Iterator<File> ite = files.iterator();
							
							if (files.size() == 1) {
								copier.paste2URIString(ite.next().toURI().toString());
							}
							
							dtde.dropComplete(true);
							basketSizeSignal();
							
							return;
							
			    	  }
			    	  
			    	  //For Linux and Mac
			    	  else if (obj instanceof String && "uri-list".equals(flavors[i].getSubType()) ){
			            	
			            	String allFilesPath;
			            	allFilesPath = (String)obj;
			            	
			            	Scanner scan = new Scanner(allFilesPath.trim());
			            	
			            	String destinationFolderURIString = scan.nextLine();

			            	if (!scan.hasNextLine()) {
			            		copier.paste2URIString(destinationFolderURIString);
			            	}
			            	
			            	dtde.dropComplete(true);
			    			basketSizeSignal();
			            	
			    			return;
			    			
			            }
			    	  
			      }
     
			} catch (Exception e) {
			      e.printStackTrace();
			      dtde.rejectDrop();
			    }
	    
			dtde.rejectDrop();
			
		}
		
		public void dragExit(DropTargetEvent e){}
		public void dropActionChanged(DropTargetDragEvent e){}
		public void dragOver(DropTargetDragEvent e){}
		public void dragEnter(DropTargetDragEvent e){}
		
	}
	
	public void changeCurrentFileSignal() {
		supPanel.currentFileSource.setText(this.copier.getCurrentFileSource());
		supPanel.currentFileDestination.setText(this.copier.getCurrentFileDestination());
	}
	
	public void changePauseStateSignal(boolean b) {
		if (b) { //Copier has been paused
			setTitle(Language.get("MainFrame.paused")+" " + getTitle());
			supPanel.pause.setText(Language.get("MainFrame.SupPanel.ResumeButton"));
		} else { //Copier has been resumed
			//setTitle(Copier.name);
			supPanel.pause.setText(Language.get("MainFrame.SupPanel.PauseButton"));
		}
	}
	
	public void totalSizeSignal() {
		String totalSize = DialogMsg.prettySize(copier.getQueueTotalSize());
		supPanel.barTotalSize.setText(totalSize);
	}
	
	public void currentSizeSignal() {
		String currentSize = DialogMsg.prettySize(copier.getFileTotalSize());
		supPanel.barCurrentSize.setText(currentSize);
	}
	
	public void basketSizeSignal() {
		supPanel.basketContent.setText(Language.get("MainFrame.SupPanel.BasketContent")+" "+copier.basketLength());
	}
	

	
	
	private class RefreshStats extends Thread {
		
		//Delay between two displays (ms)
		private int refreshRate;
		
		public RefreshStats(int l){
			this.refreshRate = l;
		}
		
		public void setRefreshRate(int l){
			this.refreshRate = l;
		}
		
		public void run() {
			Vector<Long> historyBytes = new Vector<Long>();
			int maxSize = 20;
			
			while (true) {
				long timeInit = System.currentTimeMillis();
				if (!copier.getPause()){
					historyBytes.add(0,copier.readAndInitBytesCounter());
					if (historyBytes.size() > maxSize) historyBytes.removeElementAt(historyBytes.size()-1);
					double average = 0;
					for (long l : historyBytes){
						average += l;
					}
					try{
					average /= (historyBytes.size()*refreshRate/1000f);
					//System.out.println(DialogMsg.prettySize((long)average));
					
					String totalETA = "%";
					String currentETA = "%";
					String currentSpeed = "";
					if (average > 0){
						if (copier.getTotalBytesRemaining()>0) {
							totalETA += " - "+DialogMsg.prettyTime((long)(copier.getTotalBytesRemaining()/average));
							currentSpeed = " ("+DialogMsg.prettySpeed((long)average)+")";
						}
						if (copier.getCurrentBytesRemaining()>0)
							currentETA += " - "+DialogMsg.prettyTime((long)(copier.getCurrentBytesRemaining()/average));
					}
					supPanel.mainBar.setValue(copier.getTotalPercent());
					supPanel.fileBar.setValue(copier.getCurrentPercent());
					supPanel.mainBar.setString(copier.getTotalPercent()+totalETA);				
					supPanel.fileBar.setString(copier.getCurrentPercent()+currentETA);
					setTitle(copier.getTotalPercent()+"%"+currentSpeed+" MiniCopier");
					} catch (Exception e){e.printStackTrace();}
				}
				try {Thread.sleep(refreshRate-(int)(System.currentTimeMillis()-timeInit));}
				catch (Exception e){}

			}
		}
	}
	
	private class ActionQueueButtons implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			synchronized (Copier.mainQueue) {
				int[] i = transfersPanel.transferList.getSelectedRows();
				int[] newSelectedIndexes = {-1,-1};
				
				
				if (i.length == 0) { return; } //something must be selected
				
				else if (e.getSource() == transfersPanel.putFirst){
					newSelectedIndexes = Copier.mainQueue.putFirst(i);
					
				}
				else if (e.getSource() == transfersPanel.putUp){
					newSelectedIndexes = Copier.mainQueue.putUp(i);
				}
				else if (e.getSource() == transfersPanel.putDown){
					newSelectedIndexes = Copier.mainQueue.putDown(i);
				}
				else if (e.getSource() == transfersPanel.putLast){
					newSelectedIndexes = Copier.mainQueue.putLast(i);
				}
				else if (e.getSource() == transfersPanel.delete){
					newSelectedIndexes = Copier.mainQueue.remove(i);
				}
				else {}
				
				transfersPanel.transferList.clearSelection();
				
				if (newSelectedIndexes[1] != -1) {
					transfersPanel.transferList.getSelectionModel().
					addSelectionInterval(newSelectedIndexes[0],
							newSelectedIndexes[1]);
				} 
			}
		}
	}
	
	private class ActionFailedButtons implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			synchronized (Copier.failedItems) {
				int[] i = failedPanel.failedList.getSelectedRows();				
				
				if (i.length == 0) { return; } //something must be selected
				
				else if (e.getSource() == failedPanel.retry){
					Copier.failedItems.retry(i);
				}
				else if (e.getSource() == failedPanel.removeFailed){
					Copier.failedItems.remove(i);
				}
				
				transfersPanel.transferList.clearSelection();
			}
		}
	}
	
	
	private class ActionSkip implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			if (copier.getBusy()) {
				copier.pause();
				int choice = DialogMsg.confirmSkip();
					
				switch (choice) {
					//Skip with put in queue
					case 0: copier.skip(true);
					break;
					//Skip without put in queue
					case 1: copier.skip(false);
					break;
					//Cancel skip
					case 2 : break;
				}
				
				copier.unpause();
				
			}

		}
		
	}
	
	private class ActionCancel implements ActionListener {
		public void actionPerformed(ActionEvent e){
			//violent termination of the application
			// TODO : remove the part already copied of the 
			//        transfer (?)
			System.exit(0);
		}
	}
	
	private class ActionCredits implements ActionListener {
		public void actionPerformed(ActionEvent e){
			//DialogMsg.displayCredits();
			new Credits(MainFrame.frame);
		}
	}
	
	private class ActionBasket implements MouseListener {
		public void mousePressed(MouseEvent e){
			//User clicked on the basket icon
			boolean want2Clear = DialogMsg.displayBasketInstructions(copier.basketLength()==0); 
			if (want2Clear) {
				copier.clearBasket();
				basketSizeSignal();
			}
			
		}
		public void mouseReleased(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseClicked(MouseEvent e){}
	}
	
	private class ActionCollision implements ActionListener {
		public void actionPerformed(ActionEvent e) {
	        int i = optionsPanel.collisionBox.getSelectedIndex();
	        Configuration.collisionAction = i;
	    }
	}
    
    private class ActionLimitSpeed implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            boolean limitEnabled = optionsPanel.speedLimitBox.isSelected();
            optionsPanel.speedValue.setEnabled(limitEnabled);
            optionsPanel.speedUnitBox.setEnabled(limitEnabled);
            Configuration.limitSpeed = limitEnabled;
            updateSpeed();
        }
    }
    
    private class ActionSpeedChange implements ChangeListener, ActionListener {
        public void stateChanged(ChangeEvent e) {
        	updateSpeed();
        }

		public void actionPerformed(ActionEvent e) {
			updateSpeed();
		}
    }
    
    private void updateSpeed(){
        Configuration.setSpeedValue((int)Math.pow(1024, optionsPanel.speedUnitBox.getSelectedIndex())*(Integer)optionsPanel.speedValue.getValue());
        //System.out.println(Configuration.nbBytesPerS);
    }
	
	private class ActionSymbolic implements ActionListener {
		public void actionPerformed(ActionEvent e) {
	        int i = optionsPanel.symbolicBox.getSelectedIndex();
	        Configuration.symbolicLinkAction = i;
	    }
	}
	


}
