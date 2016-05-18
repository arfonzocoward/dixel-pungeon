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
package com.poorcoding.dixelpungeon.actors.mobs;

import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.mobs.npcs.Ghost;
import com.poorcoding.dixelpungeon.items.Gold;
import com.poorcoding.dixelpungeon.items.Soul;
import com.poorcoding.dixelpungeon.sprites.GoblinSprite;
import com.poorcoding.utils.Random;
import com.poorcoding.dixelpungeon.Dungeon;

public class Goblin extends Mob {
	
	{
		name = "goblin scout";
		spriteClass = GoblinSprite.class;
		
		HP = HT = 10;
		defenseSkill = 3;
		
		EXP = 2;
		maxLvl = 8;
		
		loot = Gold.class;
		lootChance = 0.5f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 11;
	}
	
	@Override
	public int dr() {
		return 2;
	}
	
	@Override
	public void die( Object cause ) {
		Ghost.Quest.processSewersKill( pos );
		super.die( cause );

		// Always drop souls
		//Dungeon.level.drop( new Soul(5), pos ).sprite.drop();
		//Dungeon.level.drop( new Soul(HT), pos ).sprite.drop();
	}
	
	@Override
	public String description() {
		return
			"Goblins are humanoid dwellers of dungeons. They venture up to raid the surface from time to time. " +
			"Goblin scouts are regular members of their pack, they are not as strong as brutes and not as intelligent as shamans.";
	}
}
