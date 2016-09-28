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
package com.poorcoding.dixelpungeon.items.dixel.weapons.firearms;

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Badges;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.actors.Actor;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Invisibility;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.actors.hero.HeroClass;
import com.poorcoding.dixelpungeon.effects.MagicMissile;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.items.ItemStatusHandler;
import com.poorcoding.dixelpungeon.items.bags.Bag;
import com.poorcoding.dixelpungeon.items.dixel.weapons.Bullets;
import com.poorcoding.dixelpungeon.items.rings.RingOfPower.Power;
import com.poorcoding.dixelpungeon.items.weapon.melee.MeleeWeapon;
import com.poorcoding.dixelpungeon.mechanics.Ballistica;
import com.poorcoding.dixelpungeon.scenes.CellSelector;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.dixelpungeon.ui.QuickSlot;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.utils.Bundle;
import com.poorcoding.utils.Callback;
import com.poorcoding.utils.Random;

import java.util.ArrayList;

//public abstract class Firearm extends KindOfWeapon {
public abstract class Firearm extends MeleeWeapon {

	private static final int USAGES_TO_KNOW	= 25;

	public static final String AC_ZAP	= "FIRE";

	private static final String TXT_WOOD	= "This thin %s firearm is warm to the touch. Who knows what it will do when used?";
	private static final String TXT_DAMAGE	= "When this firearm is used as a melee weapon, its average damage is %d points per hit.";
	private static final String TXT_WEAPON	= "You can use this firearm as a melee weapon.";

	private static final String TXT_FIZZLES		= "your firearm clicks; you're still reloading.";
	private static final String TXT_SELF_TARGET	= "You can't target yourself";

	private static final String TXT_IDENTIFY	= "You are now familiar enough with your %s.";

	private static final float TIME_TO_ZAP	= 1f;

	public int maxCharges = initialCharges();
	public int curCharges = maxCharges;

	protected Charger charger;

	private boolean curChargeKnown = false;

	private int usagesToKnow = USAGES_TO_KNOW;

	protected boolean hitChars = true;

	private static final Class<?>[] wands = {
		PistolStandard.class,
		PistolFirebolt.class
	};
	/*private static final String[] woods =
		{"holly", "yew", "ebony", "cherry", "teak", "rowan", "willow", "mahogany", "bamboo", "purpleheart", "oak", "birch"};*/
	private static final String[] woods =
			{"dark gray", "shiny black", "purple"};
			//{"holly", "yew"};
	private static final Integer[] images = {
		ItemSpriteSheet.FIREARM_BROWN,
		ItemSpriteSheet.FIREARM_BLACK,
		ItemSpriteSheet.FIREARM_PURPLE
	};

	private static ItemStatusHandler<Firearm> handler;

	private String wood;

	{
		defaultAction = AC_ZAP;
	}

