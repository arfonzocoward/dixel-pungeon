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
package com.poorcoding.dixelpungeon.scenes;

import android.content.Intent;
import android.net.Uri;

import com.poorcoding.dixelpungeon.effects.Fireball;
import com.poorcoding.input.Touchscreen.Touch;
import com.poorcoding.noosa.BitmapTextMultiline;
import com.poorcoding.noosa.Camera;
import com.poorcoding.noosa.Game;
import com.poorcoding.noosa.Image;
import com.poorcoding.noosa.TouchArea;
import com.poorcoding.dixelpungeon.DixelPungeon;
import com.poorcoding.dixelpungeon.effects.Flare;
import com.poorcoding.dixelpungeon.ui.Archs;
import com.poorcoding.dixelpungeon.ui.ExitButton;
import com.poorcoding.dixelpungeon.ui.Icons;
import com.poorcoding.dixelpungeon.ui.Window;

public class AboutScene extends PixelScene {

	private static final String TXT =
		"DIXEL PUNGEON:\n" +
		"About: a fork of Pixel Dungeon.\n" +
		"Code: Arfonzo J. Coward <arfonzo@gmail.com>\n" +
		"\n\n\nPIXEL DUNGEON:\n" +
		"Code & graphics: Watabou\n" +
		"Music: Cube_Code\n\n" + 
		"This game is inspired by Brian Walker's Brogue. " +
		"Try it on Windows, Mac OS or Linux - it's awesome! ;)\n\n" +
		"Please visit official website for additional info:";
	
	private static final String LNK = "pixeldungeon.watabou.ru";
	
	@Override
	public void create() {
		super.create();
		
		BitmapTextMultiline text = createMultiline( TXT, 8 );
		text.maxWidth = Math.min( Camera.main.width, 120 );
		text.measure();
		add( text );
		
		text.x = align( (Camera.main.width - text.width()) / 2 );
		text.y = align( (Camera.main.height - text.height()) / 2 );
		
		BitmapTextMultiline link = createMultiline( LNK, 8 );
		link.maxWidth = Math.min( Camera.main.width, 120 );
		link.measure();
		link.hardlight( Window.TITLE_COLOR );
		add( link );
		
		link.x = text.x;
		link.y = text.y + text.height();
		
		TouchArea hotArea = new TouchArea( link ) {
			@Override
			protected void onClick( Touch touch ) {
				Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "http://" + LNK ) );
				Game.instance.startActivity( intent );
			}
		};
		add( hotArea );

		// PixelOungeon original icon.
		Image wata = Icons.WATA.get();
		wata.x = align( (Camera.main.width - wata.width) / 2 );
		wata.y = text.y - wata.height + 52;
		add( wata );

		new Flare( 7, 64 ).color( 0x112233, true ).show( wata, 0 ).angularSpeed = +20;

		// DixelPungeon icon.

		Image iconDp= Icons.SEED_POUCH.get();
		iconDp.x = align( (Camera.main.width - iconDp.width) / 2 );
		iconDp.y = text.y - iconDp.height - 8;
		add( iconDp );

		new Flare( 7, 64 ).color( 0x117733, true ).show( iconDp, 0 ).angularSpeed = +40;
		
		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );
		
		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );
		
		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		DixelPungeon.switchNoFade( TitleScene.class );
	}
}
