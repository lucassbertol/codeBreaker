package com.lucassbertol.codebreaker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lucassbertol.codebreaker.MainGame;
import com.lucassbertol.codebreaker.config.Constants;

public class GameOverScreen implements Screen {

    private final SpriteBatch batch;
    private final Texture background;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final MainGame game;
    private final Skin skin;
    private final Stage stage;

    public GameOverScreen(MainGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, camera);
        camera.position.set(Constants.VIEWPORT_WIDTH / 2f, Constants.VIEWPORT_HEIGHT / 2f, 0);

        batch = new SpriteBatch();
        background = new Texture(Constants.BG_OVER);
        skin = new Skin(Gdx.files.internal(Constants.SKIN_PATH));

        // Cria o Stage
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        // table
        Table table = new Table();
        table.setFillParent(true);
        table.bottom();
        stage.addActor(table);

        // Label com a msg
        Label messageLabel = new Label("Clique para voltar ao menu", skin);
        messageLabel.setFontScale(Constants.MESSAGE_FONT_SCALE);
        messageLabel.setColor(Color.GREEN);

        table.add(messageLabel).padBottom(Constants.VIEWPORT_HEIGHT / 4f);

        // listener de clique
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playMainMusic();
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    @Override
    public void show() {
        game.playFailedMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Desenha o background
        batch.begin();
        batch.draw(background, 0, 0, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        batch.end();

        // Desenha o stage com o texto
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        batch.dispose();
        background.dispose();
        skin.dispose();
        stage.dispose();
    }
}
