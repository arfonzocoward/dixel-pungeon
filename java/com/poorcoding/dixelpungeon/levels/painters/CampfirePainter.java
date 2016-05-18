/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.poorcoding.dixelpungeon.levels.painters;

import com.poorcoding.dixelpungeon.items.Generator;
import com.poorcoding.dixelpungeon.items.Heap.Type;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.items.keys.IronKey;
import com.poorcoding.dixelpungeon.levels.Level;
import com.poorcoding.dixelpungeon.levels.Room;
import com.poorcoding.dixelpungeon.levels.Terrain;
import com.poorcoding.utils.Point;
import com.poorcoding.utils.Random;
import com.poorcoding.dixelpungeon.items.Soul;

public class CampfirePainter extends Painter {

	public static void paint( Level level, Room room ) {

		//fill( level, room, Terrain.WALL );
		//fill( level, room, 1, Terrain.EMPTY );
		fill( level, room, Terrain.WALL );
		fill( level, room, 1, Terrain.HIGH_GRASS );
		fill( level, room, 2, Terrain.GRASS );

		Point c = room.center();

		Room.Door entrance = room.entrance();
		entrance.set( Room.Door.Type.LOCKED );
		
		Point well = null;
		if (entrance.x == room.left) {
			well = new Point( room.right-1, Random.Int( 2 ) == 0 ? room.top + 1 : room.bottom - 1 );
		} else if (entrance.x == room.right) {
			well = new Point( room.left+1, Random.Int( 2 ) == 0 ? room.top + 1 : room.bottom - 1 );
		} else if (entrance.y == room.top) {
			well = new Point( Random.Int( 2 ) == 0 ? room.left + 1 : room.right - 1, room.bottom-1 );
		} else if (entrance.y == room.bottom) {
			well = new Point( Random.Int( 2 ) == 0 ? room.left + 1 : room.right - 1, room.top+1 );
		}
		//set( level, well, Terrain.EMPTY_WELL );
		
		int remains = room.random();
		while (level.map[remains] == Terrain.EMPTY_WELL) {
			remains = room.random();
		}
		
		level.drop( new IronKey(), remains ).type = Type.SKELETON;
		
		if (Random.Int( 5 ) == 0) {
			level.drop( Generator.random( Generator.Category.RING ), remains );
		} else {
			level.drop( Generator.random( Random.oneOf( 
				Generator.Category.WEAPON, 
				Generator.Category.ARMOR
			) ), remains );
		}
		
		int n = Random.IntRange( 1, 2 );
		for (int i=0; i < n; i++) {
			level.drop( prize( level ), remains );
		}

		// TODO: Paint a campfire.
		fill( level, c.x - 1, c.y - 1, 3, 3, Terrain.GRASS );
		set( level, c, Terrain.ALCHEMY );
		level.drop(new Soul(77), room.random());
	}
	
	private static Item prize( Level level ) {
		
		Item prize = level.itemToSpanAsPrize();
		if (prize != null) {
			return prize;
		}
		
		return Generator.random( Random.oneOf( 
			Generator.Category.POTION, 
			Generator.Category.SCROLL,
			Generator.Category.FOOD, 
			Generator.Category.GOLD
		) );
	}
}
