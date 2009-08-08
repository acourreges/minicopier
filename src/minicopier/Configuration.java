/*
  Configuration.java / MiniCopier
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

import java.util.*;
import java.io.*;

import minicopier.gui.DialogMsg;

public class Configuration {
	
	private static final String configName = "configuration.ini";
	
	public static String language;
	
	public static boolean alwaysOnTop;
	
	//Look And Feel
	//"pgs" (default)
	//"native"
	//"metal"
	public static String lookAndFeel;
	
	//What to do if destination file already exists
	// 0 -> ask user
	// 1 -> ignore
	// 2 -> overwrite
	// 3 -> overwrite if older
	// 4 -> resume
	// 5 -> rename
	public static int collisionAction;
	
	//What to do if a source path contains a symbolic link
	//(Unix systems only)
	// 0 -> follow
	// 1 -> ignore
	public static int symbolicLinkAction;
    
    //Is speed limit active?
    public static boolean limitSpeed;
    
    //Maximum throughput of the transfer
    public static int nbBytesPerS;
    
    private static boolean firstLaunch = false;
	
	public Configuration(){
        
        this.limitSpeed = false;
        
        this.nbBytesPerS = 1024*1024;
		
		Properties prop = new Properties(); 
		
		File configFile = new File(configName);
		
		if (!configFile.exists()) {
			System.out.println("Configuration file not found. Creating configuration.ini.");
			createConfigFile();
			firstLaunch = true;
		}
		
		try {
			FileInputStream in = new FileInputStream(configName);
			prop.load(in);
			in.close(); 
		} catch (Exception e){
			e.printStackTrace();
		}
		
		setProp(prop);
		
	}
	
	public static void setProp(Properties prop){
		
		String readProp;
		
		//Language
		readProp = prop.getProperty("language");
		if (("").equals(readProp)||(readProp==null)){
			language = "SystemLocale";
		} else {
			language = readProp;
		}
		
		//Always on top
		readProp = prop.getProperty("alwaysontop");
		if (("true").equals(readProp)){
			alwaysOnTop = true;
		} else {
			alwaysOnTop = false;
		}
		
		//Look an Feel
		readProp = prop.getProperty("lnf");
		if (("").equals(readProp)||(readProp==null)){
			lookAndFeel = "pgs";
		}
		else {
			lookAndFeel = readProp;
		}
		
		//Collision action
		readProp = prop.getProperty("collision");
		if (("ask").equals(readProp)){
			collisionAction = 0;
		} else if (("cancel").equals(readProp)){
			collisionAction = 1;
		} else if (("overwrite").equals(readProp)){
			collisionAction = 2;
		} else if (("overwrite_older").equals(readProp)){
			collisionAction = 3;
		} else if (("resume").equals(readProp)){
			collisionAction = 4;
		} else {
			collisionAction = 0;
		}
		
		//Symbolic links action
		readProp = prop.getProperty("symlinks");
		if (("follow").equals(readProp)){
			symbolicLinkAction = 0;
		} else if (("ignore").equals(readProp)){
			symbolicLinkAction = 1;
		} else {
			symbolicLinkAction = 0;
		}
		
	}
	
	public final void createConfigFile(){
		InputStreamReader isr=new InputStreamReader(getClass()
				.getClassLoader().getResourceAsStream("minicopier/configuration.ini"));
		
		BufferedReader buf = new BufferedReader(isr);
		
		PrintWriter writer;
		String toWrite;
		
		try {
			writer = new PrintWriter (new OutputStreamWriter (new FileOutputStream (configName)));
			while ((toWrite=buf.readLine())!=null){
				writer.println(toWrite);
			}
			buf.close();
			writer.close();
		} catch (Exception e){
			System.out.println("Error creation configuration file !");
		}
		
	}
	
	public boolean wasFirstLaunch(){
		return this.firstLaunch;
	}
    
    public synchronized static void setSpeedValue(int i){
        Configuration.nbBytesPerS = i;
    }
    
    public synchronized static int getSpeedValue(){
        return Configuration.nbBytesPerS;
    }
	

	
	

}
