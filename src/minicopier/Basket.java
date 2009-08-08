/*
  Basket.java / MiniCopier
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

public class Basket {

	//Contains paths of files/folders to be copied
	private Vector<String> basketContent;
	
	/* Creation of an empty basket */
	public Basket () {
		this.basketContent = new Vector<String>() ;
	}
	
	/* Add a path to the basket
	 * @path path of a folder/file to be copied
	 */
	public void add (String path) {
		this.basketContent.add(path);
	}
	
	public Iterator<String> getIterator() {
		return basketContent.iterator();
	}
	
	public boolean isEmpty() {
		return this.basketContent.isEmpty();
	}
	
	public int getLength(){
		return basketContent.size();
	}
}
