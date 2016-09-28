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
package com.poorcoding.dixelpungeon.windows;

import java.util.Locale;

import com.poorcoding.dixelpungeon.items.rings.RingOfHaggler;
import com.poorcoding.dixelpungeon.items.rings.RingOfHerbalism;
import com.poorcoding.gltextures.SmartTexture;
import com.poorcoding.gltextures.TextureCache;
import com.poorcoding.noosa.BitmapText;
import com.poorcoding.noosa.Group;
import com.poorcoding.noosa.Image;
import com.poorcoding.noosa.TextureFilm;
import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.Statistics;
import com.poorcoding.dixelpungeon.actors.buffs.Buff;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.scenes.PixelScene;
import com.poorcoding.dixelpungeon.ui.BuffIndicator;
import com.poorcoding.dixelpungeon.ui.RedButton;
import com.poorcoding.dixelpungeon.utils.Utils;

public class WndHero extends WndTabbed {
	
	private static final String TXT_STATS	= "Stats";
	private static final String TXT_BUFFS	= "Buffs";
	private static final String TXT_DIXEL	= "Dixel";
	
	private static final String TXT_EXP		= "Experience";
	private static final String TXT_STR		= "Strength";
	private static final String TXT_HEALTH	= "Health";
	private static final String TXT_GOLD	= "Gold Collected";
	private static final String TXT_DEPTH	= "Maximum Depth";

	private static final String TXT_SOULS	= "Souls";
	private static final String TXT_SOULSCOLLECTED	= "Souls Collected";
	private static final String TXT_CAMPS	= "Camp Rests";
	
	private static final int WIDTH		= 100;
	private static final int TAB_WIDTH	= 32;
	
	private StatsTab stats;
	private BuffsTab buffs;
	private DixelTab dixel;
	
	private SmartTexture icons;
	private TextureFilm film;
	
	public WndHero() {
		
		super();
		
		icons = TextureCache.get( Assets.BUFFS_LARGE );
		film = new TextureFilm( icons, 16, 16 );
		
		stats = new StatsTab();
		add( stats );
		
		buffs = new BuffsTab();
		add( buffs );

		// Add tab for Dixel
		dixel = new DixelTab();
		add( dixel );
		
		add( new LabeledTab( TXT_STATS ) {
			protected void select( boolean value ) {
				super.select( value );
				stats.visible = stats.active = selected;
			};
		} );
		add( new LabeledTab( TXT_BUFFS ) {
			protected void select( boolean value ) {
				super.select( value );
				buffs.visible = buffs.active = selected;
			};
		} );
		add( new LabeledTab( TXT_DIXEL ) {
			protected void select( boolean value ) {
				super.select( value );
				dixel.visible = dixel.active = selected;
			};
		} );
		for (Tab tab : tabs) {
			tab.setSize( TAB_WIDTH, tabHeight() );
		}
		
		resize( WIDTH, (int)Math.max( stats.height(), dixel.height() ) );
		
		select( 0 );
	}
	
	private class StatsTab extends Group {
		
		private static final String TXT_TITLE		= "Level %d %s";
		private static final String TXT_CATALOGUS	= "Catalogus";
		private static final String TXT_JOURNAL		= "Journal";
		//private static final String TXT_DIXEL		= "Dixel";
		
		private static final int GAP = 5;
		
		private float pos;
		
		public StatsTab() {
			
			Hero hero = Dungeon.hero; 

			BitmapText title = PixelScene.createText( 
				Utils.format( TXT_TITLE, hero.lvl, hero.className() ).toUpperCase( Locale.ENGLISH ), 9 );
			title.hardlight( TITLE_COLOR );
			title.measure();
			add( title );
			
			RedButton btnCatalogus = new RedButton( TXT_CATALOGUS ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndCatalogus() );
				}
			};
			btnCatalogus.setRect( 0, title.y + title.height(), btnCatalogus.reqWidth() + 2, btnCatalogus.reqHeight() + 2 );
			add( btnCatalogus );
			
