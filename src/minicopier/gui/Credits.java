/*
  Credits.java / MiniCopier
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import minicopier.i18n.Language;
import minicopier.Copier;

public class Credits extends JDialog {
	
	private JTextArea credits;
	
	private JTextArea license;
	
	private JButton ok;
	
	public Credits(JFrame owner){
		super(owner,true);
		
		this.setTitle(Language.get("Credits.Title"));
		this.setSize(360, 300);
		this.setLocationRelativeTo(null);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		mainPanel.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
		
		//Panel with logo and version number
		JPanel logoPanel = new JPanel();
		logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
		logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		JLabel logo = new JLabel("");
		logo.setIcon(new ImageIcon(MainFrame.frame.imageFromJar("img/minicopier-logo.png")));
		
		JLabel version = new JLabel(Language.get("Credits.Version")+" : "+Copier.version);
		logoPanel.add(logo);
		logoPanel.add(version);
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		version.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Tabbed pane with credits and license
		JTabbedPane tabbedPane = new JTabbedPane();
		
			//Credits Panel
			JPanel creditsPanel = new JPanel();
			creditsPanel.setLayout(new BorderLayout());
			
			this.credits = new JTextArea();
			credits.setEditable(false);
			credits.setWrapStyleWord(true);
			credits.setLineWrap(true);
			credits.setText(getCreditsText());
			credits.setCaretPosition(0);
			credits.setTabSize(1);
			
			JScrollPane jspCredits = new JScrollPane(credits);
			creditsPanel.add(jspCredits,BorderLayout.CENTER);
			
			//License Panel
			JPanel licensePanel = new JPanel();
			licensePanel.setLayout(new BorderLayout());
			
			this.license = new JTextArea();
			license.setEditable(false);
			license.setWrapStyleWord(true);
			license.setLineWrap(true);
			license.setText(getLicenseText());
			license.setCaretPosition(0);
			credits.setTabSize(1);
			
			JScrollPane jspLicense = new JScrollPane(license);
			licensePanel.add(jspLicense,BorderLayout.CENTER);
			
			
		tabbedPane.add(Language.get("Credits.Title"),creditsPanel);
		tabbedPane.add(Language.get("Credits.License.Title"),licensePanel);
		
		mainPanel.add(logoPanel);
		mainPanel.add(tabbedPane);
		
		this.ok = new JButton("OK");
		this.ok.addActionListener(new ActionOK());
		mainPanel.add(Box.createVerticalStrut(5));
		mainPanel.add(ok);
		ok.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getContentPane().add(mainPanel);
		this.setVisible(true);
	}
	
	public String getCreditsText() {
		StringBuffer credits = new StringBuffer();
		
		credits.append(Language.get("Credits.Credits.Homepage")+"\n");
		credits.append("http://www.adriancourreges.com/projects/minicopier\n\n");
		credits.append(Language.get("Credits.Credits.Development")+"\n");
		credits.append(Language.get("Credits.Credits.Developers")+"\n\n");
		credits.append(Language.get("Credits.Credits.LanguageTranslation")+"\n");
		credits.append(Language.get("Credits.Credits.LanguageContributors")+"\n\n");
		credits.append(Language.get("Credits.Credits.SendMail")+"\n");
		credits.append("a.courreges@gmail.com");
		
		return credits.toString();
	}
	
	public String getLicenseText() {
		StringBuffer license = new StringBuffer();
		
		license.append(Language.get("Credits.License.Intro")+"\n\n"); 
		license.append("Copyright (C) Adrian Courreges\n\n");
		license.append("This program is free software; you can redistribute it and/or modify " +
				"it under the terms of the GNU General Public License as published by " +
				"the Free Software Foundation; either version 2 of the License, or " +
				" (at your option) any later version.\n\n" +
				"This program is distributed in the hope that it will be useful, " +
				"but WITHOUT ANY WARRANTY; without even the implied warranty of " +
				"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
				"GNU General Public License for more details.\n\n" +
				"You should have received a copy of the GNU General Public License " +
				"along with this program; see the file COPYING. If not, write to the " +
				"Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.");
		
		return license.toString();
		
	}
	
	public void exit(){
		this.dispose();
	}

	private class ActionOK implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			exit();
	    }
	}

}
