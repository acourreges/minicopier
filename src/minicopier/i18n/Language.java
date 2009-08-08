/*
  Language.java / MiniCopier
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

package minicopier.i18n;

import java.util.*;

public class Language {
	
	static ResourceBundle messages;
	
	/** If argument is "SystemLocale", we will use the system default locale. 
	* Otherwise, we can force a language with : "fr", "en" ...
	* @param languageLocale "SystemLocale", "fr", "en"
	* */
	public static void init(String languageLocale) {
		
		Locale locale;
		
		if (languageLocale.equals("SystemLocale")) {
			locale = Locale.getDefault();
		} else {
			locale = new Locale(languageLocale);
		}
		
		Language.messages = ResourceBundle.getBundle("minicopier.i18n.minicopier",locale);
		
		
	}
	
	public static String get(String m) {
		return messages.getString(m);
	}	

}
