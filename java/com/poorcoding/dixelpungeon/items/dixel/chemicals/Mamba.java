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

import com.poorcoding.dixelpungeon.actors.buffs.Awareness;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Invisibility;
import com.poorcoding.dixelpungeon.actors.buffs.Speed;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.sprites.CharSprite;
import com.poorcoding.dixelpungeon.utils.GLog;

public class Mamba extends Chemical {

	{
		name = "Mamba";
	}
	
	@Override
	protected void apply( Hero hero ) {
		setKnown();

		hero.sprite.showStatus( CharSprite.POSITIVE, "speed++" );
		GLog.p( "The Mamba temporarily heightens your speed." );
		Buff.affect( hero, Speed.class, Speed.DURATION );
	}
	
	@Override
	public String desc() {
		return
			"Filthy ol' Mamba. Drug of choice for the wastrels of LelDorado due to it being a legal gray market substance.\n\n" +
			"Side effects include: feeling like a bigwig for about 10 minutes (+speed).";
	}
	
	@Override
	public int price() {
		return isKnown() ? 10 * quantity : super.price();
	}
}
