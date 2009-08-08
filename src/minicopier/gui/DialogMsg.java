/*
  DialogMsg.java / MiniCopier
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
import javax.swing.JOptionPane;
import minicopier.i18n.Language;

public class DialogMsg {
	
	//byte
	private static String b;
	
	private static String kb; //kB
	
	private static String mb; //MB
	
	private static String gb; //GB
	
	private static String tb; //TB
	
	private static String bps, kbps, mbps, gbps, tbps;
	
	private static String day, hour, min, sec;
	
	public static void init() {
		b = Language.get("DialogMsg.byte");
		kb = Language.get("DialogMsg.kilobyte");
		mb = Language.get("DialogMsg.megabyte");
		gb = Language.get("DialogMsg.gigabyte");
		tb = Language.get("DialogMsg.terabyte");
		bps = Language.get("DialogMsg.byteps");
		kbps = Language.get("DialogMsg.kilobyteps");
		mbps = Language.get("DialogMsg.megabyteps");
		gbps = Language.get("DialogMsg.gigabyteps");
		tbps = Language.get("DialogMsg.terabyteps");
		day = "d";
		hour = "h";
		min = "m";
		sec = "s";
	}
	
	
	
	
	public static boolean displayBasketInstructions(boolean empty) {
		boolean clearBasket = false;
		if (empty) { //Basket is empty, we display use instructions
			JOptionPane.showMessageDialog(MainFrame.frame,
					Language.get("DialogMsg.displayBasketInstructions.EmptyText"),
					Language.get("DialogMsg.displayBasketInstructions.Title"),
					JOptionPane.INFORMATION_MESSAGE);
		} else { //Basket not empty, possibility to clear
			Object[] options = {
					Language.get("DialogMsg.displayBasketInstructions.ClearButton"),
					Language.get("DialogMsg.displayBasketInstructions.CancelButton")};
			int n = JOptionPane.showOptionDialog(MainFrame.frame,
			Language.get("DialogMsg.displayBasketInstructions.FullText"),
			Language.get("DialogMsg.displayBasketInstructions.Title"),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]);
			clearBasket = (n==0);
			
		}
		return clearBasket;
	}


	public static int confirmSkip(){
		Object[] options = {Language.get("DialogMsg.confirmSkip.QueueButton"),
				Language.get("DialogMsg.confirmSkip.NoButton"),
				Language.get("DialogMsg.confirmSkip.CancelButton")};
		int n = JOptionPane.showOptionDialog(MainFrame.frame,
				Language.get("DialogMsg.confirmSkip.Text"),
				Language.get("DialogMsg.confirmSkip.Title"),
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[2]);
		return n;
	}
	

	
	
	public static String askNewName(String oldName) {
		return JOptionPane.showInputDialog(MainFrame.frame,
	            Language.get("DialogMsg.askNewName.Text"),	
	            oldName);
	}
	
	public static void renameError(){
		JOptionPane.showMessageDialog(MainFrame.frame, 
				Language.get("DialogMsg.renameError.Text"),
				Language.get("DialogMsg.renameError.Title"),
				JOptionPane.ERROR_MESSAGE);
				
	}
	
	public static void errorOverwrite() {
		JOptionPane.showMessageDialog(MainFrame.frame, 
				Language.get("DialogMsg.errorOverwrite.Text"),
				Language.get("DialogMsg.errorOverwrite.Title"),
				JOptionPane.ERROR_MESSAGE);
	}
	
	public static int alreadyExistsChoice(String sourcePath, long sourceSize,
			String targetPath, long destSize){
		Object[] options = {Language.get("DialogMsg.alreadyExistsChoice.OverwriteButton"),
				Language.get("DialogMsg.alreadyExistsChoice.ResumeButton"),
				Language.get("DialogMsg.alreadyExistsChoice.RenameButton"),
				Language.get("DialogMsg.alreadyExistsChoice.CancelButton")};
		int n = JOptionPane.showOptionDialog(MainFrame.frame,
			Language.get("DialogMsg.alreadyExistsChoice.Text1") +
			Language.get("DialogMsg.alreadyExistsChoice.Source") +
			" : "+ prettySize(sourceSize)+ "\n"+
			sourcePath +
			Language.get("DialogMsg.alreadyExistsChoice.Target")+
			" : "+ prettySize(destSize)+ "\n"
			+targetPath+"\n\n"
			+ Language.get("DialogMsg.alreadyExistsChoice.Text2"),
			Language.get("DialogMsg.alreadyExistsChoice.Title"),
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[3]);
		return n;
	}
	
	
	public static boolean transferErrorChoice(){
		Object[] options = {Language.get("DialogMsg.transferErrorChoice.QueueButton"),
				Language.get("DialogMsg.transferErrorChoice.AbortButton")};
		int n = JOptionPane.showOptionDialog(MainFrame.frame,
				Language.get("DialogMsg.transferErrorChoice.Text"),
		Language.get("DialogMsg.transferErrorChoice.Title"),
		JOptionPane.YES_NO_OPTION,
		JOptionPane.ERROR_MESSAGE,
		null,
		options,
		options[0]);
		return (n==0);
	}
	
	public static void displayWelcome(){
		JOptionPane.showMessageDialog(null,
				Language.get("DialogMsg.welcome.Text"),
				Language.get("DialogMsg.welcome.Title"),
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void displayCredits() {
		JOptionPane.showMessageDialog(MainFrame.frame, 
				"MiniCopier - 0.2\nNovember 2007\nAdrian Courrèges\n\n"
				+"For bug reports or suggestions,\n"
				+"please, send an email to :\n"
				+"a.courreges@gmail.com",
				"Credits",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/** Conversion of a long representing the size of a file
	 * in bytes into a user-friendly String. 
	 * e.g. : 71430144 -> "68.12 MB"
	 * @param s size in bytes
	 */
	public static String prettySize(long s){
		String unit = "";
		double cut;
		
		if (s < 1024) {
			return s + " " + b;
		} else if (s < Math.pow(1024,2)){
			cut = ((double) s) / Math.pow(1024,1);
			unit = " " + kb;
		} else if (s < Math.pow(1024,3)) {
			cut = ((double) s) / Math.pow(1024,2);
			unit = " " + mb;
		} else if (s < Math.pow(1024,4)) {
			cut = ((double) s) / Math.pow(1024,3);
			unit = " " + gb;
		} else {
			cut = ((double) s) / Math.pow(1024,4);
			unit = " " + tb;
		}
		
		cut *= 100.0;
		cut = Math.floor(cut+0.5);
		cut /= 100.0;
		
		return cut + unit;
	}
	
	public static String prettySpeed(long s){
		String unit = "";
		double cut;
		
		if (s < 1024) {
			return s + " " + bps;
		} else if (s < Math.pow(1024,2)){
			cut = ((double) s) / Math.pow(1024,1);
			unit = " " + kbps;
		} else if (s < Math.pow(1024,3)) {
			cut = ((double) s) / Math.pow(1024,2);
			unit = " " + mbps;
		} else if (s < Math.pow(1024,4)) {
			cut = ((double) s) / Math.pow(1024,3);
			unit = " " + gbps;
		} else {
			cut = ((double) s) / Math.pow(1024,4);
			unit = " " + tbps;
		}
		
		cut *= 100.0;
		cut = Math.floor(cut+0.5);
		cut /= 100.0;
		
		return cut + unit;
	}
	
	public static String prettyTime(long t){
		StringBuffer sb = new StringBuffer();
		if (t < 60) { // x sec
			sb.append(t).append(sec);
			return sb.toString();
		}
		if (t < 3600) { // x min y sec
			sb.append(t/60).append(min).append(" ");
			t = t % 60;
			if (t > 0) {
				//if (t<10) sb.append("0");
				sb.append(t).append(sec);
			}

			return sb.toString();
		}
		if (t < 86400) {// x h y min
			sb.append(t/3600).append(hour).append(" ");
			t = t % 3600;
			if (t/60 > 0) {
				//if (t<10) sb.append("0");
				sb.append(t/60).append(min);
			}

			return sb.toString();
		}
		// x days y hours
		sb.append(t/86400).append(day).append(" ");
		t = t % 86400;
		if (t/3600 > 0) {
			sb.append(t/3600).append(hour);
		}
		
		return sb.toString();
	}
	
}
