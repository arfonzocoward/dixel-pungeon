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
import com.poorcoding.dixelpungeon.actors.hero.Hero;
import com.poorcoding.dixelpungeon.items.Item;
import com.poorcoding.dixelpungeon.items.Soul;
import com.poorcoding.dixelpungeon.items.scrolls.ScrollOfUpgrade;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.scenes.PixelScene;
import com.poorcoding.dixelpungeon.sprites.ItemSprite;
import com.poorcoding.dixelpungeon.ui.ItemSlot;
import com.poorcoding.dixelpungeon.ui.RedButton;
import com.poorcoding.dixelpungeon.ui.Window;
import com.poorcoding.dixelpungeon.utils.GLog;
import com.poorcoding.noosa.BitmapTextMultiline;
import com.poorcoding.noosa.NinePatch;
import com.poorcoding.noosa.audio.Sample;
import com.poorcoding.noosa.ui.Component;

public class WndSoulforge extends Window {

	private float pos;

	private static final int BTN_HEIGHT	= 20;
	private static final int BTN_SIZE	= 36;
	private static final float GAP		= 2;
	private static final float BTN_GAP	= 4;
	private static final int WIDTH		= 128;

	private ItemButton btnPressed;

	private ItemButton btnItem1;
	private ItemButton btnItem2;
	private RedButton btnRepair;
	private RedButton btnReforge;
	private RedButton btnExit;
	private RedButton btnMinus;
	private RedButton btnPlus;

	private static final String TXT_SELECT =
		"Select an item to upgrade or repair:";
	private static final String TXT_REPAIR =
			"Choose item to repair";
	private static final String TXT_REFORGE =
		"Choose item to upgrade";

	private static final String TXT_EXIT =
			"Leave Soulforge";

	private static final String TXT_LOOKS_BETTER	= "your %s looks much better now";

	private static final String TXT_REPAIRED	= "you repair the %s for %d point(s)";

	private static Integer SOUL_UPGRADE_FACTOR = 5 + Dungeon.hero.lvl;
	private static Integer SOUL_REPAIR_FACTOR = 2 + (Math.round(Math.max(1,Dungeon.hero.lvl)/2));

	private static Integer repairAmount = 0;

