/*
  SupPanel.java / MiniCopier
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

import java.awt.Component;

import javax.swing.*;

import minicopier.i18n.Language;


//Panel with DnD pictures + transfer stats
public class SupPanel extends JPanel {
	
	private MainFrame mainFrame;
	
	protected JButton pause;

	protected JButton skip;
	
	protected JButton cancel;
	
	protected JLabel basket;
	
	protected JLabel basketContent;
	
	protected JLabel paste;
	
	protected JLabel barTotalSize;
	
	protected JLabel barCurrentSize;	
	
	protected JLabel currentFileSource;
	
	protected JLabel currentFileDestination;
	
	protected JProgressBar mainBar;
	
	protected JProgressBar fileBar;
	
	public SupPanel(MainFrame f) {
		
		super();
		
		this.mainFrame = f;
		
		this.pause = new JButton(Language.get("MainFrame.SupPanel.PauseButton"));
		this.skip = new JButton(Language.get("MainFrame.SupPanel.SkipButton"));
		this.cancel = new JButton(Language.get("MainFrame.SupPanel.CancelButton"));
		this.basket = new JLabel("");
		this.basket.setIcon(new ImageIcon(mainFrame.imageFromJar("img/basket.png")));
		this.basketContent = new JLabel(Language.get("MainFrame.SupPanel.BasketContent")+" 0");
		this.paste = new JLabel("");
		this.paste.setIcon(new ImageIcon(mainFrame.imageFromJar("img/paste.png")));
		this.barTotalSize = new JLabel("");
		this.barCurrentSize = new JLabel("");
		this.currentFileSource = new JLabel(mainFrame.copier.getCurrentFileSource());
		this.currentFileDestination = new JLabel(mainFrame.copier.getCurrentFileDestination());
		
		//Tooltips
		this.basket.setToolTipText(Language.get("Tooltip.Basket"));
		this.paste.setToolTipText(Language.get("Tooltip.Paste"));
		
		this.mainBar = new JProgressBar(0, 100);
        mainBar.setValue(0);
        mainBar.setStringPainted(true);
        
        this.fileBar = new JProgressBar(0, 100);
        fileBar.setValue(0);
        fileBar.setStringPainted(true);

		
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(Component.CENTER_ALIGNMENT);
        
		JPanel firstPanel = new JPanel();
		
		firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.X_AXIS));
		firstPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
			Box basketPanel = Box.createVerticalBox();
			
			//basketPanel.setLayout(new BoxLayout(basketPanel,BoxLayout.Y_AXIS));
			basketPanel.add(basket);
			basketPanel.add(basketContent,Component.RIGHT_ALIGNMENT);
			
			basketPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		firstPanel.add(basketPanel,Component.RIGHT_ALIGNMENT);
		
			//Sub JPanel with stats
			JPanel subJPanel1 = new JPanel();
			subJPanel1.setLayout(new BoxLayout(subJPanel1, BoxLayout.Y_AXIS));
				
				//Above Total progress bar
				JPanel aboveTotalBar = new JPanel();
				aboveTotalBar.setLayout(new BoxLayout(aboveTotalBar,BoxLayout.X_AXIS));
				aboveTotalBar.add(new JLabel(Language.get("MainFrame.SupPanel.TotalBarTitle")));
				aboveTotalBar.add(Box.createHorizontalGlue());
				aboveTotalBar.add(this.barTotalSize);
				
			subJPanel1.add(aboveTotalBar);
			subJPanel1.add(mainBar);
			
				//Above Current progress bar
				JPanel aboveCurrentBar = new JPanel();
				aboveCurrentBar.setLayout(new BoxLayout(aboveCurrentBar,BoxLayout.X_AXIS));
				aboveCurrentBar.add(new JLabel(Language.get("MainFrame.SupPanel.CurrentFileBarTitle")));
				aboveCurrentBar.add(Box.createHorizontalGlue());
				aboveCurrentBar.add(this.barCurrentSize);
				
				
			subJPanel1.add(aboveCurrentBar);
			subJPanel1.add(fileBar);
		
		firstPanel.add(subJPanel1);
		firstPanel.add(paste);
		this.add(firstPanel);
		
		//Panel with control buttons
		JPanel secondPanel = new JPanel();
		secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.X_AXIS));
		//secondPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		secondPanel.add(pause);
		secondPanel.add(skip);
		secondPanel.add(cancel);
		
		this.add(secondPanel);
		
		//Panel of files strings
		JPanel stringPanel = new JPanel();
		stringPanel.setLayout(new BoxLayout(stringPanel, BoxLayout.X_AXIS));
		
		
			//Subpanel "From:" and "To:"
			JPanel ftPanel = new JPanel();
			ftPanel.setLayout(new BoxLayout(ftPanel, BoxLayout.Y_AXIS));
			ftPanel.add(new JLabel(Language.get("MainFrame.SupPanel.SourcePath")+" "));
			ftPanel.add(new JLabel(Language.get("MainFrame.SupPanel.DestinationPath")+" "));
			
			
			//Subpanel with "source path" and "destination file"
			JPanel sourceDestPanel = new JPanel();
			sourceDestPanel.setLayout(new BoxLayout(sourceDestPanel, BoxLayout.Y_AXIS));
			sourceDestPanel.add(this.currentFileSource);
			sourceDestPanel.add(this.currentFileDestination);
			
		stringPanel.add(ftPanel);
		stringPanel.add(sourceDestPanel);
		stringPanel.add(Box.createVerticalStrut(15));
		this.add(stringPanel);
	}


}
