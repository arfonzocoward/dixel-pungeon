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
package com.poorcoding.dixelpungeon.actors.hero;

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Badges;
import com.poorcoding.dixelpungeon.DixelPungeon;
import com.poorcoding.dixelpungeon.items.Ankh;
import com.poorcoding.dixelpungeon.items.TomeOfMastery;
import com.poorcoding.dixelpungeon.items.armor.ClothArmor;
import com.poorcoding.dixelpungeon.items.bags.Keyring;
import com.poorcoding.dixelpungeon.items.dixel.chemicals.Gwailoprin;
import com.poorcoding.dixelpungeon.items.dixel.chemicals.Mamba;
import com.poorcoding.dixelpungeon.items.dixel.chemicals.StemCells;
import com.poorcoding.dixelpungeon.items.dixel.chemicals.Steroids;
import com.poorcoding.dixelpungeon.items.dixel.weapons.Bullets;
import com.poorcoding.dixelpungeon.items.dixel.weapons.firearms.PistolFirebolt;
import com.poorcoding.dixelpungeon.items.dixel.weapons.firearms.PistolStandard;
import com.poorcoding.dixelpungeon.items.food.Food;
import com.poorcoding.dixelpungeon.items.potions.PotionOfStrength;
import com.poorcoding.dixelpungeon.items.rings.RingOfMending;
import com.poorcoding.dixelpungeon.items.rings.RingOfShadows;
import com.poorcoding.dixelpungeon.items.scrolls.ScrollOfIdentify;
import com.poorcoding.dixelpungeon.items.scrolls.ScrollOfMagicMapping;
import com.poorcoding.dixelpungeon.items.wands.WandOfMagicMissile;
import com.poorcoding.dixelpungeon.items.weapon.melee.Dagger;
import com.poorcoding.dixelpungeon.items.weapon.melee.Knuckles;
import com.poorcoding.dixelpungeon.items.weapon.melee.ShortSword;
import com.poorcoding.dixelpungeon.items.weapon.missiles.Dart;
import com.poorcoding.dixelpungeon.items.weapon.missiles.Boomerang;
import com.poorcoding.dixelpungeon.ui.QuickSlot;
import com.poorcoding.utils.Bundle;

public enum HeroClass {

	WARRIOR( "warrior" ), MAGE( "mage" ), ROGUE( "rogue" ), HUNTRESS( "huntress" ), TECHNOPRANCER( "technoprancer"), PEPE( "pepe" ) ;
	
	private String title;
	
	private HeroClass( String title ) {
		this.title = title;
	}
	
	public static final String[] WAR_PERKS = {
		"Warriors start with 11 points of Strength.",
		"Warriors start with a unique short sword. This sword can be later \"reforged\" to upgrade another melee weapon.",
		"Warriors are less proficient with missile weapons.",
		"Any piece of food restores some health when eaten.",
		"Potions of Strength are identified from the beginning.",
	};
	
	public static final String[] MAG_PERKS = {
		"Mages start with a unique Wand of Magic Missile. This wand can be later \"disenchanted\" to upgrade another wand.",
		"Mages recharge their wands faster.",
		"When eaten, any piece of food restores 1 charge for all wands in the inventory.",
		"Mages can use wands as a melee weapon.",
		"Scrolls of Identify are identified from the beginning."
	};
	
	public static final String[] ROG_PERKS = {
		"Rogues start with a Ring of Shadows+1.",
		"Rogues identify a type of a ring on equipping it.",
		"Rogues are proficient with light armor, dodging better while wearing one.",
		"Rogues are proficient in detecting hidden doors and traps.",
		"Rogues can go without food longer.",
		"Scrolls of Magic Mapping are identified from the beginning."
	};

	public static final String[] TEC_PERKS = {
		"Technoprancers start with 9 points of Health",
		"Technoprancers start with a unique pistol: Jengo's Justice. (+unique)",
		"Technoprancers are proficient with Pistols, dealing more damage with them than other classes. (+pistol)",
		"Technoprancers do not need to equip Firearms to use them. (+firearm)",
		"Scrolls of Identify are identified from the beginning. (+)"
	};
	
	public static final String[] HUN_PERKS = {
		"Huntresses start with 15 points of Health.",
		"Huntresses start with a unique upgradeable boomerang.",
		"Huntresses are proficient with missile weapons and get a damage bonus for excessive strength when using them.",
		"Huntresses gain more health from dewdrops.",
		"Huntresses sense neighbouring monsters even if they are hidden behind obstacles."
	};

	public static final String[] PEP_PERKS = {
		"Pepe uses what he finds around, he has no specialities nor shortcomings.",
		"Pepe starts with a Ring of Mending+1.",
		"Pepe starts with all Chemicals: one of each kind."
	};
	
