/*
 OptionsPanel.java / MiniCopier
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

import java.awt.Dimension;

import javax.swing.*;

import minicopier.Configuration;
import minicopier.i18n.Language;

public class OptionsPanel extends JPanel {

    private MainFrame mainFrame;

    protected JComboBox collisionBox;

    protected JComboBox symbolicBox;

    protected JCheckBox speedLimitBox;
    
    protected JSpinner speedValue;
    
    protected JComboBox speedUnitBox;

    protected JButton credits;

    public OptionsPanel(MainFrame f) {
        super();

        this.mainFrame = f;

        this.credits = new JButton(Language
                .get("MainFrame.OptionsPane.creditsButton"));

        String[] collisionStrings = {
                Language.get("MainFrame.OptionsPane.collision.Ask"),
                Language.get("MainFrame.OptionsPane.collision.Cancel"),
                Language.get("MainFrame.OptionsPane.collision.Overwrite"),
                Language.get("MainFrame.OptionsPane.collision.OverwriteOld"),
                Language.get("MainFrame.OptionsPane.collision.Resume") };
        collisionBox = new JComboBox(collisionStrings);
        // DefaultListCellRenderer rightRenderer = new
        // DefaultListCellRenderer();
        // rightRenderer.setHorizontalAlignment(DefaultListCellRenderer.RIGHT);
        // collisionBox.setRenderer(rightRenderer);
        collisionBox.setSelectedIndex(Configuration.collisionAction);
        
        this.speedLimitBox = new JCheckBox(Language.get("MainFrame.OptionsPane.throughput.Sentence"));
        this.speedLimitBox.setSelected(false);
        SpinnerModel speedModel = new SpinnerNumberModel(1,1,9999,1);
        this.speedValue = new JSpinner(speedModel);
        this.speedValue.setEnabled(false);
        String[] unitValues = {
        		Language.get("DialogMsg.byteps"),
				Language.get("DialogMsg.kilobyteps"),
				Language.get("DialogMsg.megabyteps"),
				Language.get("DialogMsg.gigabyteps")};
        speedUnitBox = new JComboBox(unitValues);
        speedUnitBox.setSelectedIndex(2);
        speedUnitBox.setEnabled(false);

        String[] symbolicStrings = {
                Language.get("MainFrame.OptionsPane.symLinks.Follow"),
                Language.get("MainFrame.OptionsPane.symLinks.Ignore") };
        symbolicBox = new JComboBox(symbolicStrings);

        symbolicBox.setSelectedIndex(Configuration.symbolicLinkAction);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Collision option
        JPanel collisionPanel = new JPanel();
        collisionPanel.setLayout(new BoxLayout(collisionPanel, BoxLayout.X_AXIS));
        collisionPanel.add(new JLabel(Language.get("MainFrame.OptionsPane.collision.Sentence")));

        collisionPanel.add(Box.createHorizontalGlue());
        collisionBox.setMaximumSize(collisionBox.getPreferredSize());
        collisionPanel.add(collisionBox);
        collisionPanel.setBorder(BorderFactory.createTitledBorder(Language.get("MainFrame.OptionsPane.collision.Title")));

        // Speed option
        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.X_AXIS));

        
        speedPanel.add(this.speedLimitBox);
        speedLimitBox.setMaximumSize(speedLimitBox.getPreferredSize());
        speedPanel.add(Box.createHorizontalGlue());
        speedPanel.add(this.speedValue);
        speedValue.setMaximumSize(new Dimension(
        		speedValue.getMinimumSize().width,
        		speedUnitBox.getMinimumSize().height)
        );
        speedPanel.add(Box.createRigidArea(new Dimension(5,5)));
        speedPanel.add(speedUnitBox);
        speedUnitBox.setMaximumSize(speedUnitBox.getMinimumSize());
        speedPanel.setBorder(BorderFactory.createTitledBorder(Language.get("MainFrame.OptionsPane.throughput.Title")));
        


        
        // Symbolic link option
        JPanel symbolicPanel = new JPanel();
        symbolicPanel.setLayout(new BoxLayout(symbolicPanel, BoxLayout.X_AXIS));
        symbolicPanel.add(new JLabel(Language
                .get("MainFrame.OptionsPane.symLinks.Sentence")));
        symbolicPanel.add(Box.createHorizontalGlue());

        symbolicBox.setMaximumSize(symbolicBox.getPreferredSize());
        // symbolicBox.setMinimumSize(collisionBox.getPreferredSize());

        symbolicPanel.add(symbolicBox);
        symbolicPanel.setBorder(BorderFactory.createTitledBorder(Language
                .get("MainFrame.OptionsPane.symLinks.Title")));

        JPanel optionsBottom = new JPanel();
        optionsBottom.setLayout(new BoxLayout(optionsBottom, BoxLayout.X_AXIS));
        optionsBottom.add(this.credits);
        optionsBottom.add(Box.createHorizontalGlue());

        this.add(collisionPanel);
        this.add(speedPanel);
        this.add(symbolicPanel);
        this.add(Box.createVerticalGlue());
        this.add(optionsBottom);
    }

}
