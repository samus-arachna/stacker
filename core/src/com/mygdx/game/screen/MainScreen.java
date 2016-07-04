package com.mygdx.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameState;
import com.mygdx.game.StackerGame;

import static java.lang.Math.abs;

public class MainScreen implements InputProcessor, Screen {
    // setup game
    private StackerGame game;

    // environment
    private OrthographicCamera camera;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private ModelInstance modelInstance;
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private Environment environment;
    private ShapeRenderer shapeRenderer;

    // colors
    private Color boxColor = new Color(157/255f, 227/255f, 255/255f, 1);

    // game logic
    private char boxMove = '+';
    private Vector3 boxPosition;
    private int boxLevel = 0;
    private float cameraLevel = 7f;

    // text
    private Stage uiStage;
    private int score;
    private Label scoreLabel;

    public MainScreen(StackerGame game) {
        // setup game
        this.game = game;

        // setup camera
        camera = new OrthographicCamera(480, 800);
        camera.position.set(5f, cameraLevel, 5f);
        camera.lookAt(0f, 0f, 0f);
        camera.zoom = 0.03f;

        // setup model
        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createBox(
                5f, 1f, 5f,
                new Material(ColorAttribute.createDiffuse(boxColor)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal
        );
        modelInstance = new ModelInstance(box, 0, boxLevel++, 0);
        instances.add(modelInstance);
        spawnNewBox(5f, 1f, 5f);

        // setup env
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -5.8f, -0.2f));


        // setup background gradient
        shapeRenderer = new ShapeRenderer();

        // setup input
        Gdx.input.setInputProcessor(this);

        boxPosition = new Vector3();

        // setup label score
        uiStage = new Stage();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSerif.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        String text = "Score: 0";
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        scoreLabel = new Label(text, style);
        scoreLabel.setPosition(340, 750);
        uiStage.addActor(scoreLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();

        //setupBg();

        moveBox();

        // render models
        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        // render ui
        uiStage.draw();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        calculateBoxes();

        return false;
    }

    private void calculateBoxes() {
        int size = instances.size;

        // calculate top box stuff
        ModelInstance topBox = instances.peek();
        topBox.transform.getTranslation(boxPosition);
        float topBoxPosition = boxPosition.z;
        BoundingBox topBound = new BoundingBox();
        topBox.calculateBoundingBox(topBound);

        // calculate last box stuff
        ModelInstance lastBox = instances.get(size-2);
        lastBox.transform.getTranslation(boxPosition);
        float lastBoxPosition = boxPosition.z;
        BoundingBox lastBound = new BoundingBox();
        lastBox.calculateBoundingBox(lastBound);

        // we are not interested in "pixel perfect" game
        float distance = abs(topBoxPosition - lastBoxPosition);
        if (distance < 0.3) {
            instances.pop();
            float lastSizeZ = abs(lastBound.min.z) + abs(lastBound.max.z);
            spawnSameBox(5f, 1f, lastSizeZ, lastBoxPosition);
            spawnNewBox(5f, 1f, lastSizeZ);
            incrementScore();
            return;
        }

        // move/spawning logic
        if (topBoxPosition != lastBoxPosition) {
            float lastSizeZ = abs(lastBound.min.z) + abs(lastBound.max.z);
            float resizeBy = abs(topBoxPosition - lastBoxPosition);
            float newSize = lastSizeZ - resizeBy;

            if (newSize < 0) {
                System.out.println("You Lost!");
                game.setScreen(new LostScreen(game, score));
            } else {
                instances.pop();

                if (lastBoxPosition < topBoxPosition) {
                    float newPos = lastBound.max.z - (newSize / 2) + lastBoxPosition;
                    spawnSameBox(5f, 1f, newSize, newPos);
                } else {
                    float newPos = lastBound.min.z + (newSize / 2) + lastBoxPosition;
                    spawnSameBox(5f, 1f, newSize, newPos);
                }

                spawnNewBox(5f, 1f, newSize);

                incrementScore();
            }
        }
    }

    private void incrementScore() {
        score += 1;
        scoreLabel.setText("Score: " + score);
    }

    // moving box around
    private void moveBox() {
        ModelInstance lastBox = instances.peek();

        if (boxMove == '+') {
            lastBox.transform.getTranslation(boxPosition);
            lastBox.transform.trn(0, 0, 0.07f);

            if (boxPosition.z > 7) {
                boxMove = '-';
            }
        } else if (boxMove == '-') {
            lastBox.transform.getTranslation(boxPosition);
            lastBox.transform.trn(0, 0, -0.07f);

            if (boxPosition.z < -7) {
                boxMove = '+';
            }
        }
    }

    private void spawnNewBox(float x, float y, float z) {
        Model box = modelBuilder.createBox(
                x, y, z,
                new Material(ColorAttribute.createDiffuse(boxColor)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal
        );
        modelInstance = new ModelInstance(box, 0, boxLevel++, -7);
        instances.add(modelInstance);
        camera.position.set(5f, cameraLevel++, 5f);
    }

    private void spawnSameBox(float x, float y, float z, float pos) {
        Model box = modelBuilder.createBox(
                x, y, z,
                new Material(ColorAttribute.createDiffuse(boxColor)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal
        );
        modelInstance = new ModelInstance(box, 0, boxLevel-1, pos);
        instances.add(modelInstance);
        camera.position.set(5f, cameraLevel, 5f);
    }

    private void setupBg() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color c1 = new Color(255/255f, 90/255f, 90/255f, 1);
        Color c2 = new Color(255/255f, 242/255f, 153/255f, 1);
        shapeRenderer.rect(0f, 0f, 640f, 680f, c2, c2, c1, c1);
        shapeRenderer.end();
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
    public void dispose() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }
}
