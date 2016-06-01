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

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Invisibility;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.sprites.CharSprite;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.noosa.audio.Sample;

public class Gwailoprin extends Chemical {

	{
		name = "Gwailoprin";
	}
	
	@Override
	protected void apply( Hero hero ) {
		setKnown();

		Buff.affect( hero, Invisibility.class, Invisibility.DURATION );
		GLog.i( "You see your hands turn invisible!" );
		Sample.INSTANCE.play( Assets.SND_MELD );

		hero.damage(hero.HP-1, this);
		hero.sprite.showStatus( CharSprite.NEGATIVE, "GWAILOPRIN!!! U FUCKED ME OVER!" );
		GLog.p( "You take the Gwailoprin, vanish, and start knocking on death's door." );
	}
	
	@Override
	public String desc() {
		return
			"This magic juice has a delightful taste of passion fruit, " +
			"with a mild aftertaste of death." +
				"\n\nBrings one to the brink of death on contact. It does turn one invisible though, which could be handy.";
	}
	
	@Override
	public int price() {
		return isKnown() ? 100 * quantity : super.price();
	}
}
