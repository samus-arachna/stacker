package com.mygdx.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.StackerGame;

class GameOverScreen implements InputProcessor, Screen {

    // game object
    private StackerGame game;

    // text
    private Stage uiStage;
    private Label menuLabel;
    private Label finalScoreLabel;
    private Label highScoreLabel;

    GameOverScreen(StackerGame game, int finalScore) {
        // setup game
        this.game = game;

        // saving high score
        saveHighScore(finalScore);

        // setup input
        Gdx.input.setInputProcessor(this);

        // setup label score
        uiStage = new Stage();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSerif.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // adding menu label text
        parameter.size = Gdx.graphics.getWidth() / 25;
        BitmapFont font = generator.generateFont(parameter);
        String text = "Game over. Tap Screen To Play Again.";
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        menuLabel = new Label(text, style);
        menuLabel.setPosition(
                Gdx.graphics.getWidth() / 2 - (Gdx.graphics.getWidth() / 25 * 9),
                Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 2)
        );

        // adding final score label text
        parameter.size = Gdx.graphics.getWidth() / 20;
        font = generator.generateFont(parameter);
        text = "Final score: " + finalScore;
        style = new Label.LabelStyle(font, Color.WHITE);
        finalScoreLabel = new Label(text, style);
        finalScoreLabel.setPosition(
                Gdx.graphics.getWidth() / 2 - (Gdx.graphics.getWidth() / 20 * 4),
                Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 4)
        );

        // adding high score label text
        parameter.size = Gdx.graphics.getWidth() / 20;
        font = generator.generateFont(parameter);
        text = "High score: " + showHighScore();
        style = new Label.LabelStyle(font, Color.WHITE);
        highScoreLabel = new Label(text, style);
        highScoreLabel.setPosition(
                Gdx.graphics.getWidth() / 2 - (Gdx.graphics.getWidth() / 20 * 4),
                Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 5)
        );

        // disposing of font generator
        generator.dispose();

        // adding ui actors
        uiStage.addActor(menuLabel);
        uiStage.addActor(finalScoreLabel);
        uiStage.addActor(highScoreLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        uiStage.draw();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        game.setScreen(new MainScreen(game));
        return false;
    }

    private void saveHighScore(int newHighScore) {
        Preferences prefs = Gdx.app.getPreferences("stacker");
        int savedHighScore = prefs.getInteger("highScore");

        if (savedHighScore < newHighScore) {
            prefs.putInteger("highScore", newHighScore);
            prefs.flush();
        }
    }

    private int showHighScore() {
        Preferences prefs = Gdx.app.getPreferences("stacker");
        return prefs.getInteger("highScore");
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
