package com.lucassbertol.codebreaker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lucassbertol.codebreaker.MainGame;
import com.lucassbertol.codebreaker.config.Constants;

public class MenuScreen implements Screen {

    private final SpriteBatch batch;
    private final Texture background;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final MainGame game;
    private final Skin skin;
    private final Stage stage;

    public MenuScreen(MainGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        // FitViewport
        viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, camera);
        camera.position.set(Constants.VIEWPORT_WIDTH / 2f, Constants.VIEWPORT_HEIGHT / 2f, 0);

        batch = new SpriteBatch();

        // Carrega a imagem de fundo da pasta assets
        background = new Texture(Constants.BG_MENU);

        // Usa a fonte do skin
        skin = new Skin(Gdx.files.internal(Constants.SKIN_PATH));

        // Cria o Stage
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        // Cria a tabela para organizar os botões
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(100); // Posiciona na parte inferior com padding
        stage.addActor(table);

        // Estilo dos botões
        TextButton.TextButtonStyle buttonStyle = skin.get(TextButton.TextButtonStyle.class);
        BitmapFont buttonFont = skin.getFont("default-font");
        buttonFont.getData().setScale(Constants.BUTTON_FONT_SCALE);

        // Botão JOGAR
        TextButton playButton = new TextButton("JOGAR", buttonStyle);
        playButton.getLabel().setFontScale(Constants.BUTTON_FONT_SCALE);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new UserInputScreen(game));
            }
        });

        // Botão RANK
        TextButton rankButton = new TextButton("RANK", buttonStyle);
        rankButton.getLabel().setFontScale(Constants.BUTTON_FONT_SCALE);
        rankButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ScoreScreenTable(game));
            }
        });

        // Adiciona os botões à tabela com espaçamento
        table.add(playButton).width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).padRight(50);
        table.add(rankButton).width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT);
    }
    @Override
    public void show() {
        // Este método é chamado quando a tela se torna a tela ativa.
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Limpa a tela
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Atualiza a câmera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Inicia o desenho
        batch.begin();

        // Desenha a imagem de fundo para preencher a viewport virtual
        batch.draw(background, 0, 0, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);

        batch.end();

        // Atualiza e desenha o stage com os botões
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza o viewport quando a janela é redimensionada
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        // Chamado quando a tela não é mais a ativa.
    }

    @Override
    public void dispose() {
        // Libera os recursos para evitar vazamento de memória
        background.dispose();
        skin.dispose();
        stage.dispose();
        batch.dispose();
    }
}
