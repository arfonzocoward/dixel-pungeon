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
package com.poorcoding.dixelpungeon.items.dixel;

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.ResultDescriptions;
import com.poorcoding.dixelpungeon.actors.Actor;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.blobs.Blob;
import com.poorcoding.dixelpungeon.actors.blobs.Fire;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Burning;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.effects.MagicMissile;
import com.poorcoding.dixelpungeon.effects.particles.BloodParticle;
import com.poorcoding.dixelpungeon.effects.particles.FlameParticle;
import com.poorcoding.dixelpungeon.levels.Level;
import com.poorcoding.dixelpungeon.mechanics.Ballistica;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.utils.Callback;
import com.poorcoding.utils.Random;

public class Pistol extends Firearm {

	{
		name = "Pistol";
	}

	@Override
	protected void onZap( int cell ) {

		int level = power();

		Char ch = Actor.findChar( cell );
		if (ch != null) {
			//int damage = Random.Int( 1, 8 + level * level );
			int damage = Random.Int( 1, 8 + level*2 );
			ch.damage( damage, this );
			ch.sprite.emitter().burst( BloodParticle.FACTORY, damage );
		}
	}

}
