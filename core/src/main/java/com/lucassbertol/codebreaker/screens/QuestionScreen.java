package com.lucassbertol.codebreaker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lucassbertol.codebreaker.MainGame;
import com.lucassbertol.codebreaker.config.Constants;
import com.lucassbertol.codebreaker.data.Question;
import com.lucassbertol.codebreaker.utils.AnswerValidator;
import com.lucassbertol.codebreaker.managers.GameStateManager;
import com.lucassbertol.codebreaker.managers.TimerManager;
import com.lucassbertol.codebreaker.managers.ScoreManager;
import com.lucassbertol.codebreaker.leaderboard.LeaderboardService;
import com.lucassbertol.codebreaker.leaderboard.LeaderboardService.SubmitCallback;

import java.util.ArrayList;
import java.util.List;

public class QuestionScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final Texture backgroundTexture;
    private final MainGame game;
    private final Question currentQuestion;
    private final String difficulty;
    private final List<TextField> inputFields;
    private final Label messageLabel;
    private final Label progressLabel;
    private final GameStateManager gameState;
    private final TimerManager timerManager;
    private final ScoreManager scoreManager;
    private final Label timerLabel;

    public QuestionScreen(MainGame game, Question question, String difficulty, int questionsAnswered, List<Integer> usedQuestionIds, TimerManager timerManager, ScoreManager scoreManager) {
        this.game = game;
        this.currentQuestion = question;
        this.difficulty = difficulty;
        this.inputFields = new ArrayList<>();

        // Inicializa managers
        this.gameState = new GameStateManager(questionsAnswered, usedQuestionIds, Constants.TOTAL_QUESTIONS);
        this.timerManager = (timerManager != null) ? timerManager : new TimerManager(difficulty);
        this.scoreManager = (scoreManager != null) ? scoreManager : new ScoreManager(difficulty);
        gameState.markQuestionAsUsed(currentQuestion.getId());

        // Inicializa stage e skin
        this.stage = new Stage(new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT));
        this.skin = new Skin(Gdx.files.internal(Constants.SKIN_PATH));

        // Setup background
        this.backgroundTexture = new Texture(Gdx.files.internal(Constants.BG_QUESTION));
        setupBackground();

        // Cria labels
        this.timerLabel = createTimerLabel();
        this.progressLabel = createProgressLabel();
        this.messageLabel = createMessageLabel();

        // Setup UI
        setupUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void setupBackground() {
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.stretch);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
    }

    private void setupUI() {
        Table mainTable = createMainTable();
        Table contentTable = createContentTable();
        ScrollPane scrollPane = createScrollPane(contentTable);
        layoutMainTable(mainTable, scrollPane);
        stage.addActor(mainTable);
    }

    private Label createTimerLabel() {
        Label label = new Label("0s", skin);
        label.setFontScale(Constants.TIMER_FONT_SCALE);
        return label;
    }

    private Label createProgressLabel() {
        Label label = new Label(gameState.getProgressText(), skin);
        label.setFontScale(Constants.PROGRESS_FONT_SCALE);
        return label;
    }

    private Label createMessageLabel() {
        Label label = new Label("", skin);
        label.setColor(Color.RED);
        label.setFontScale(Constants.FEEDBACK_FONT_SCALE);
        return label;
    }

    private Table createMainTable() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        return mainTable;
    }

    private Table createContentTable() {
        Table contentTable = new Table();
        contentTable.top();
        contentTable.pad(20);

        Label enunciadoLabel = createEnunciadoLabel();
        Label questionLabel = createQuestionLabel();
        Table inputTable = createInputTable();
        TextButton verifyButton = createVerifyButton();

        contentTable.add(progressLabel).top().padBottom(15);
        contentTable.row();
        contentTable.add(enunciadoLabel).width(1200).top();
        contentTable.row().pad(30);
        contentTable.add(questionLabel).width(1200).top();
        contentTable.row().pad(30);
        contentTable.add(inputTable);
        contentTable.row().pad(20);
        contentTable.add(messageLabel);
        contentTable.row().pad(20);
        contentTable.add(verifyButton).width(300).height(100);
        contentTable.row().padBottom(50);

        return contentTable;
    }

    private Label createEnunciadoLabel() {
        Label enunciadoLabel = new Label(currentQuestion.getEnunciado(), skin);
        enunciadoLabel.setFontScale(Constants.QUESTION_TEXT_SCALE);
        enunciadoLabel.setWrap(true);
        enunciadoLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        enunciadoLabel.setColor(Color.GREEN);
        return enunciadoLabel;
    }

    private Label createQuestionLabel() {
        Label questionLabel = new Label(currentQuestion.getQuestaoTexto(), skin);
        questionLabel.setFontScale(Constants.QUESTION_TEXT_SCALE);
        questionLabel.setWrap(true);
        return questionLabel;
    }

    private Table createInputTable() {
        Table inputTable = new Table();
        createInputFields(inputTable);
        return inputTable;
    }

    private TextButton createVerifyButton() {
        TextButton verifyButton = new TextButton("VERIFICAR", skin);
        verifyButton.getLabel().setFontScale(Constants.BUTTON_FONT_SCALE);
        verifyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                checkAnswer();
            }
        });
        return verifyButton;
    }

    private ScrollPane createScrollPane(Table contentTable) {
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // Remove o fundo do ScrollPane
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(scrollPane.getStyle());
        scrollStyle.background = null;
        scrollPane.setStyle(scrollStyle);

        return scrollPane;
    }

    private void layoutMainTable(Table mainTable, ScrollPane scrollPane) {
        mainTable.add(timerLabel).top().right().padTop(20).padRight(20);
        mainTable.row();
        mainTable.add(scrollPane).width(1650).height(890).pad(10);
    }

    private void createInputFields(Table table) {
        inputFields.clear();
        ArrayList<ArrayList<String>> answers = currentQuestion.getAnswer();

        for (int i = 0; i < answers.size(); i++) {
            TextField input = new TextField("", skin);
            input.getStyle().font.getData().setScale(Constants.INPUT_FONT_SCALE);
            inputFields.add(input);

            Label label = new Label("Resposta " + (i + 1) + ":", skin);
            label.setFontScale(Constants.BUTTON_FONT_SCALE);

            table.row().pad(15);
            table.add(label).padRight(20);
            table.add(input).width(500).height(80);
        }
    }

    private void checkAnswer() {
        boolean allCorrect = AnswerValidator.validateAnswers(inputFields, currentQuestion);

        if (allCorrect) {
            handleCorrectAnswer();
        } else {
            handleWrongAnswer();
        }
    }

    private void handleCorrectAnswer() {
        scoreManager.addCorrectAnswer();
        gameState.incrementAnsweredQuestions();

        if (gameState.isGameCompleted()) {
            finishGame();
        } else {
            loadNextQuestion();
        }
    }

    private void handleWrongAnswer() {
        scoreManager.addWrongAnswer();
        showErrorMessage();
        AnswerValidator.clearInputFields(inputFields);
    }

    private void finishGame() {
        stopGameTimers();
        showCalculatingMessage();
        submitScoreAndNavigate();
    }

    private void stopGameTimers() {
        timerManager.stop();
        scoreManager.stop();
    }

    private void showCalculatingMessage() {
        messageLabel.setText("Calculando score...");
        messageLabel.setColor(Color.GREEN);
    }

    private void showErrorMessage() {
        messageLabel.setText("Tente novamente");
        messageLabel.setColor(Color.RED);
    }

    private void submitScoreAndNavigate() {
        String nomeJogador = game.getPlayerName();
        int pontuacao = scoreManager.getScore();

        LeaderboardService leaderboardService = new LeaderboardService();
        leaderboardService.submitScore(nomeJogador, pontuacao, new SubmitCallback() {
            @Override
            public void onSuccess() {
                navigateToScoreScreen();
            }

            @Override
            public void onError(String message) {
                navigateToScoreScreen();
            }
        });
    }

    private void navigateToScoreScreen() {
        game.setScreen(new ScoreScreen(game, scoreManager));
    }

    private void loadNextQuestion() {
        com.lucassbertol.codebreaker.data.QuestionsParsing parser =
            new com.lucassbertol.codebreaker.data.QuestionsParsing();
        Question nextQuestion = parser.getRandomQuestionExcluding(
            difficulty, gameState.getUsedQuestionIds()
        );

        game.setScreen(new QuestionScreen(
            game,
            nextQuestion,
            difficulty,
            gameState.getQuestionsAnswered(),
            gameState.getUsedQuestionIds(),
            timerManager,
            scoreManager
        ));
    }


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateGameState(delta);

        if (timerManager.isTimeUp()) {
            handleTimeout();
            return;
        }

        stage.act(delta);
        stage.draw();
    }

    private void updateGameState(float delta) {
        timerManager.update(delta);
        scoreManager.update(delta);
        timerLabel.setText(timerManager.getFormattedTime());
    }

    private void handleTimeout() {
        timerManager.stop();
        scoreManager.stop();
        game.setScreen(new GameOverScreen(game));
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
