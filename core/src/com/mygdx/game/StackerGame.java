package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.screen.MainScreen;


public class StackerGame extends Game {

    @Override
    public void create() {
        setScreen(new MainScreen());
    }
}
