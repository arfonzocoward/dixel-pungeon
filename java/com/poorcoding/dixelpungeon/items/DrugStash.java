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
package com.poorcoding.dixelpungeon.items;

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.effects.Speck;
import com.poorcoding.dixelpungeon.effects.particles.ShaftParticle;
import com.poorcoding.dixelpungeon.sprites.CharSprite;
import com.poorcoding.dixelpungeon.sprites.ItemSprite.Glowing;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.utils.Bundle;

import java.util.ArrayList;

public class DrugStash extends Item {

	private static final int MAX_VOLUME	= 10;
	
	private static final String AC_DRINK	= "DRINK";
	private static final String AC_CONSUME = "CONSUME";
	
	private static final float TIME_TO_DRINK = 5f;
	
	private static final String TXT_VALUE	= "%+dHP";
	private static final String TXT_STATUS	= "%d/%d";
	
	private static final String TXT_AUTO_DRINK	= "The drug stash was consumed to heal your wounds.";
	private static final String TXT_COLLECTED	= "You collected some drugs into your stash.";
	private static final String TXT_FULL		= "Drug stash is full!";
	private static final String TXT_EMPTY		= "Your drug stash is empty! :(";
	
	{
		name = "drug stash";
		//image = ItemSpriteSheet.VIAL;
		image = ItemSpriteSheet.POUCH;
		
		//defaultAction = AC_DRINK;
		defaultAction = AC_CONSUME;
		
		unique = true;
	}
	
	private int volume = 0;
	
	private static final String VOLUME	= "volume";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( VOLUME, volume );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		volume	= bundle.getInt( VOLUME );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (volume > 0) {
			actions.add( AC_CONSUME );
		}
		return actions;
	}
	
	private static final double NUM = 20;
	private static final double POW = Math.log10( NUM );
	
	@Override
	public void execute( final Hero hero, String action ) {
		if (action.equals( AC_CONSUME )) {
			
			if (volume > 0) {

				int value = (int)Math.ceil( Math.pow( volume, POW ) / NUM * hero.HT );
				int effect = Math.min( hero.HT - hero.HP, value );
				if (effect > 0) {
					hero.HP += effect;
					hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), volume > 5 ? 2 : 1 );
					hero.sprite.showStatus( CharSprite.POSITIVE, TXT_VALUE, effect );
				}
				
				volume = 0;
				
				hero.spend( TIME_TO_DRINK );
				hero.busy();
				
				//Sample.INSTANCE.play( Assets.SND_DRINK );
				Sample.INSTANCE.play( Assets.SND_PUFF );
				hero.sprite.operate( hero.pos );
				
				updateQuickslot();
				
			} else {
				GLog.w( TXT_EMPTY );
			}
			
		} else {
			
			super.execute( hero, action );
			
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public boolean isFull() {
		return volume >= MAX_VOLUME;
	}
	
	public void collectDrugs(Drug drug ) {
		
		GLog.i( TXT_COLLECTED );
		volume += drug.quantity;
		if (volume >= MAX_VOLUME) {
			volume = MAX_VOLUME;
			GLog.p( TXT_FULL );
		}
		
		updateQuickslot();
	}
	
	public void fill() {
		volume = MAX_VOLUME;
		updateQuickslot();
	}

	/*
	public static void autoDrink( Hero hero ) {
		DrugStash vial = hero.belongings.getItem( DrugStash.class );
		if (vial != null && vial.isFull()) {
			vial.execute( hero );
			hero.sprite.emitter().start( ShaftParticle.FACTORY, 0.2f, 3 );
			
			GLog.w( TXT_AUTO_DRINK );
		}
	}
	*/
	
	private static final Glowing WHITE = new Glowing( 0xFFFFCC );
	
	@Override
	public Glowing glowing() {
		return isFull() ? WHITE : null;
	}
	
	@Override
	public String status() {
		return Utils.format( TXT_STATUS, volume, MAX_VOLUME );
	}
	
	@Override
	public String info() {
		return 
			"You can store drugs in this stash for consuming it when needed. ";
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + status() +  ")" ;
	}
}
