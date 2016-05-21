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
package com.poorcoding.dixelpungeon.actors.blobs;

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.Journal;
import com.poorcoding.dixelpungeon.Journal.Feature;
import com.poorcoding.dixelpungeon.actors.buffs.Hunger;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.effects.BlobEmitter;
import com.poorcoding.dixelpungeon.effects.CellEmitter;
import com.poorcoding.dixelpungeon.effects.Speck;
import com.poorcoding.dixelpungeon.effects.particles.ShaftParticle;
import com.poorcoding.dixelpungeon.items.DewVial;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.items.potions.PotionOfHealing;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.noosa.audio.Sample;

public class Soulforge extends SoulforgeForce {

	private static final String TXT_PROCCED =
		"As you rest at the camp, you feel your wounds heal completely.";
	
	@Override
	protected boolean affectHero( Hero hero ) {
		
		Sample.INSTANCE.play( Assets.SND_LULLABY);
		
		PotionOfHealing.heal( hero );
		hero.belongings.uncurseEquipped();
		((Hunger)hero.buff( Hunger.class )).satisfy( Hunger.STARVING );
		
		CellEmitter.get( pos ).start( ShaftParticle.FACTORY, 0.2f, 3 );

		Dungeon.hero.interrupt();
	
		GLog.p( TXT_PROCCED );
		
		//Journal.remove( Feature.WELL_OF_HEALTH );
		Journal.remove( Feature.SOULFORGE);
		
		return true;
	}
	
	/*@Override
	protected Item affectItem( Item item ) {
		if (item instanceof DewVial && !((DewVial)item).isFull()) {
			((DewVial)item).fill();
			Journal.remove( Feature.WELL_OF_HEALTH );
			return item;
		}
		
		return null;
	}*/
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );	
		emitter.start( Speck.factory( Speck.HEALING ), 0.5f, 0 );
	}

	/*
	@Override
	public String tileDesc() {
		return 
			"Soulforge camp. " +
			"Rest here to heal your wounds and satisfy hunger.";
	}
	*/

}
