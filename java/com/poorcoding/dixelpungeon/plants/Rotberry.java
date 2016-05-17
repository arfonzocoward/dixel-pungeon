package com.poorcoding.dixelpungeon.plants;

import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.actors.Char;
import com.poorcoding.dixelpungeon.actors.blobs.Blob;
import com.poorcoding.dixelpungeon.actors.blobs.ToxicGas;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.buffs.Roots;
import com.poorcoding.dixelpungeon.actors.mobs.Mob;
import com.poorcoding.dixelpungeon.effects.CellEmitter;
import com.poorcoding.dixelpungeon.effects.Speck;
import com.poorcoding.dixelpungeon.items.bags.Bag;
import com.poorcoding.dixelpungeon.items.potions.PotionOfStrength;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.sprites.ItemSpriteSheet;
import com.poorcoding.dixelpungeon.utils.GLog;

public class Rotberry extends Plant {
	
	private static final String TXT_DESC = 
		"Berries of this shrub taste like sweet, sweet death.";
	
	{
		image = 7;
		plantName = "Rotberry";
	}
	
	@Override
	public void activate( Char ch ) {
		super.activate( ch );
		
		GameScene.add( Blob.seed( pos, 100, ToxicGas.class ) );
		
		Dungeon.level.drop( new Seed(), pos ).sprite.drop();
		
		if (ch != null) {
			Buff.prolong( ch, Roots.class, Roots.TICK * 3 );
		}
	}
	
	@Override
	public String desc() {
		return TXT_DESC;
	}
	
	public static class Seed extends Plant.Seed {
		{
			plantName = "Rotberry";
			
			name = "seed of " + plantName;
			image = ItemSpriteSheet.SEED_ROTBERRY;
			
			plantClass = Rotberry.class;
			alchemyClass = PotionOfStrength.class;
		}
		
		@Override
		public boolean collect( Bag container ) {
			if (super.collect( container )) {
				
				if (Dungeon.level != null) {
					for (Mob mob : Dungeon.level.mobs) {
						mob.beckon( Dungeon.hero.pos );
					}
					
					GLog.w( "The seed emits a roar that echoes throughout the dungeon!" );
					CellEmitter.center( Dungeon.hero.pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
					Sample.INSTANCE.play( Assets.SND_CHALLENGE );
				}
				
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public String desc() {
			return TXT_DESC;
		}
	}
}