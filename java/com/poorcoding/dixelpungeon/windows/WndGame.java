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

import java.io.IOException;

import com.poorcoding.dixelpungeon.levels.Terrain;
import com.poorcoding.noosa.Game;
import com.poorcoding.dixelpungeon.Dungeon;
import com.poorcoding.dixelpungeon.DixelPungeon;
import com.poorcoding.dixelpungeon.scenes.GameScene;
import com.poorcoding.dixelpungeon.scenes.InterlevelScene;
import com.poorcoding.dixelpungeon.scenes.RankingsScene;
import com.poorcoding.dixelpungeon.scenes.TitleScene;
import com.poorcoding.dixelpungeon.ui.Icons;
import com.poorcoding.dixelpungeon.ui.RedButton;
import com.poorcoding.dixelpungeon.ui.Window;

public class WndGame extends Window {
	
	private static final String TXT_SETTINGS	= "Settings";
	private static final String TXT_CHALLEGES	= "Challenges";
	private static final String TXT_RANKINGS	= "Rankings";
	private static final String TXT_START		= "Start New Game";
	private static final String TXT_MENU		= "Main Menu";
	private static final String TXT_EXIT		= "Exit Game";
	private static final String TXT_RETURN		= "Return to Game";
	// Dixel specifics
	private static final String TXT_DIXEL		= "About Dixel Pungeon";
	private static final String TXT_SOULFORGE		= "Soulforge";
	
	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP		= 2;
	
	private int pos;
	
	public WndGame() {
		
		super();

		// Display Soulforge button only at Campfires.
		if (Dungeon.level.map[Dungeon.hero.pos] == Terrain.CAMPFIRE) {
			addButton(new RedButton(TXT_SOULFORGE) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show(new WndSpendSouls(Dungeon.hero));
				}
			});
		}

		addButton( new RedButton( TXT_SETTINGS ) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show( new WndSettings( true ) );
			}
		} );
		
		if (Dungeon.challenges > 0) {
			addButton( new RedButton( TXT_CHALLEGES ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndChallenges( Dungeon.challenges, false ) );
				}
			} );
		}
		
		if (!Dungeon.hero.isAlive()) {
			
			RedButton btnStart;
			addButton( btnStart = new RedButton( TXT_START ) {
				@Override
				protected void onClick() {
					Dungeon.hero = null;
					DixelPungeon.challenges( Dungeon.challenges );
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					InterlevelScene.noStory = true;
					Game.switchScene( InterlevelScene.class );
				}
			} );
			btnStart.icon( Icons.get( Dungeon.hero.heroClass ) );
			
			addButton( new RedButton( TXT_RANKINGS ) {
				@Override
				protected void onClick() {
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					Game.switchScene( RankingsScene.class );
				}
			} );
		}

		addButton(new RedButton( TXT_DIXEL ) {
			@Override
			protected void onClick() {
				hide();
				//GameScene.show( new WndSettings( true ) );

				GameScene.show( new WndDixel(
						"Remember: keep smile lads, keep smile!\n:D :D :D\n" +
								"\n\nDarkest condolences,\n" +
								"Arfonzo J. Coward\n" +
								"<arfonzo@gmail.com>"
				) );

				GameScene.show( new WndDixel(
						"ABOUT DIXEL PUNGEON\n\n" +
								"Key codebase changes from Pixel Dungeon:\n\n" +
								"- Souls: collect souls for upgrade things in Soulforges.\n"+
								"- All classes start with 10 food rations.\n" +
								"- New MOBs: Goblin Scout.\n"+
								"- Chance to drop food when MOBs die.\n" +
								"- Campfires to rest and use Soulforge.\n" +
								"\n\n\n\n(scroll 2 of 2)"

				) );

				GameScene.show( new WndDixel(
						"ABOUT DIXEL PUNGEON\n\n" +
						"Dixel Pungeon is a fork of Pixel Dungeon.\n\n" +
								"It contains some changes and additions to the codebase which will alter your experience if you have played Pixel Dungeon in the past.\n\n" +
								"Dixel is about exploration. It also makes starting off slightly easier." +
								"\n\n\n\n(scroll 1 of 2)"
				) );

			}
		} );
				
		addButtons( 
			new RedButton( TXT_MENU ) {
				@Override
				protected void onClick() {
					try {
						Dungeon.saveAll();
					} catch (IOException e) {
						// Do nothing
					}
					Game.switchScene( TitleScene.class );
				}
			}, new RedButton( TXT_EXIT ) {
				@Override
				protected void onClick() {
					Game.instance.finish();
				}
			} 
		);
		
		addButton( new RedButton( TXT_RETURN ) {
			@Override
			protected void onClick() {
				hide();
			}
		} );
		
		resize( WIDTH, pos );
	}
	
	private void addButton( RedButton btn ) {
		add( btn );
		btn.setRect( 0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}
	
	private void addButtons( RedButton btn1, RedButton btn2 ) {
		add( btn1 );
		btn1.setRect( 0, pos > 0 ? pos += GAP : 0, (WIDTH - GAP) / 2, BTN_HEIGHT );
		add( btn2 );
		btn2.setRect( btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}
}
