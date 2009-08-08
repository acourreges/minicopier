/*
  FailedPanel.java / MiniCopier
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import minicopier.i18n.Language;

public class FailedPanel extends JPanel {

	
private MainFrame mainFrame;
	
	protected QueueJButton retry;
	
	protected QueueJButton removeFailed;
	
	protected DefaultTableModel failedModel;
	
	protected JTable failedList;
	
	public FailedPanel(MainFrame f){
		super();
		
		this.mainFrame = f;
		
		this.retry = new QueueJButton("img/retry.gif");
		this.retry.setToolTipText(Language.get("Tooltip.Failed.Retry"));
		
		
		this.removeFailed = new QueueJButton("img/delete.gif");
		this.removeFailed.setToolTipText(Language.get("Tooltip.Failed.Clear"));
		
		failedModel = mainFrame.copier.failedItems.getTableModel();
		failedList = new JTable(failedModel);
		//transferList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		failedList.getColumnModel().getColumn(1).setCellRenderer(new RightTableCellRenderer());
		failedList.getColumnModel().getColumn(1).setMaxWidth(80);
		failedList.getColumnModel().getColumn(1).setMinWidth(80);
		
		

		this.setLayout(new BorderLayout());
		

		JScrollPane jspFailed = new JScrollPane(failedList);
		jspFailed.setViewportView(failedList);
		jspFailed.setPreferredSize(new Dimension(40,40));
		//transferList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		//jspTransfer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

			
			//Subpanel (west) with queue management buttons
			JPanel queueButtonsPanel = new JPanel();
			queueButtonsPanel.setLayout(new BoxLayout(queueButtonsPanel, BoxLayout.Y_AXIS));
			queueButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			queueButtonsPanel.add(this.retry,Component.CENTER_ALIGNMENT);
			queueButtonsPanel.add(this.removeFailed);

			
		this.add(queueButtonsPanel,BorderLayout.WEST);
		this.add(jspFailed,BorderLayout.CENTER);
		
		
	}
	
	private class RightTableCellRenderer extends DefaultTableCellRenderer {
	    public RightTableCellRenderer() {
	        setHorizontalAlignment(RIGHT);
	        setVerticalAlignment(CENTER);
	    }
	}
}
