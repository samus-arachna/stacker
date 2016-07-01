package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.mygdx.game.screen.MainScreen;
import com.mygdx.game.screen.MenuScreen;


public class StackerGame extends Game {

    @Override
    public void create() {
        Screen main = new MainScreen();
        setScreen(main);
    }
}