	public void initHero( Hero hero ) {
		
		hero.heroClass = this;
		
		initCommon( hero );
		
		switch (this) {
			case WARRIOR:
				initWarrior(hero);
				break;

			case MAGE:
				initMage(hero);
				break;

			case ROGUE:
				initRogue(hero);
				break;

			case HUNTRESS:
				initHuntress(hero);
				break;

			case TECHNOPRANCER:
				initTechnoprancer(hero);
				break;

			case PEPE:
				initPepe(hero);
				break;
		}

		if (Badges.isUnlocked( masteryBadge() )) {
			new TomeOfMastery().collect();
		}

		hero.updateAwareness();
	}
	
	private static void initCommon( Hero hero ) {
		(hero.belongings.armor = new ClothArmor()).identify();

		new Food().identify().collect();

		if (DixelPungeon.DIXEL_DEBUG) {
			// Add 10 units of food to start.
			int i = 0;
			while (i < 10) {
				new Food().identify().collect();
				i++;
			}

			new Ankh().identify().collect();

			new PistolStandard().identify().collect();
			new Bullets(10).identify().collect();
		}

		new Keyring().collect();
	}
	
	public Badges.Badge masteryBadge() {
		switch (this) {
		case WARRIOR:
			return Badges.Badge.MASTERY_WARRIOR;
		case MAGE:
			return Badges.Badge.MASTERY_MAGE;
		case ROGUE:
			return Badges.Badge.MASTERY_ROGUE;
		case HUNTRESS:
			return Badges.Badge.MASTERY_HUNTRESS;
		}
		return null;
	}
	
	private static void initWarrior( Hero hero ) {
		hero.STR = hero.STR + 1;
		
		(hero.belongings.weapon = new ShortSword()).identify();
		new Dart( 8 ).identify().collect();
		
		QuickSlot.primaryValue = Dart.class;
		
		new PotionOfStrength().setKnown();
	}
	
	private static void initMage( Hero hero ) {	
		(hero.belongings.weapon = new Knuckles()).identify();
		
		WandOfMagicMissile wand = new WandOfMagicMissile();
		wand.identify().collect();
		
		QuickSlot.primaryValue = wand;
		
		new ScrollOfIdentify().setKnown();
	}
	
	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();
		(hero.belongings.ring1 = new RingOfShadows()).upgrade().identify();
		new Dart( 8 ).identify().collect();
		
		hero.belongings.ring1.activate( hero );
		
		QuickSlot.primaryValue = Dart.class;
		
		new ScrollOfMagicMapping().setKnown();
	}
	
	private static void initHuntress( Hero hero ) {
		
		hero.HP = (hero.HT -= 5);
		
		(hero.belongings.weapon = new Dagger()).identify();
		Boomerang boomerang = new Boomerang();
		boomerang.identify().collect();
		
		QuickSlot.primaryValue = boomerang;
	}

	private static void initTechnoprancer( Hero hero ) {
		hero.STR = hero.STR - 1;

		new ScrollOfIdentify().setKnown();

		(hero.belongings.weapon = new Dagger()).identify();

		new Bullets( 50 ).identify().collect();

		new PistolFirebolt().identify().collect();
		//QuickSlot.primaryValue = PistolFirebolt.class;
	}

	private static void initPepe( Hero hero ) {
		(hero.belongings.ring1 = new RingOfMending()).upgrade().identify();

		Mamba mamba = new Mamba();
		mamba.quantity(1);
		mamba.identify().collect();

		Steroids steroids = new Steroids();
		steroids.quantity(1);
		steroids.identify().collect();

		Gwailoprin gwailoprin = new Gwailoprin();
		gwailoprin.quantity(1);
		gwailoprin.identify().collect();

		StemCells stemcells = new StemCells();
		stemcells.quantity(1);
		stemcells.identify().collect();
	}
	
	public String title() {
		return title;
	}
	
	public String spritesheet() {
		
		switch (this) {
		case WARRIOR:
			return Assets.WARRIOR;
		case MAGE:
			return Assets.MAGE;
		case ROGUE:
			return Assets.ROGUE;
		case HUNTRESS:
			return Assets.HUNTRESS;
		case TECHNOPRANCER:
			return Assets.TECHNOPRANCER;
		case PEPE:
			return Assets.PEPE;
		}
		
		return null;
	}
	
	public String[] perks() {
		
		switch (this) {
		case WARRIOR:
			return WAR_PERKS;
		case MAGE:
			return MAG_PERKS;
		case ROGUE:
			return ROG_PERKS;
		case HUNTRESS:
			return HUN_PERKS;
		case TECHNOPRANCER:
			return TEC_PERKS;
		case PEPE:
			return  PEP_PERKS;
		}
		
		return null;
	}

	private static final String CLASS	= "class";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}
	
	public static HeroClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : ROGUE;
	}

	public String getCharName() {
		switch (this) {
			case WARRIOR:
				return "Simon";
			case MAGE:
				return "Arfonzo J. Coward";
			case ROGUE:
				return "Matteo";
			case HUNTRESS:
				return "Funzetta";
			case TECHNOPRANCER:
				return "Jengus Roundstone";
			case PEPE:
				return "Pepe the Frog";
		}

		return null;
	}
}
