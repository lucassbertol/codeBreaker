package com.lucassbertol.codebreaker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lucassbertol.codebreaker.MainGame;
import com.lucassbertol.codebreaker.config.Constants;
import com.lucassbertol.codebreaker.managers.ScoreManager;

public class ScoreScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final Texture backgroundTexture;
    private final MainGame game;
    private final ScoreManager scoreManager;

    public ScoreScreen(MainGame game, ScoreManager scoreManager) {
        this.game = game;
        this.scoreManager = scoreManager;

        // Stage com FitViewport
        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT));
        skin = new Skin(Gdx.files.internal(Constants.SKIN_PATH));

        // Background
        backgroundTexture = new Texture(Gdx.files.internal(Constants.BG_SCORE));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.stretch);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Tabela principal
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        // Título
        Label titleLabel = new Label("SCORE FINAL", skin);
        titleLabel.setFontScale(Constants.TITLE_FONT_SCALE);
        titleLabel.setColor(Color.WHITE);

        // Score final
        Label scoreLabel = new Label(scoreManager.getScore() + " pontos", skin);
        scoreLabel.setFontScale(Constants.SCORE_FONT_SCALE);
        scoreLabel.setColor(Color.GREEN);

        // Tempo decorrido
        int minutes = (int) scoreManager.getElapsedTime() / 60;
        int seconds = (int) scoreManager.getElapsedTime() % 60;
        String minStr = (minutes < 10 ? "0" : "") + minutes;
        String secStr = (seconds < 10 ? "0" : "") + seconds;
        String timeText = "Tempo: " + minStr + ":" + secStr;
        Label timeLabel = new Label(timeText, skin);
        timeLabel.setFontScale(Constants.FEEDBACK_FONT_SCALE);
        timeLabel.setColor(Color.WHITE);

        // Penalidade de tempo
        Label penaltyLabel = new Label("Penalidade de Tempo: -" + scoreManager.getTimePenalty(), skin);
        penaltyLabel.setFontScale(Constants.FEEDBACK_FONT_SCALE);
        penaltyLabel.setColor(Color.RED);

        // Botão para voltar ao menu
        TextButton menuButton = new TextButton("VOLTAR AO MENU", skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playMainMusic();
                game.setScreen(new MenuScreen(game));
            }
        });
        menuButton.getLabel().setFontScale(Constants.BUTTON_FONT_SCALE);

        // Monta o layout
        table.add(titleLabel).colspan(2).padBottom(60f);
        table.row();
        table.add(scoreLabel).colspan(2).padBottom(30f);
        table.row();
        table.add(timeLabel).colspan(2).padBottom(20f);
        table.row();
        table.add(penaltyLabel).colspan(2).padBottom(60f);
        table.row();
        table.add(menuButton).width(Constants.BUTTON_WIDTH + 100).height(Constants.BUTTON_HEIGHT).padLeft(20f);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        game.playPassedMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
    }
}
