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
package com.poorcoding.dixelpungeon.items.wands;

import com.poorcoding.noosa.Camera;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.ResultDescriptions;
import com.poorcoding.dixelpungeon.actors.Actor;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Paralysis;
import com.poorcoding.dixelpungeon.actors.mobs.Mob;
import com.poorcoding.dixelpungeon.effects.CellEmitter;
import com.poorcoding.dixelpungeon.effects.MagicMissile;
import com.poorcoding.dixelpungeon.effects.Speck;
import com.poorcoding.dixelpungeon.levels.Level;
import com.poorcoding.dixelpungeon.mechanics.Ballistica;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.utils.BArray;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.utils.Callback;
import com.poorcoding.utils.PathFinder;
import com.poorcoding.utils.Random;

public class WandOfAvalanche extends Wand {

	{
		name = "Wand of Avalanche";
		hitChars = false;
	}

	@Override
	protected void onZap( int cell ) {

		Sample.INSTANCE.play( Assets.SND_ROCKS );

		int level = power();

		Ballistica.distance = Math.min( Ballistica.distance, 8 + level );

		int size = 1 + level / 3;
		PathFinder.buildDistanceMap( cell, BArray.not( Level.solid, null ), size );

		int shake = 0;
		for (int i=0; i < Level.LENGTH; i++) {

			int d = PathFinder.distance[i];

			if (d < Integer.MAX_VALUE) {

				Char ch = Actor.findChar( i );
				if (ch != null) {

					ch.sprite.flash();
					ch.damage( Random.Int( 2, 6 + (size - d) * 2 ), this );

					if (ch.isAlive() && Random.Int( 2 + d ) == 0) {
						Buff.prolong( ch, Paralysis.class, Random.IntRange( 2, 6 ) );
					}
				}

				if (ch != null && ch.isAlive()) {
					if (ch instanceof Mob) {
						Dungeon.level.mobPress( (Mob)ch );
					} else {
						Dungeon.level.press( i, ch );
					}
				} else {
					Dungeon.level.press( i, null );
				}

				if (Dungeon.visible[i]) {
					CellEmitter.get(i).start(Speck.factory(Speck.ROCK), 0.07f, 3 + (size - d));
					if (Level.water[i]) {
						GameScene.ripple( i );
					}
					if (shake < size - d) {
						shake = size - d;
					}
				}
			}

			Camera.main.shake( 3, 0.07f * (3 + shake) );
		}

		if (!curUser.isAlive()) {
			Dungeon.fail( Utils.format( ResultDescriptions.WAND, name, Dungeon.depth ) );
			GLog.n( "You killed yourself with your own Wand of Avalanche..." );
		}
	}

	protected void fx( int cell, Callback callback ) {
		MagicMissile.earth( curUser.sprite.parent, curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}

	@Override
	public String desc() {
		return
				"When a discharge of this wand hits a wall (or any other solid obstacle) it causes " +
						"an avalanche of stones, damaging and stunning all creatures in the affected area.";
	}
}