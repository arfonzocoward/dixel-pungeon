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

import com.poorcoding.dixelpungeon.Assets;
import com.poorcoding.dixelpungeon.Badges;
import com.poorcoding.dixelpungeon.Chrome;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.Statistics;
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.actors.mobs.npcs.Blacksmith;
import com.poorcoding.dixelpungeon.items.EquipableItem;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.items.Soul;
import com.poorcoding.dixelpungeon.items.scrolls.ScrollOfUpgrade;
import com.poorcoding.dixelpungeon.plants.Plant;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.scenes.PixelScene;
import com.poorcoding.dixelpungeon.sprites.HeroSprite;
import com.poorcoding.dixelpungeon.sprites.ItemSprite;
import com.poorcoding.dixelpungeon.ui.Icons;
import com.poorcoding.dixelpungeon.ui.ItemSlot;
import com.poorcoding.dixelpungeon.ui.RedButton;
import com.poorcoding.dixelpungeon.ui.Window;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.dixelpungeon.utils.Utils;
import com.poorcoding.noosa.BitmapTextMultiline;
import com.poorcoding.noosa.NinePatch;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.noosa.ui.Component;

public class WndSoulforge extends Window {

	private static final int BTN_SIZE	= 36;
	private static final float GAP		= 2;
	private static final float BTN_GAP	= 10;
	private static final int WIDTH		= 116;

	private ItemButton btnPressed;

	private ItemButton btnItem1;
	private ItemButton btnItem2;
	private RedButton btnReforge;
	private RedButton btnExit;

	private static final String TXT_PROMPT =
		"Ok, a deal is a deal, dat's what I can do for you: I can reforge " +
		"2 items and turn them into one of a better quality.";
	private static final String TXT_SELECT =
		"Select an item to reforge";
	private static final String TXT_REFORGE =
		"Soulforge: ??? Souls";

	private static final String TXT_EXIT =
			"Leave Soulforge";

	private static final String TXT_LOOKS_BETTER	= "your %s certainly looks better now";

	private static final Integer SOUL_FACTOR = 5 + Dungeon.depth;

	//public WndSoulforge(Blacksmith troll, Hero hero ) {
	public WndSoulforge(Hero hero ) {
		
		super();
		
		IconTitle titlebar = new IconTitle();
		//titlebar.icon(new ItemSprite( new Item().image(), null ));
		titlebar.icon(new ItemSprite( new Soul().image(), null ));
		titlebar.color(0xFF44FF);
		titlebar.label("The Soulforge" );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );
		
		//BitmapTextMultiline message = PixelScene.createMultiline( TXT_PROMPT, 6 );
		BitmapTextMultiline message = PixelScene.createMultiline(
				//"You have " + Statistics.soulsCollected + " Souls to spend.\n" +
				"You have " + Dungeon.souls + " Souls to consume at the Soulforge.\n\nSelect an item to upgrade:",
				6 );
		message.maxWidth = WIDTH;
		message.measure();
		message.y = titlebar.bottom() + GAP;
		add( message );
		
		btnItem1 = new ItemButton() {
			@Override
			protected void onClick() {
				btnPressed = btnItem1;
				GameScene.selectItem( itemSelector, WndBag.Mode.UPGRADEABLE, TXT_SELECT );
			}
		};
		//btnItem1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.y + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
		btnItem1.setRect( WIDTH - BTN_SIZE*2 - BTN_GAP/2, message.y + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
		add( btnItem1 );

		/*
		btnItem2 = new ItemButton() {
			@Override
			protected void onClick() {
				btnPressed = btnItem2;
				GameScene.selectItem( itemSelector, WndBag.Mode.UPGRADEABLE, TXT_SELECT );
			}
		};
		btnItem2.setRect( btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE );
		add( btnItem2 );
		*/

		btnReforge = new RedButton( TXT_REFORGE ) {
			@Override
			protected void onClick() {
				//Blacksmith.upgrade( btnItem1.item, btnItem2.item );
				soul_upgrade(btnItem1.item);
				hide();
			}
		};
		btnReforge.enable( false );
		btnReforge.setRect( 0, btnItem1.bottom() + BTN_GAP, WIDTH, 20 );
		add( btnReforge );

		btnExit = new RedButton( TXT_EXIT ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnExit.enable( true );
		btnExit.setRect( 0, btnReforge.bottom() + BTN_GAP, WIDTH, 20 );
		add( btnExit );

		resize( WIDTH, (int)btnExit.bottom() );
	}
	
	protected WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				btnPressed.item( item );
				
				/*if (btnItem1.item != null && btnItem2.item != null) {
					String result = Blacksmith.verify( btnItem1.item, btnItem2.item );
					if (result != null) {
						GameScene.show( new WndMessage( result ) );
						btnReforge.enable( false );
					} else {
						btnReforge.enable( true );
					}
				}*/

				/* TODO: Validate item1 and worth to upgrade */
				int cost = item.price() * SOUL_FACTOR;
				//String result = "It'll cost " + cost + " Souls.";
				//GameScene.show( new WndMessage( result ) );

				btnReforge.text("Soulforge: " + cost + " Souls");

				if (Dungeon.souls >= cost) {
					btnReforge.enable( true );
				} else {
					btnReforge.enable( false );
				}
			}
		}
	};
	
	public static class ItemButton extends Component {
		
		protected NinePatch bg;
		protected ItemSlot slot;
		
		public Item item = null;
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			bg = Chrome.get( Chrome.Type.BUTTON );
			add( bg );
			
			slot = new ItemSlot() {
				@Override
				protected void onTouchDown() {
					bg.brightness( 1.2f );
					Sample.INSTANCE.play( Assets.SND_CLICK );
				};
				@Override
				protected void onTouchUp() {
					bg.resetColor();
				}
				@Override
				protected void onClick() {
					ItemButton.this.onClick();
				}
			};
			add( slot );
		}
		
		protected void onClick() {};
		
		@Override
		protected void layout() {	
			super.layout();
			
			bg.x = x;
			bg.y = y;
			bg.size( width, height );
			
			slot.setRect( x + 2, y + 2, width - 4, height - 4 );
		};
		
		public void item( Item item ) {
			slot.item( this.item = item );
		}
	}

	/* Dixel: upgrade a single item */
	public static void soul_upgrade( Item item1 ) {

		/* Upgrade item1 */

		int cost = item1.price() * SOUL_FACTOR;

		Sample.INSTANCE.play( Assets.SND_EVOKE );
		ScrollOfUpgrade.upgrade( Dungeon.hero );
		Item.evoke( Dungeon.hero );

		/*if (item1.isEquipped( Dungeon.hero )) {
			((EquipableItem)item1).doUnequip( Dungeon.hero, true );
		}*/
		item1.upgrade();

		Dungeon.souls -= cost;

		GLog.p( TXT_LOOKS_BETTER, item1.name() );
		Dungeon.hero.spendAndNext( 2f );
		Badges.validateItemLevelAquired( item1 );
	}

}
