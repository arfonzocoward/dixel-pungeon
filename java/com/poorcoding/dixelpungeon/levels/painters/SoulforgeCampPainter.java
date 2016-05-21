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

import com.poorcoding.dixelpungeon.actors.blobs.Soulforge;
import com.poorcoding.dixelpungeon.actors.blobs.SoulforgeForce;
//import com.poorcoding.dixelpungeon.actors.blobs.WellWater;
import com.poorcoding.dixelpungeon.levels.Level;
import com.poorcoding.dixelpungeon.levels.Room;
import com.poorcoding.dixelpungeon.levels.Terrain;
import com.poorcoding.utils.Point;
import com.poorcoding.utils.Random;

public class SoulforgeCampPainter extends Painter {

	private static final Class<?>[] WATERS = 
		{Soulforge.class};
	
	public static void paint( Level level, Room room ) {

		fill( level, room, Terrain.WALL );
		fill( level, room, 1, Terrain.EMPTY );

		// Add campfire.
		Point c = room.center();
		set( level, c.x, c.y, Terrain.CAMPFIRE);

		// TODO: Add forge.
		set( level, c.x + Random.Int(1,2), c.y + Random.Int(1,3), Terrain.SOULFORGE);

		@SuppressWarnings("unchecked")
		Class<? extends SoulforgeForce> waterClass =
			(Class<? extends SoulforgeForce>)Random.element( WATERS );

		SoulforgeForce water = (SoulforgeForce)level.blobs.get( waterClass );
		if (water == null) {
			try {
				water = waterClass.newInstance();
			} catch (Exception e) {
				water = null;
			}
		}
		water.seed( c.x + Level.WIDTH * c.y, 1 );
		level.blobs.put( waterClass, water );
		
		room.entrance().set( Room.Door.Type.REGULAR );
		
	}
}
