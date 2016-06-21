package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
    private OrthographicCamera camera;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model box;
    private ModelInstance modelInstance;
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private Environment environment;
    private ShapeRenderer shapeRenderer;

    private char boxMove = '+';
    private Vector3 boxPosition;

    @Override
    public void create() {
        // setup camera
        camera = new OrthographicCamera(640, 480);
        camera.position.set(5f, 7f, 5f);
        camera.lookAt(0f, 0f, 0f);
        camera.zoom = 0.03f;

        // setup model
        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        Color boxColor = new Color(157/255f, 227/255f, 255/255f, 1);
        Model box = modelBuilder.createBox(
                5f, 1f, 5f,
                new Material(ColorAttribute.createDiffuse(boxColor)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal
        );
        modelInstance = new ModelInstance(box, 0, 0, 0);
        instances.add(modelInstance);
        modelInstance = new ModelInstance(box, 0, 1, -7);
        instances.add(modelInstance);

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

        // render bg
        shapeRenderer.begin(ShapeType.Filled);
        Color c1 = new Color(255/255f, 90/255f, 90/255f, 1);
        Color c2 = new Color(255/255f, 242/255f, 153/255f, 1);
        shapeRenderer.rect(0f, 0f, 640f, 680f, c2, c2, c1, c1);
        shapeRenderer.end();

        // moving box around
        if (boxMove == '+') {
            instances.peek().transform.getTranslation(boxPosition);
            instances.peek().transform.trn(0, 0, 0.1f);

            if (boxPosition.z > 7) {
                boxMove = '-';
            }
        } else {
            instances.peek().transform.getTranslation(boxPosition);
            instances.peek().transform.trn(0, 0, -0.1f);

            if (boxPosition.z < -7) {
                boxMove = '+';
            }
        }


        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f), 1f);
        }
        if (keycode == Input.Keys.RIGHT) {
            camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f), -1f);
        }

        return true;
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
