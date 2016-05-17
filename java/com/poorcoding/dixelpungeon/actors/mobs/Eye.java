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

import java.util.HashSet;

import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.ResultDescriptions;
import com.poorcoding.dixelpungeon.actors.Actor;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.buffs.Light;
import com.poorcoding.dixelpungeon.actors.buffs.Terror;
import com.poorcoding.dixelpungeon.effects.CellEmitter;
import com.poorcoding.dixelpungeon.effects.particles.PurpleParticle;
import com.poorcoding.dixelpungeon.items.Dewdrop;
import com.poorcoding.dixelpungeon.items.wands.WandOfDisintegration;
import com.poorcoding.dixelpungeon.items.weapon.enchantments.Death;
import com.poorcoding.dixelpungeon.items.weapon.enchantments.Leech;
import com.poorcoding.dixelpungeon.mechanics.Ballistica;
import com.poorcoding.dixelpungeon.sprites.CharSprite;
import com.poorcoding.dixelpungeon.sprites.EyeSprite;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.utils.Random;

public class Eye extends Mob {
	
	private static final String TXT_DEATHGAZE_KILLED = "%s's deathgaze killed you...";
	
	{
		name = "evil eye";
		spriteClass = EyeSprite.class;
		
		HP = HT = 100;
		defenseSkill = 20;
		viewDistance = Light.DISTANCE;
		
		EXP = 13;
		maxLvl = 25;
		
		flying = true;
		
		loot = new Dewdrop();
		lootChance = 0.5f;
	}
	
	@Override
	public int dr() {
		return 10;
	}
	
	private int hitCell;
	
	@Override
	protected boolean canAttack( Char enemy ) {
		
		hitCell = Ballistica.cast( pos, enemy.pos, true, false );

		for (int i=1; i < Ballistica.distance; i++) {
			if (Ballistica.trace[i] == enemy.pos) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30;
	}
	
	@Override
	protected float attackDelay() {
		return 1.6f;
	}
	
	@Override
	protected boolean doAttack( Char enemy ) {

		spend( attackDelay() );
		
		boolean rayVisible = false;
		
		for (int i=0; i < Ballistica.distance; i++) {
			if (Dungeon.visible[Ballistica.trace[i]]) {
				rayVisible = true;
			}
		}
		
		if (rayVisible) {
			sprite.attack( hitCell );
			return false;
		} else {
			attack( enemy );
			return true;
		}
	}
	
	@Override
	public boolean attack( Char enemy ) {
		
		for (int i=1; i < Ballistica.distance; i++) {
			
			int pos = Ballistica.trace[i];
			
			Char ch = Actor.findChar( pos );
			if (ch == null) {
				continue;
			}
			
			if (hit( this, ch, true )) {
				ch.damage( Random.NormalIntRange( 14, 20 ), this );
				
				if (Dungeon.visible[pos]) {
					ch.sprite.flash();
					CellEmitter.center( pos ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
				}
				
				if (!ch.isAlive() && ch == Dungeon.hero) {
					Dungeon.fail( Utils.format( ResultDescriptions.MOB, Utils.indefinite( name ), Dungeon.depth ) );
					GLog.n( TXT_DEATHGAZE_KILLED, name );
				}
			} else {
				ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
			}
		}
		
		return true;
	}
	
	@Override
	public String description() {
		return
			"One of this demon's other names is \"orb of hatred\", because when it sees an enemy, " +
			"it uses its deathgaze recklessly, often ignoring its allies and wounding them.";
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( WandOfDisintegration.class );
		RESISTANCES.add( Death.class );
		RESISTANCES.add( Leech.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
	
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add( Terror.class );
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
