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

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.actors.buffs.Awareness;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Hunger;
import com.poorcoding.dixelpungeon.actors.buffs.Light;
import com.poorcoding.dixelpungeon.actors.buffs.MindVision;
import com.poorcoding.dixelpungeon.actors.buffs.Regeneration;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.actors.hero.HeroClass;
import com.poorcoding.dixelpungeon.effects.Speck;
import com.poorcoding.dixelpungeon.effects.particles.FlameParticle;
import com.poorcoding.dixelpungeon.effects.particles.SmokeParticle;
import com.poorcoding.dixelpungeon.items.potions.PotionOfMindVision;
import com.poorcoding.dixelpungeon.levels.CityLevel;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.sprites.CharSprite;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.noosa.particles.Emitter;
import com.poorcoding.utils.Random;

import java.util.ArrayList;

public class Drug extends Item {

	private static final String TXT_VALUE	= "%+d drug(s)";

	public static final String AC_USE	= "SMOKE";

	public static final float TIME_TO_LIGHT = 10f;
	
	{
		name = "wannabliss";
		//image = ItemSpriteSheet.DEWDROP;
		image = ItemSpriteSheet.WANNABLISS;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_USE );
		return actions;
	}

	@Override
	public boolean doPickUp( Hero hero ) {

		if (collect( hero.belongings.backpack )) {

			GameScene.pickUp( this );
			hero.sprite.emitter().burst( Speck.factory( Speck.STAR ), 3 );
			Sample.INSTANCE.play( Assets.SND_ITEM);
			//Sample.INSTANCE.play( Assets.SND_ITEM );
			hero.spendAndNext( TIME_TO_PICK_UP );

			GLog.i( "You pick up the wannabliss.");

			return true;

		} else {
			return false;
		}
	}

	@Override
	public void execute( Hero hero, String action ) {

		if (action == AC_USE) {

			hero.spend( TIME_TO_LIGHT );
			hero.busy();

			hero.sprite.operate( hero.pos );

			detach( hero.belongings.backpack );

			// AUDIO EFFECTS
			Sample.INSTANCE.play( Assets.SND_PUFF );

			// VISUAL EFFECTS
			//Emitter emitter = hero.sprite.centerEmitter();
			//emitter.start( FlameParticle.FACTORY, 0.2f, 3 );
			Emitter emitter = hero.sprite.emitter();
			emitter.start(SmokeParticle.FACTORY, 0.5f, 35);
			hero.sprite.emitter().burst( Speck.factory( Speck.STAR ), 50 );

			//GLog.i( "You smoke some " + this.name + ". ");

			// Affects - mind, perception, hunger up.
			//Buff.affect( hero, Light.class, Light.DURATION );
			//Buff.affect( hero, Awareness.class, Awareness.DURATION );

			// Below is the same effect as PotionOfMindVision
			Buff.affect( hero, MindVision.class, MindVision.DURATION );
			Dungeon.observe();

			if (Dungeon.level.mobs.size() > 0) {
				GLog.i(Utils.capitalize(this.name) + " affect: you sense the presence of many creatures nearby." );
			} else {
				GLog.i(Utils.capitalize(this.name) + " affect: you sense the presence of only a few creatures nearby." );
			}

			// Increase hunger.
			Dungeon.hero.buff(Hunger.class).satisfy(-50f);

			// Random chance actions
			switch (Random.Int(0,5)) {
				case 0:
					GLog.i( "... that's some gooooood sheee-iiit! :D ");
					break;
				case 1:
					GLog.i( " Your conscience reminds you: \"Winners Don't Do Drugs\".");
					break;
				default:
					break;
			}

		} else {
			super.execute( hero, action );
		}
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
	public int price() {
		return 10 * quantity;
	}
	@Override
	public String info() {
		return "A gram of Wannabliss, LelDorado's recreational drug of choice since the ancient days of yore.";
	}
}