			RedButton btnJournal = new RedButton( TXT_JOURNAL ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndJournal() );
				}
			};
			btnJournal.setRect( 
				btnCatalogus.right() + 1, btnCatalogus.top(), 
				btnJournal.reqWidth() + 2, btnJournal.reqHeight() + 2 );
			add( btnJournal );

			/* Dixel specific Stats button */
			/*
			RedButton btnDixel = new RedButton( TXT_DIXEL) {
				@Override
				protected void onClick() {
					//hide();
					//GameScene.show( new WndJournal() );
					GameScene.show( new WndError("Nothing here yet."));
				}
			};
			btnDixel.setRect(
					btnJournal.right() + 1, btnCatalogus.top(),
					btnDixel.reqWidth() + 2, btnDixel.reqHeight() + 2 );
			add( btnDixel );
			*/
			
			pos = btnCatalogus.bottom() + GAP;
			
			statSlot( TXT_STR, hero.STR() );
			statSlot( TXT_HEALTH, hero.HP + "/" + hero.HT );
			statSlot( TXT_EXP, hero.exp + "/" + hero.maxExp() );

			pos += GAP;
			
			statSlot( TXT_GOLD, Statistics.goldCollected );
			statSlot( TXT_DEPTH, Statistics.deepestFloor );
			
			pos += GAP;

			statSlot( TXT_SOULS, Dungeon.souls );
			pos += GAP;
		}
		
		private void statSlot( String label, String value ) {
			
			BitmapText txt = PixelScene.createText( label, 8 );
			txt.y = pos;
			add( txt );
			
			txt = PixelScene.createText( value, 8 );
			txt.measure();
			txt.x = PixelScene.align( WIDTH * 0.65f );
			txt.y = pos;
			add( txt );
			
			pos += GAP + txt.baseLine();
		}
		
		private void statSlot( String label, int value ) {
			statSlot( label, Integer.toString( value ) );
		}
		
		public float height() {
			return pos;
		}
	}
	
	private class BuffsTab extends Group {
		
		private static final int GAP = 2;
		
		private float pos;
		
		public BuffsTab() {
			for (Buff buff : Dungeon.hero.buffs()) {
				buffSlot( buff );
			}
		}
		
		private void buffSlot( Buff buff ) {
			
			int index = buff.icon();
			
			if (index != BuffIndicator.NONE) {
				
				Image icon = new Image( icons );
				icon.frame( film.get( index ) );
				icon.y = pos;
				add( icon );
				
				BitmapText txt = PixelScene.createText( buff.toString(), 8 );
				txt.x = icon.width + GAP;
				txt.y = pos + (int)(icon.height - txt.baseLine()) / 2;
				add( txt );
				
				pos += GAP + icon.height;
			}
		}
		
		public float height() {
			return pos;
		}
	}

	private class DixelTab extends Group {

		private static final String TXT_TITLE		= "Dixel Pungeon Stuff";

		private static final int GAP = 2;

		private float pos;

		public DixelTab() {
			Hero hero = Dungeon.hero;

			BitmapText title = PixelScene.createText(
					Utils.format( TXT_TITLE, hero.lvl, hero.className() ).toUpperCase( Locale.ENGLISH ), 9 );
			//title.hardlight( TITLE_COLOR );
			title.hardlight( 0xFF44FF);
			title.measure();
			add( title );

			pos = title.baseLine() + GAP + GAP;

			dixelSlot( TXT_SOULSCOLLECTED, Statistics.soulsCollected );
			pos += GAP;

			dixelSlot( TXT_CAMPS, Statistics.campfiresRested );
			pos += GAP;

			if (hero.buff(RingOfHaggler.Haggling.class) != null) {
				dixelSlot("Haggling", hero.buff(RingOfHaggler.Haggling.class).level);
			} else {
				dixelSlot("Haggling", 0);
			}
			pos += GAP;

			if (hero.buff(RingOfHerbalism.Herbalism.class) != null) {
				dixelSlot( "Herbalism", hero.buff(RingOfHerbalism.Herbalism.class ).level );
			} else {
				dixelSlot("Herbalism", 0);
			}
			pos += GAP;

			dixelSlot( "Level size", Dungeon.level.WIDTH + "x" + Dungeon.level.HEIGHT);
			pos += GAP;

			dixelSlot( "MOBs this level", Dungeon.level.nMobs());
			pos += GAP;
		}

		private void dixelSlot( String label, String value ) {

			BitmapText txt = PixelScene.createText( label, 8 );
			txt.y = pos;
			add( txt );

			txt = PixelScene.createText( value, 8 );
			txt.measure();
			txt.x = PixelScene.align( WIDTH * 0.65f );
			txt.y = pos;
			add( txt );

			pos += GAP + txt.baseLine();
		}

		private void dixelSlot( String label, int value ) {
			dixelSlot( label, Integer.toString( value ) );
		}

		public float height() {
			return pos;
		}
	}
}
