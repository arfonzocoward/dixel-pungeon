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
import com.poorcoding.dixelpungeon.Badges;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.Statistics;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.sprites.CharSprite;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.utils.Bundle;
import com.poorcoding.utils.Random;

import java.util.ArrayList;

public class Soul extends Item {

	private static final String TXT_COLLECT	= "Collect souls to upgrade at campfires.";
	private static final String TXT_INFO	= "%d souls. " + TXT_COLLECT;
	private static final String TXT_INFO_1	= "One soul. " + TXT_COLLECT;
	private static final String TXT_VALUE	= "%+d souls";

	{
		name = "souls";
		// TODO: Create image sprite.
		image = ItemSpriteSheet.SOULS;
		stackable = true;
	}

	public Soul() {
		this( 1 );
	}

	public Soul(int value ) {
		this.quantity = value;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		return new ArrayList<String>();
	}
	
	@Override
	public boolean doPickUp( Hero hero ) {
		
		Dungeon.souls += quantity;
		Statistics.soulsCollected += quantity;
		//Badges.validateGoldCollected();
		
		GameScene.pickUp( this );
		hero.sprite.showStatus( CharSprite.NEUTRAL, TXT_VALUE, quantity );
		hero.spendAndNext( TIME_TO_PICK_UP );
		
		Sample.INSTANCE.play( Assets.SND_LIGHTNING, 1, 1, Random.Float( 0.9f, 1.1f ) );
		
		return true;
	}
	
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
		quantity = Random.Int( 20 + Dungeon.depth * 10, 40 + Dungeon.depth * 20 );
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
}
