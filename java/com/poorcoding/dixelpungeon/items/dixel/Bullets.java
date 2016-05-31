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

import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.utils.Bundle;
import com.poorcoding.utils.Random;

import java.util.ArrayList;

public class Bullets extends Item {

	private static final String TXT_COLLECT	= "Collect bullets to use in firearms.";
	private static final String TXT_INFO	= "A pile of %d bullets. " + TXT_COLLECT;
	private static final String TXT_INFO_1	= "One bullet. " + TXT_COLLECT;
	private static final String TXT_VALUE	= "%+d";

	{
		name = "bullets";
		image = ItemSpriteSheet.BULLET;
		stackable = true;

		defaultAction = AC_DROP;
	}

	public Bullets() {
		this( 1 );
	}

	public Bullets(int value ) {
		this.quantity = value;
	}
	
	/*@Override
	public ArrayList<String> actions( Hero hero ) {
		return new ArrayList<String>();
	}*/
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		return actions;
	}
	
	/*@Override
	public boolean doPickUp( Hero hero ) {
		
		Dungeon.gold += quantity;
		Statistics.goldCollected += quantity;
		Badges.validateGoldCollected();
		
		GameScene.pickUp( this );
		hero.sprite.showStatus( CharSprite.NEUTRAL, TXT_VALUE, quantity );
		hero.spendAndNext( TIME_TO_PICK_UP );
		
		Sample.INSTANCE.play( Assets.SND_GOLD, 1, 1, Random.Float( 0.9f, 1.1f ) );
		
		return true;
	}*/

	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public String info() {
		switch (quantity) {
		case 0:
			return TXT_COLLECT;
		case 1:
			return TXT_INFO_1;
		default:
			return Utils.format( TXT_INFO, quantity );
		}
	}
	
	@Override
	public Item random() {
		quantity = 1 + Random.Int(Dungeon.depth, Dungeon.depth*10 );
		return this;
	}
	
	private static final String VALUE	= "value";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( VALUE, quantity );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		quantity = bundle.getInt( VALUE );
	}

	@Override
	public int price() {
		return 5 * quantity;
	}
}