	@SuppressWarnings("unchecked")
	public static void initWoods() {
		handler = new ItemStatusHandler<Firearm>( (Class<? extends Firearm>[])wands, woods, images );
	}

	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<Firearm>( (Class<? extends Firearm>[])wands, woods, images, bundle );
	}

	public Firearm() {
		// public MeleeWeapon( int tier, float acu, float dly )
		super( 0, 1f, 0.5f );

		try {
			image = handler.image( this );
			wood = handler.label( this );
		} catch (Exception e) {
			// Wand of Magic Missile
		}
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );

		/*
		if (hero.heroClass != HeroClass.MAGE) {
			actions.remove( AC_EQUIP );
			actions.remove( AC_UNEQUIP );
		}*/

		if ((curCharges > 0 || !curChargeKnown )&& isEquipped( hero )) {
			actions.add(AC_ZAP);
		}

		return actions;
	}
	
	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		onDetach();
		return super.doUnequip( hero, collect, single );
	}
	
	@Override
	public void activate( Hero hero ) {
		charge( hero );
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( AC_ZAP )) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( zapper );
			
		} else {
			
			super.execute( hero, action );
			
		}
	}
	
	protected abstract void onZap( int cell );
	
	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (container.owner != null) {
				charge( container.owner );
			}
			return true;
		} else {
			return false;
		}
	};
	
	public void charge( Char owner ) {
		if (charger == null) {
			(charger = new Charger()).attachTo( owner );
		}
	}
	
	@Override
	public void onDetach( ) {
		stopCharging();
	}
	
	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}
	
	public int power() {
		int eLevel = effectiveLevel();
		if (charger != null) {
			Power power = charger.target.buff( Power.class );
			return power == null ? eLevel : Math.max( eLevel + power.level, 0 ); 
		} else {
			return eLevel;
		}
	}
	
	protected boolean isKnown() {
		return handler.isKnown( this );
	}
	
	public void setKnown() {
		if (!isKnown()) {
			handler.know( this );
		}
		
		Badges.validateAllWandsIdentified();
	}
	
	@Override
	public Item identify() {
		
		setKnown();
		curChargeKnown = true;
		super.identify();
		
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder( super.toString() );
		
		String status = status();
		if (status != null) {
			sb.append( " (" + status +  ")" );
		}
		
		if (isBroken()) {
			sb.insert( 0, "broken " );
		}
		
		return sb.toString();
	}
	
	@Override
	public String name() {
		return isKnown() ? name : wood + " firearm";
	}

	/*
	@Override
	public String info() {
		StringBuilder info = new StringBuilder( isKnown() ? desc() : String.format( TXT_WOOD, wood ) );
		return info.toString();
	}*/
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown() && curChargeKnown;
	}
	
	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}

	/*
	@Override
	public Item upgrade() {

		super.upgrade();
		
		updateLevel();
		curCharges = Math.min( curCharges + 1, maxCharges );
		updateQuickslot();
		
		return this;
	}*/
	
	@Override
	public Item degrade() {
		super.degrade();
		
		updateLevel();
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public int maxDurability( int lvl ) {

		//return 6 * (lvl < 16 ? 16 - lvl : 1);
		return 20 * (lvl < 16 ? 16 - lvl : 1);
	}
	
	protected void updateLevel() {
		maxCharges = Math.min( initialCharges() + level(), 9 );
		curCharges = Math.min( curCharges, maxCharges );
	}
	
	protected int initialCharges() {
		return 6;
	}
	
	@Override
	public int min() {
		int tier = 1 + effectiveLevel() / 3;
		return tier;
	}
	
	@Override
	public int max() {
		int level = effectiveLevel();
		int tier = 1 + level / 3;
		return (tier * tier - tier + 10) / 2 + level;
	}
	
	protected void fx( int cell, Callback callback ) {

		Sample.INSTANCE.play( Assets.SND_FIREARM);

		if (isEnchanted()) {
			Enchantment e = enchantment;
			String en = e.getClass().toString();
			int a = en.lastIndexOf(".") + 1;

			switch (en.substring(a).toUpperCase()) {
				case "FIRE":
					MagicMissile.fire(curUser.sprite.parent, curUser.pos, cell, callback);
					break;
				case "INSTABILTY":
				case "POISON":
					MagicMissile.purpleLight(curUser.sprite.parent, curUser.pos, cell, callback);
					break;
				case "DEATH":
				case "HORROR":
				case "LEECH":
				case "LUCK":
				case "TEMPERING":
					MagicMissile.whiteLight(curUser.sprite.parent, curUser.pos, cell, callback);
					break;
				case "PARALYSIS":
				case "SLOW":
					MagicMissile.foliage(curUser.sprite.parent, curUser.pos, cell, callback);
					break;
				case "SHOCK":
					MagicMissile.coldLight(curUser.sprite.parent, curUser.pos, cell, callback);
					break;
				default:
					MagicMissile.blueLight(curUser.sprite.parent, curUser.pos, cell, callback);
					break;
			}
		} else {
			MagicMissile.blueLight(curUser.sprite.parent, curUser.pos, cell, callback);
		}

	}

	protected void wandUsed() {

		// Use a bullet.
		Bullets bullet = Dungeon.hero.belongings.getItem(Bullets.class);
		if (bullet != null) {
			bullet.detach(Dungeon.hero.belongings.backpack);
		}

		curCharges--;
		if (!isIdentified() && --usagesToKnow <= 0) {
			identify();
			GLog.w( TXT_IDENTIFY, name() );
		} else {
			updateQuickslot();
		}

		use();
		
		curUser.spendAndNext( TIME_TO_ZAP );
	}
	
	@Override
	public Item random() {
		if (Random.Float() < 0.5f) {
			upgrade();
			if (Random.Float() < 0.15f) {
				upgrade();
			}
		}
		
		return this;
	}
	
	public static boolean allKnown() {
		return handler.known().size() == wands.length;
	}
	
	@Override
	public int price() {
		return considerState( 50 );
	}
	
	private static final String UNFAMILIRIARITY		= "unfamiliarity";
	private static final String MAX_CHARGES			= "maxCharges";
	private static final String CUR_CHARGES			= "curCharges";
	private static final String CUR_CHARGE_KNOWN	= "curChargeKnown";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, usagesToKnow );
		bundle.put( MAX_CHARGES, maxCharges );
		bundle.put( CUR_CHARGES, curCharges );
		bundle.put( CUR_CHARGE_KNOWN, curChargeKnown );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if ((usagesToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			usagesToKnow = USAGES_TO_KNOW;
		}
		maxCharges = bundle.getInt( MAX_CHARGES );
		curCharges = bundle.getInt( CUR_CHARGES );
		curChargeKnown = bundle.getBoolean( CUR_CHARGE_KNOWN );
	}

	@Override
	public void updateQuickslot() {
		super.updateQuickslot();
	}
	
	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {
		
		@Override
		public void onSelect( Integer target ) {

			if (!canZap()) {
				return;
			}
			
			if (target != null) {
				
				if (target == curUser.pos) {
					GLog.i( TXT_SELF_TARGET );
					return;
				}

				final Firearm curWand = (Firearm) Firearm.curItem;
				
				curWand.setKnown();
				
				//final int cell = Ballistica.cast( curUser.pos, target, true, curWand.hitChars );
				final int cell = Ballistica.cast( curUser.pos, target, false, curWand.hitChars );
				curUser.sprite.zap( cell );
				
				QuickSlot.target( curItem, Actor.findChar( cell ) );
				
				if (curWand.curCharges > 0) {
					
					curUser.busy();
					
					curWand.fx( cell, new Callback() {
						@Override
						public void call() {
							curWand.onZap( cell );
							curWand.wandUsed();
						}
					} );

					Invisibility.dispel();
					
				} else {
					
					curUser.spendAndNext( TIME_TO_ZAP );
					GLog.w( TXT_FIZZLES );
					curWand.levelKnown = true;
					
					curWand.updateQuickslot();
				}
				
			}
		}
		
		@Override
		public String prompt() {
			return "Direction to shoot";
		}
	};
	
	protected class Charger extends Buff {
		
		//private static final float TIME_TO_CHARGE = 40f;
		private static final float TIME_TO_CHARGE = 20f;
		
		@Override
		public boolean attachTo( Char target ) {
			super.attachTo( target );
			delay();
			
			return true;
		}
		
		@Override
		public boolean act() {
			
			if (curCharges < maxCharges) {
				curCharges++;
				updateQuickslot();
			}
			
			delay();
			
			return true;
		}
		
		protected void delay() {
			float time2charge = ((Hero)target).heroClass == HeroClass.MAGE ? 
				TIME_TO_CHARGE / (float)Math.sqrt( 1 + effectiveLevel() ) : 
				TIME_TO_CHARGE;
			spend( time2charge );
		}
	}

	public static boolean canZap() {
		if (curUser.heroClass != HeroClass.TECHNOPRANCER && !curItem.isEquipped(curUser)) {
			GLog.h("Needs to be equipped to use.");
			return false;
		}

		Bullets ammo = Dungeon.hero.belongings.getItem( Bullets.class );
		if (ammo == null) {
			GLog.h("You ain't got no bullets, min.");
			return false;
		}
		return true;
	}

}