	//public WndSoulforge(Blacksmith troll, Hero hero ) {
	public WndSoulforge(Hero hero ) {
		super();

		//repairAmount = 0;
		
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
				"You have " + Dungeon.souls + " Souls to consume at the Soulforge." + " Factor " + SOUL_UPGRADE_FACTOR + "\n\nSelect an item to upgrade:",
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

				/*
				if (btnItem1.item != null) {
					//repairAmount = 0;
					calc_repairs_affordable(btnItem1.item);
					calc_repairs(btnItem1.item, btnRepair, btnMinus, btnPlus);
				}
				*/
			}
		};
		btnItem1.setRect( (WIDTH - BTN_SIZE)/2, message.y + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
		add( btnItem1 );

		pos = btnItem1.bottom();

		btnMinus = new RedButton("-") {
			@Override
			protected void onClick() {
				//hide();
				if (repairAmount >= 1) {
					repairAmount--;
					calc_repairs(btnItem1.item, btnRepair, btnMinus, btnPlus);
				}
			}
		};
		btnMinus.enable(false);

		btnPlus = new RedButton("+") {
			@Override
			protected void onClick() {
				//hide();
				if ((btnItem1.item.durability() + repairAmount) < btnItem1.item.maxDurability()) {
					repairAmount++;
					calc_repairs(btnItem1.item, btnRepair, btnMinus, btnPlus);
				}
			}
		};
		btnPlus.enable(false);

		addButtons( btnMinus, btnPlus);

		btnRepair = new RedButton( TXT_REPAIR  ) {
			@Override
			protected void onClick() {
				//Blacksmith.upgrade( btnItem1.item, btnItem2.item );
				soul_repair(btnItem1.item);
				hide();
			}
		};
		btnRepair.enable( false );
		btnRepair.setRect( 0, btnPlus.bottom() + BTN_GAP, WIDTH, 20 );
		add( btnRepair );

		btnReforge = new RedButton( TXT_REFORGE ) {
			@Override
			protected void onClick() {
				//Blacksmith.upgrade( btnItem1.item, btnItem2.item );
				soul_upgrade(btnItem1.item);
				hide();
			}
		};
		btnReforge.enable( false );
		btnReforge.setRect( 0, btnRepair.bottom() + BTN_GAP, WIDTH, 20 );
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

				/* item1 cost to repair */
				calc_repairs_affordable(btnItem1.item);
				calc_repairs( item, btnRepair, btnMinus, btnPlus);

				/* item1 cost to upgrade */
				int cost = item.price() * SOUL_UPGRADE_FACTOR;
				btnReforge.text("Upgrade: " + cost + " Souls");

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

		int cost = item1.price() * SOUL_UPGRADE_FACTOR;

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

	/* Dixel: repair a single item */
	public static void soul_repair( Item item1 ) {
		Sample.INSTANCE.play( Assets.SND_EVOKE );
		Dungeon.souls -= repairAmount * SOUL_REPAIR_FACTOR;
		item1.setDurability(repairAmount);
		GLog.p( TXT_REPAIRED, item1.name(), repairAmount );
		Dungeon.hero.spendAndNext( 9f );
	}

	private void addButtons( RedButton btn1, RedButton btn2 ) {
		add( btn1 );
		btn1.setRect( 0, pos > 0 ? pos += GAP : 0, (WIDTH - GAP) / 2, BTN_HEIGHT );
		add( btn2 );
		btn2.setRect( btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}

	/*private static void calc_repairs( Item item, RedButton btnRepair, RedButton btnMinus, RedButton btnPlus ) {
		calc_repairs(item, item.maxDurability() - item.durability(), btnRepair,btnMinus,btnPlus);
	}*/

	private static void calc_repairs_affordable (Item item) {
		Item curItem = item;

		/*
		if ((repairAmount + curItem.durability()) > curItem.maxDurability()) {
			//GameScene.show(new WndDixel(repairAmount + "+" + curItem.durability() + " vs " + curItem.maxDurability()));
			repairAmount =  curItem.maxDurability() - curItem.durability();
		}
		*/
		repairAmount =  curItem.maxDurability() - curItem.durability();

		// work out automatically the max affordable
		int repairMax = curItem.maxDurability() - curItem.durability();
		int repairMaxCost = repairMax * SOUL_REPAIR_FACTOR;

		if (repairMaxCost > Dungeon.souls) {
			// Work downwards.
			int r = repairMax;
			int c = repairMaxCost;

			while (c > Dungeon.souls){
				r--;
				c = r * SOUL_REPAIR_FACTOR;
			}

			repairAmount = r;
		}

		//GameScene.show(new WndDixel("Repair Amount: " + repairAmount));
	}

	private static void calc_repairs( Item item, RedButton btnRepair, RedButton btnMinus, RedButton btnPlus ) {

		if (item.durability() == item.maxDurability()) {
			//GameScene.show(new WndDixel("Repair Amount set to zero"));
			repairAmount = 0;
		}

		int repairCost = repairAmount * SOUL_REPAIR_FACTOR;
		btnRepair.text("Repair " + repairAmount + " (" + (item.durability() + repairAmount) + "/" + item.maxDurability() + "): " + repairCost + " Souls");

		btnMinus.enable(true);
		btnPlus.enable(true);

		// If repairAmount > 0, and can afford, enable button.
		if ((repairAmount > 0) && (repairCost < Dungeon.souls) ) {
			btnRepair.enable( true );
		} else {
			btnRepair.enable( false );
		}


	}

	public static void updateSoulFactor() {
		SOUL_UPGRADE_FACTOR = 5 + Dungeon.hero.lvl;
	}
}
