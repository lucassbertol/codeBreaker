package com.lucassbertol.codebreaker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lucassbertol.codebreaker.MainGame;
import com.lucassbertol.codebreaker.config.Constants;
import com.lucassbertol.codebreaker.leaderboard.LeaderboardService;

public class UserInputScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final Texture backgroundTexture;
    private final MainGame game;
    private final LeaderboardService leaderboardService;
    private Label messageLabel;
    private TextButton nextButton;
    private boolean isCheckingName = false;

    public UserInputScreen(MainGame game) {
        this.game = game;
        this.leaderboardService = new LeaderboardService();

        stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT));
        skin = new Skin(Gdx.files.internal(Constants.SKIN_PATH));

        backgroundTexture = new Texture(Gdx.files.internal(Constants.BG_INPUT));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.stretch);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Botão voltar no topo esquerdo
        TextButton backButton = new TextButton(Constants.BTN_BACK, skin);
        backButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        backButton.getLabel().setFontScale(Constants.BUTTON_FONT_SCALE);
        backButton.setPosition(Constants.BTN_BACK_PAD, Constants.VIEWPORT_HEIGHT - Constants.BUTTON_HEIGHT - Constants.BTN_BACK_PAD);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        stage.addActor(backButton);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label titleLabel = new Label(Constants.MSG_ENTER_USERNAME, skin);
        titleLabel.setFontScale(Constants.TITLE_FONT_SCALE);
        table.add(titleLabel).colspan(2).padBottom(60f);
        table.row();

        TextField nameInput = new TextField("", skin);
        nameInput.getStyle().font.getData().setScale(Constants.INPUT_FONT_SCALE);
        nameInput.setMessageText("");

        // Label de mensagem de feedback
        messageLabel = new Label("", skin);
        messageLabel.setFontScale(Constants.FEEDBACK_FONT_SCALE);
        messageLabel.setColor(Color.RED);

        nextButton = new TextButton(Constants.BTN_CONTINUE, skin);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isCheckingName) {
                    return; // Evita múltiplos cliques durante verificação
                }

                String playerName = nameInput.getText().trim();
                if (playerName.isEmpty()) {
                    showErrorMessage("Por favor, digite um nome!");
                    return;
                }

                if (playerName.length() < 3) {
                    showErrorMessage("Nome deve ter pelo menos 3 caracteres!");
                    return;
                }

                // Verifica se o nome já existe
                checkAndProceed(playerName);
            }
        });
        nextButton.getLabel().setFontScale(Constants.BUTTON_FONT_SCALE);

        table.row();
        table.add(nameInput).width(Constants.INPUT_WIDTH).height(Constants.INPUT_HEIGHT).colspan(2).padBottom(20f);
        table.row();
        table.add(messageLabel).colspan(2).padBottom(20f);
        table.row();
        table.add(nextButton).width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).colspan(2);

        Gdx.input.setInputProcessor(stage);
    }

    private void checkAndProceed(String playerName) {
        isCheckingName = true;
        showInfoMessage("Verificando nome...");
        nextButton.setDisabled(true);

        leaderboardService.checkNameExists(playerName, new LeaderboardService.CheckNameCallback() {
            @Override
            public void onNameExists(boolean exists) {
                isCheckingName = false;
                nextButton.setDisabled(false);

                if (exists) {
                    showErrorMessage("Nome '" + playerName + "' ja em uso! Escolha outro.");
                } else {
                    // Nome disponível
                    game.setPlayerName(playerName);
                    game.setScreen(new DifficultSelectScreen(game));
                }
            }

            @Override
            public void onError(String message) {
                isCheckingName = false;
                nextButton.setDisabled(false);
                showErrorMessage("Erro ao verificar nome. Tente novamente.");
                Gdx.app.error("UserInputScreen", "Erro na verificação: " + message);
            }
        });
    }

    private void showErrorMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setColor(Color.RED);
    }

    private void showInfoMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setColor(Color.GREEN);
    }

    @Override
    public void show() { }

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
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
    }
}
