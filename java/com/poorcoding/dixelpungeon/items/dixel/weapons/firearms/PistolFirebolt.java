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
package com.poorcoding.dixelpungeon.items.dixel.weapons.firearms;

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.ResultDescriptions;
import com.poorcoding.dixelpungeon.actors.Actor;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.blobs.Blob;
import com.poorcoding.dixelpungeon.actors.blobs.Fire;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Burning;
import com.poorcoding.dixelpungeon.effects.MagicMissile;
import com.poorcoding.dixelpungeon.effects.particles.FlameParticle;
import com.poorcoding.dixelpungeon.levels.Level;
import com.poorcoding.dixelpungeon.mechanics.Ballistica;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.utils.Callback;
import com.poorcoding.utils.Random;

public class PistolFirebolt extends Pistol {

	{
		name = "Jengo's Justice (Firebolt Pistol)";
	}

	@Override
	protected void onZap( int cell ) {

		//super.onZap( cell );

		if (super.canZap()) {

			int level = power();

			for (int i = 1; i < Ballistica.distance - 1; i++) {
				int c = Ballistica.trace[i];
				if (Level.flamable[c]) {
					GameScene.add(Blob.seed(c, 1, Fire.class));
				}
			}

			GameScene.add(Blob.seed(cell, 1, Fire.class));

			Char ch = Actor.findChar(cell);
			if (ch != null) {

				ch.damage(Random.Int(1, 8 + level * level), this);
				Buff.affect(ch, Burning.class).reignite(ch);

				ch.sprite.emitter().burst(FlameParticle.FACTORY, 5);

				if (ch == curUser && !ch.isAlive()) {
					Dungeon.fail(Utils.format(ResultDescriptions.FIREARM, name, Dungeon.depth));
					GLog.n("You killed yourself with " + name + ": justice is served, sucka.");
				}
			}

			//updateQuickslot();
		}
	}

	protected void fx( int cell, Callback callback ) {
		MagicMissile.fire( curUser.sprite.parent, curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_FIREARM);
		Sample.INSTANCE.play( Assets.SND_BURNING);
	}

	@Override
	protected int initialCharges() {
		return 2;
	}

	@Override
	public String desc() {
		return
				"Jengo's Justice is a unique firearm that unleashes flaming bullets. It will ignite " +
						"any flesh and items it hits. BBQ, anyone?";
	}
}
