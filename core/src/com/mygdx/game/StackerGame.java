package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import static java.lang.Math.abs;

public class StackerGame extends ApplicationAdapter implements InputProcessor {
    private OrthographicCamera camera;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model box;
    private ModelInstance modelInstance;
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private Environment environment;
    private ShapeRenderer shapeRenderer;

    // colors
    Color boxColor = new Color(157/255f, 227/255f, 255/255f, 1);

    // game logic
    private char boxMove = '+';
    private Vector3 boxPosition;
    private int boxLevel = 0;
    private float cameraLevel = 7f;


    @Override
    public void create() {
        // setup camera
        camera = new OrthographicCamera(640, 480);
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
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();

        setupBg();

        moveBox();

        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        calculateParts();

        return false;
    }

    private void calculateParts() {
        int size = instances.size;
        ModelInstance topBox = instances.peek();
        ModelInstance lastBox = instances.get(size-2);

        // calculate top box stuff
        topBox.transform.getTranslation(boxPosition);
        float topBoxPosition = boxPosition.z;
        BoundingBox topBound = new BoundingBox();
        topBox.calculateBoundingBox(topBound);

        // calculate last box stuff
        lastBox.transform.getTranslation(boxPosition);
        float lastBoxPosition = boxPosition.z;
        BoundingBox lastBound = new BoundingBox();
        lastBox.calculateBoundingBox(lastBound);

        System.out.println("top bound z: " + (abs(topBound.min.z) + abs(topBound.max.z)));
        System.out.println("last bound z: " + (abs(lastBound.min.z) + abs(lastBound.max.z)));
        System.out.println("top position: " + topBoxPosition);
        System.out.println("last position: " + lastBoxPosition);

        // stuff happening here
        if (topBoxPosition != lastBoxPosition) {
            float lastSizeZ = abs(lastBound.min.z) + abs(lastBound.max.z);
            float resizeBy = abs(topBoxPosition - lastBoxPosition);
            float newSize = lastSizeZ - resizeBy;

            System.out.println("Resize by: " + resizeBy);
            System.out.println("New size must be: " + newSize);
            System.out.println("");

            if (newSize < 0) {
                System.out.println("You Lost!");
                System.out.println("");
            } else {
                instances.pop();
                spawnSameBox(5f, 1f, newSize);
                spawnNewBox(5f, 1f, newSize);
            }
        }
    }

    // moving box around
    private void moveBox() {
        ModelInstance lastBox = instances.peek();

        if (boxMove == '+') {
            lastBox.transform.getTranslation(boxPosition);
            lastBox.transform.trn(0, 0, 0.1f);

            if (boxPosition.z > 7) {
                boxMove = '-';
            }
        } else if (boxMove == '-') {
            lastBox.transform.getTranslation(boxPosition);
            lastBox.transform.trn(0, 0, -0.1f);

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

    private void spawnSameBox(float x, float y, float z) {
        Model box = modelBuilder.createBox(
                x, y, z,
                new Material(ColorAttribute.createDiffuse(boxColor)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal
        );
        modelInstance = new ModelInstance(box, 0, boxLevel-1, 0);
        instances.add(modelInstance);
        camera.position.set(5f, cameraLevel, 5f);
    }

    private void setupBg() {
        shapeRenderer.begin(ShapeType.Filled);
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
}
