
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
 */package com.poorcoding.dixelpungeon.items.dixel.chemicals;

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Badges;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.effects.Splash;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.items.ItemStatusHandler;
import com.poorcoding.dixelpungeon.items.potions.PotionOfExperience;
import com.poorcoding.dixelpungeon.items.potions.PotionOfFrost;
import com.poorcoding.dixelpungeon.items.potions.PotionOfHealing;
import com.poorcoding.dixelpungeon.items.potions.PotionOfInvisibility;
import com.poorcoding.dixelpungeon.items.potions.PotionOfLevitation;
import com.poorcoding.dixelpungeon.items.potions.PotionOfLiquidFlame;
import com.poorcoding.dixelpungeon.items.potions.PotionOfMight;
import com.poorcoding.dixelpungeon.items.potions.PotionOfMindVision;
import com.poorcoding.dixelpungeon.items.potions.PotionOfParalyticGas;
import com.poorcoding.dixelpungeon.items.potions.PotionOfPurity;
import com.poorcoding.dixelpungeon.items.potions.PotionOfStrength;
import com.poorcoding.dixelpungeon.items.potions.PotionOfToxicGas;
import com.poorcoding.dixelpungeon.levels.Level;
import com.poorcoding.dixelpungeon.levels.Terrain;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.sprites.ItemSprite;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.windows.WndOptions;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.utils.Bundle;

import java.util.ArrayList;
import java.util.HashSet;

public class Chemical extends Item {

	public static final String AC_DRINK	= "INJECT";

	private static final String TXT_HARMFUL		= "Harmful chemical!";
	private static final String TXT_BENEFICIAL	= "Beneficial chemical";
	private static final String TXT_YES			= "Yes, I know what I'm doing";
	private static final String TXT_NO			= "No, I changed my mind";
	private static final String TXT_R_U_SURE_CONSUME =
		"Are you sure you want to inject it? In most cases you should throw such chemicals at your enemies.";
	private static final String TXT_R_U_SURE_THROW =
		"Are you sure you want to throw it? In most cases it makes sense to inject it.";

	private static final float TIME_TO_CONSUME = 2f;

	private static final Class<?>[] potions = {
		Steroids.class
	};
	private static final String[] colors = {
		"turquoise"
	};
	private static final Integer[] images = {
		ItemSpriteSheet.CHEM_TURQUOISE
	};

	private static ItemStatusHandler<Chemical> handler;

	private String color;

	{
		stackable = true;
		defaultAction = AC_DRINK;
	}

	@SuppressWarnings("unchecked")
	public static void initColors() {
		handler = new ItemStatusHandler<Chemical>( (Class<? extends Chemical>[])potions, colors, images );
	}

	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<Chemical>( (Class<? extends Chemical>[])potions, colors, images, bundle );
	}

	public Chemical() {
		super();
		image = handler.image( this );
		color = handler.label( this );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_DRINK );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {
		if (action.equals( AC_DRINK )) {
			
			if (isKnown() && (this instanceof Steroids)) {
				
					GameScene.show( 
						new WndOptions( TXT_HARMFUL, TXT_R_U_SURE_CONSUME, TXT_YES, TXT_NO ) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									drink( hero );
								}
							};
						}
					);
					
				} else {
					drink( hero );
				}
			
		} else {
			
			super.execute( hero, action );
			
		}
	}
	
	@Override
	public void doThrow( final Hero hero ) {

		if (isKnown() && (this instanceof Steroids)) {
		
			GameScene.show( 
				new WndOptions( TXT_BENEFICIAL, TXT_R_U_SURE_THROW, TXT_YES, TXT_NO ) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							Chemical.super.doThrow( hero );
						}
					};
				}
			);
			
		} else {
			super.doThrow( hero );
		}
	}
	
	protected void drink( Hero hero ) {
		
		detach( hero.belongings.backpack );
		
		hero.spend(TIME_TO_CONSUME);
		hero.busy();
		onThrow( hero.pos );
		
		Sample.INSTANCE.play( Assets.SND_DRINK );
		
		hero.sprite.operate( hero.pos );
	}
	
	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.hero.pos == cell) {
			
			apply( Dungeon.hero );
			
		} else if (Dungeon.level.map[cell] == Terrain.WELL || Level.pit[cell]) {
			
			super.onThrow( cell );
			
		} else  {
			
			shatter( cell );
			
		}
	}
	
	protected void apply( Hero hero ) {
		shatter( hero.pos );
	}
	
	public void shatter( int cell ) {
		if (Dungeon.visible[cell]) {
			GLog.i( "The syringe shatters and " + color() + " liquid splashes harmlessly" );
			Sample.INSTANCE.play( Assets.SND_SHATTER );
			splash( cell );
		}
	}
	
	public boolean isKnown() {
		return handler.isKnown( this );
	}
	
	public void setKnown() {
		if (!isKnown()) {
			handler.know( this );
		}
		
		Badges.validateAllPotionsIdentified();
	}
	
	@Override
	public Item identify() {
		setKnown();
		return this;
	}
	
	protected String color() {
		return color;
	}
	
	@Override
	public String name() {
		return "Syringe with " + (isKnown() ? name : color + " chemical");
	}
	
	@Override
	public String info() {
		return isKnown() ?
			desc() :
			"This syringe contains a swirling " + color + " chemical. " +
			"Who knows what it will do when injected or thrown?";
	}
	
	@Override
	public boolean isIdentified() {
		return isKnown();
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	public static HashSet<Class<? extends Chemical>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Chemical>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler.known().size() == potions.length;
	}
	
	protected void splash( int cell ) {		
		final int color = ItemSprite.pick( image, 8, 10 );
		Splash.at( cell, color, 5 );
	}
	
	@Override
	public int price() {
		return 20 * quantity;
	}
}
