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
package com.poorcoding.dixelpungeon.items.weapon.missiles;

import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.actors.Actor;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.actors.hero.HeroAction;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.items.weapon.Weapon;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.utils.Random;

public class Ammo extends MissileWeapon {

	{
		name = "bullet";
		image = ItemSpriteSheet.STYLUS;
		STR = 12;

		//defaultAction = AC_DROP;
	}

	public Ammo() {
		this( 1 );
	}

	public Ammo(int number ) {
		super();
		quantity = number;
	}
	
	@Override
	public int min() {
		return 3;
	}
	
	@Override
	public int max() {
		return 9;
	}
	
	@Override
	public String desc() {
		return 
			"These bullets are used in firearms to cause serious damage.";
	}
	
	@Override
	public Item random() {
		quantity = Random.Int( 5, 15 );
		return this;
	}
	
	@Override
	public int price() {
		return quantity * 7;
	}

	@Override
	protected void onThrow( int cell ) {
		//this.detach( Dungeon.hero.belongings.backpack ).onThrow( cell );
		//user.spendAndNext( finalDelay );
	}
}
