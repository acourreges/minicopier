/*
  TransfersPanel.java / MiniCopier
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.*;

import minicopier.i18n.Language;

//Panel with transfer list
public class TransfersPanel extends JPanel {
	
	private MainFrame mainFrame;
	
	protected QueueJButton putFirst;
	
	protected QueueJButton putUp;
	
	protected QueueJButton putDown;
	
	protected QueueJButton putLast;
	
	protected QueueJButton delete;
	
	protected DefaultTableModel transferModel;
	
	protected JTable transferList;
	
	public TransfersPanel(MainFrame f){
		super();
		
		this.mainFrame = f;
		
		this.putFirst = new QueueJButton("img/first.gif");
		this.putFirst.setToolTipText(Language.get("Tooltip.Transfers.First"));
        
        this.putUp = new QueueJButton("img/up.gif");
        this.putUp.setToolTipText(Language.get("Tooltip.Transfers.Up"));
        this.putDown = new QueueJButton("img/down.gif");
        this.putDown.setToolTipText(Language.get("Tooltip.Transfers.Down"));
        this.putLast = new QueueJButton("img/last.gif");
        this.putLast.setToolTipText(Language.get("Tooltip.Transfers.Last"));
        this.delete = new QueueJButton("img/delete.gif");
        this.delete.setToolTipText(Language.get("Tooltip.Transfers.Cancel"));
		
		transferModel = mainFrame.copier.mainQueue.getTableModel();
		transferList = new JTable(transferModel);
		//transferList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transferList.getColumnModel().getColumn(1).setCellRenderer(new RightTableCellRenderer());
		transferList.getColumnModel().getColumn(1).setMaxWidth(80);
		transferList.getColumnModel().getColumn(1).setMinWidth(80);
		
		

		this.setLayout(new BorderLayout());
		

		JScrollPane jspTransfer = new JScrollPane(transferList);
		jspTransfer.setViewportView(transferList);
		jspTransfer.setPreferredSize(new Dimension(40,40));
		//transferList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		//jspTransfer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

			
			//Subpanel (west) with queue management buttons
			JPanel queueButtonsPanel = new JPanel();
			queueButtonsPanel.setLayout(new BoxLayout(queueButtonsPanel, BoxLayout.Y_AXIS));
			queueButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			queueButtonsPanel.add(this.putFirst,Component.CENTER_ALIGNMENT);
			queueButtonsPanel.add(this.putUp);
			queueButtonsPanel.add(this.putDown);
			queueButtonsPanel.add(this.putLast);
			queueButtonsPanel.add(this.delete);

			
		this.add(queueButtonsPanel,BorderLayout.WEST);
		this.add(jspTransfer,BorderLayout.CENTER);
		
		
	}

	public class RightTableCellRenderer extends DefaultTableCellRenderer {
	    public RightTableCellRenderer() {
	        setHorizontalAlignment(RIGHT);
	        setVerticalAlignment(CENTER);
	    }
	}
	


}
