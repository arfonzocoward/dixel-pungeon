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
import com.poorcoding.dixelpungeon.actors.Actor;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.hero.HeroClass;
import com.poorcoding.dixelpungeon.effects.particles.BloodParticle;
import com.poorcoding.dixelpungeon.effects.particles.PoisonParticle;
import com.poorcoding.dixelpungeon.items.weapon.enchantments.Poison;
import com.poorcoding.dixelpungeon.sprites.CharSprite;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.utils.Random;

public class Pistol extends Firearm {

	private static final String TXT_YOU_MISSED	= "%s %s your shot";
	private static final String TXT_SMB_MISSED	= "%s %s %s's shot";

	{
		name = "Pistol";
	}

	@Override
	protected void onZap( int cell ) {

		int level = power();

		Char ch = Actor.findChar( cell );
		if (ch != null) {
			if (Random.Int(0,9) == 0) {
				// Calculate dodge
				String defense = ch.defenseVerb();
				ch.sprite.showStatus(CharSprite.NEUTRAL, defense);
				if (curUser == Dungeon.hero) {
					GLog.i(TXT_YOU_MISSED, ch.name, defense);
				} else {
					GLog.i(TXT_SMB_MISSED, ch.name, defense, name);
				}
				Sample.INSTANCE.play(Assets.SND_MISS);
			} else {
				// Not missed: calculate damage

				//GLog.i("DEBUG: Pistol level is " + level);

				//int damage = Random.Int( 1, 8 + level * level );
				int damage = Random.Int(1, 5 + level * 2);

				if (curUser.heroClass == HeroClass.TECHNOPRANCER) {
					// Increased Pistol damage for Technoprancers.
					damage *= Random.Float(1.25f,2f);
				}

				ch.damage(damage, this);
				ch.sprite.emitter().burst(BloodParticle.FACTORY, damage);

				// Enchantment damage: does up to half physical damage as enchanted damage.
				if (isEnchanted()) {
					int d = damage / 2;
					enchantment.proc(this, curUser, ch, d);
					if (enchantment.equals(Poison.class)) {
						// Poison affect
						ch.sprite.emitter().burst(PoisonParticle.SPLASH,d);
					}
				}
			}
		}
	}

}
