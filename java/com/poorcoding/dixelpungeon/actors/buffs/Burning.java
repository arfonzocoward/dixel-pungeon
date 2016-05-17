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
package com.poorcoding.dixelpungeon.actors.buffs;

import com.poorcoding.dixelpungeon.Badges;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.ResultDescriptions;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.blobs.Blob;
import com.poorcoding.dixelpungeon.actors.blobs.Fire;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.actors.mobs.Thief;
import com.poorcoding.dixelpungeon.effects.particles.ElmoParticle;
import com.poorcoding.dixelpungeon.items.Heap;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.items.food.ChargrilledMeat;
import com.poorcoding.dixelpungeon.items.food.MysteryMeat;
import com.poorcoding.dixelpungeon.items.rings.RingOfElements.Resistance;
import com.poorcoding.dixelpungeon.items.scrolls.Scroll;
import com.poorcoding.dixelpungeon.levels.Level;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.ui.BuffIndicator;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.utils.Bundle;
import com.poorcoding.utils.Random;

public class Burning extends Buff implements Hero.Doom {

	private static final String TXT_BURNS_UP		= "%s burns up!";
	private static final String TXT_BURNED_TO_DEATH	= "You burned to death...";
	
	private static final float DURATION = 8f;
	
	private float left;
	
	private static final String LEFT	= "left";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEFT, left );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat( LEFT );
	}
	
	@Override
	public boolean act() {
		
		if (target.isAlive()) {
			
			if (target instanceof Hero) {
				Buff.prolong( target, Light.class, TICK * 1.01f );
			}

			target.damage( Random.Int( 1, 5 ), this );
			
			if (target instanceof Hero) {
				
				Item item = ((Hero)target).belongings.randomUnequipped();
				if (item instanceof Scroll) {
					
					item = item.detach( ((Hero)target).belongings.backpack );
					GLog.w( TXT_BURNS_UP, item.toString() );
					
					Heap.burnFX( target.pos );
					
				} else if (item instanceof MysteryMeat) {
					
					item = item.detach( ((Hero)target).belongings.backpack );
					ChargrilledMeat steak = new ChargrilledMeat(); 
					if (!steak.collect( ((Hero)target).belongings.backpack )) {
						Dungeon.level.drop( steak, target.pos ).sprite.drop();
					}
					GLog.w( TXT_BURNS_UP, item.toString() );
					
					Heap.burnFX( target.pos );
					
				}
				
			} else if (target instanceof Thief && ((Thief)target).item instanceof Scroll) {
				
				((Thief)target).item = null;
				target.sprite.emitter().burst( ElmoParticle.FACTORY, 6 );
			}

		} else {
			detach();
		}
		
		if (Level.flamable[target.pos]) {
			GameScene.add( Blob.seed( target.pos, 4, Fire.class ) );
		}
		
		spend( TICK );
		left -= TICK;
		
		if (left <= 0 ||
			Random.Float() > (2 + (float)target.HP / target.HT) / 3 ||
			(Level.water[target.pos] && !target.flying)) {
			
			detach();
		}

		return true;
	}
	
	public void reignite( Char ch ) {
		left = duration( ch );
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FIRE;
	}
	
	@Override
	public String toString() {
		return "Burning";
	}

	public static float duration( Char ch ) {
		Resistance r = ch.buff( Resistance.class );
		return r != null ? r.durationFactor() * DURATION : DURATION;
	}

	@Override
	public void onDeath() {
		
		Badges.validateDeathFromFire();
		
		Dungeon.fail( Utils.format( ResultDescriptions.BURNING, Dungeon.depth ) );
		GLog.n( TXT_BURNED_TO_DEATH );
	}
}
