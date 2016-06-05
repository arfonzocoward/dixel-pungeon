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
package com.poorcoding.dixelpungeon.items.dixel.chemicals;

import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Regeneration;
import com.poorcoding.dixelpungeon.actors.buffs.Speed;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.items.potions.PotionOfHealing;
import com.poorcoding.dixelpungeon.sprites.CharSprite;
import com.poorcoding.dixelpungeon.utils.GLog;

public class StemCells extends Chemical {

	{
		name = "Stem Cells";
	}
	
	@Override
	protected void apply( Hero hero ) {
		setKnown();

		hero.sprite.showStatus( CharSprite.POSITIVE, "+HP" );
		GLog.p( "As the stem cells rush into your bloodstream, you feel your life force healing." );
		PotionOfHealing.heal(hero);
	}
	
	@Override
	public String desc() {
		return
			"Stem Cells. These little beauts will heal anything, real quick.";
	}
	
	@Override
	public int price() {
		return isKnown() ? 150 * quantity : super.price();
	}
}
