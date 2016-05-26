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
package com.poorcoding.dixelpungeon.items.food;

import com.poorcoding.dixelpungeon.actors.buffs.Hunger;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;

public class LobsterSubmarineSandwich extends Food {

	{
		name = "lobster sub";
		image = ItemSpriteSheet.FOOD_LOBSTERSUB;
		energy = Hunger.STARVING;
		message = "The Lobster Sub was fecking delicious!";
	}
	
	@Override
	public String info() {
		return "This is a genuine, limited edition Lobster Submarine Sandwich from SubTray. Its hunger satisfying properties are well known.";
	}
	
	@Override
	public int price() {
		return 20 * quantity;
	}
}
