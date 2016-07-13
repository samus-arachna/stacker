package io.umren.stacker;

import com.badlogic.gdx.Game;
import io.umren.stacker.screen.MenuScreen;


public class StackerGame extends Game {

    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
