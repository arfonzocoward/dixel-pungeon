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

public class Sushi extends Food {

	{
		name = "sushi";
		image = ItemSpriteSheet.FOOD_SUSHI;
		energy = Hunger.STARVING;
		message = "Mmmm, sushi!";
	}
	
	@Override
	public String info() {
		return "This sushi looks fresh and nutritious... These MOBs sure do know how to eat well down here.";
	}
	
	@Override
	public int price() {
		return 20 * quantity;
	}
}
