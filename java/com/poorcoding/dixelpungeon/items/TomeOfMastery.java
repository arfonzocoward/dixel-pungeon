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

import java.util.ArrayList;

import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Badges;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.actors.buffs.Blindness;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Fury;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.actors.hero.HeroSubClass;
import com.poorcoding.dixelpungeon.effects.Speck;
import com.poorcoding.dixelpungeon.effects.SpellSprite;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.dixelpungeon.windows.WndChooseWay;

public class TomeOfMastery extends Item {

	private static final String TXT_BLINDED	= "You can't read while blinded";
	
	public static final float TIME_TO_READ = 10;
	
	public static final String AC_READ	= "READ";
	
	{
		stackable = false;
		name = Dungeon.hero != null && Dungeon.hero.subClass != HeroSubClass.NONE ? "Tome of Remastery" : "Tome of Mastery";
		image = ItemSpriteSheet.MASTERY;
		
		unique = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_READ );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( AC_READ )) {
			
			if (hero.buff( Blindness.class ) != null) {
				GLog.w( TXT_BLINDED );
				return;
			}
			
			curUser = hero;
			
			switch (hero.heroClass) {
			case WARRIOR:
				read( hero, HeroSubClass.GLADIATOR, HeroSubClass.BERSERKER );
				break;
			case MAGE:
				read( hero, HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK );
				break;
			case ROGUE:
				read( hero, HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER );
				break;
			case HUNTRESS:
				read( hero, HeroSubClass.SNIPER, HeroSubClass.WARDEN );
				break;
			}
			
		} else {			
			super.execute( hero, action );		
		}
	}
	
	@Override
	public boolean doPickUp( Hero hero ) {
		Badges.validateMastery();
		return super.doPickUp( hero );
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
		return 
			"This worn leather book is not that thick, but you feel somehow, " +
			"that you can gather a lot from it. Remember though that reading " +
			"this tome may require some time.";
	}
	
	private void read( Hero hero, HeroSubClass sc1, HeroSubClass sc2 ) {
		if (hero.subClass == sc1) {
			GameScene.show( new WndChooseWay( this, sc2 ) );
		} else if (hero.subClass == sc2) {
			GameScene.show( new WndChooseWay( this, sc1 ) );
		} else {
			GameScene.show( new WndChooseWay( this, sc1, sc2 ) );
		}
	}
	
	public void choose( HeroSubClass way ) {
		
		detach( curUser.belongings.backpack );
		
		curUser.spend( TomeOfMastery.TIME_TO_READ );
		curUser.busy();
		
		curUser.subClass = way;
		
		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.SND_MASTERY );
		
		SpellSprite.show( curUser, SpellSprite.MASTERY );
		curUser.sprite.emitter().burst( Speck.factory( Speck.MASTERY ), 12 );
		GLog.w( "You have chosen the way of the %s!", Utils.capitalize( way.title() ) );
		
		if (way == HeroSubClass.BERSERKER && curUser.HP <= curUser.HT * Fury.LEVEL) {
			Buff.affect( curUser, Fury.class );
		}
	}
}
