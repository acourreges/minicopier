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

	public static class Item
	{
		String  content;
		boolean move   ; 
		
		private Item(String content)
		{
			this.content = content;
		}

		private Item(String content, Boolean move)
		{
			this.content = content;
			this.move    = move   ;
		}

		/**
		 * Factory method creates movable content.
		 * @param content
		 * @return 
		 */
		public static Item Movable(String content)
		{
			return new Item(content, true);
		}

		/**
		 * Factory method creates default item.
		 * @param content
		 * @return 
		 */
		public static Item Default(String content)
		{
			return new Item(content);
		}

		public String toString()
		{
			return getContent();
		}

		public String getContent()
		{
			return content;
		}

		public boolean getMove()
		{
			return move;
		}
	}

	//Contains paths of files/folders to be copied
	private Vector<Item> basketContent;
	
	/* Creation of an empty basket */
	public Basket () {
		this.basketContent = new Vector<Item>() ;
	}
	
	/* Add a path to the basket
	 * @path path of a folder/file to be copied
	 */
	public void add (String path) {
		this.basketContent.add(Basket.Item.Default(path));
	}

	/**
	 * Add a path to the basket, when move true then path will be moved.
	 * @param path
	 * @param move 
	 */
	public void add (String path, Boolean move)
	{
		if (move)
		{
		   this.basketContent.add(Basket.Item.Movable(path));
		} 
		else
		{
			this.basketContent.add(Basket.Item.Default(path));
		} 
	}
	
	public Iterator<Item> getIterator() {
		return basketContent.iterator();
	}
	
	public boolean isEmpty() {
		return this.basketContent.isEmpty();
	}
	
	public int getLength(){
		return basketContent.size();
	}
}
