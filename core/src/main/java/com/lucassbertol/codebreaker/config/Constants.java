package com.lucassbertol.codebreaker.config;


// Classe com todas as constantes do jogo.

public class Constants {

    // classes de viewport do jogo
    public static final int VIEWPORT_WIDTH = 1920;
    public static final int VIEWPORT_HEIGHT = 1080;

    // config de jogo
    public static final int TOTAL_QUESTIONS = 5;
    public static final float COUNTDOWN_TIME = 5f;

    // caminhhos dos assets
    public static final String SKIN_PATH = "skins/uiskin.json";
    public static final String BG_MENU = "images/menu.png";
    public static final String BG_SELECT = "images/selectFase.png";
    public static final String BG_QUESTION = "images/question.png";
    public static final String BG_SCORE = "images/scoreScreen.png";
    public static final String BG_INPUT = "images/userInput.png";
    public static final String BG_OVER = "images/gameOver.png";


    // Questions
    public static final String QUESTIONS_PATH = "questions/";
    public static final String QUESTIONS_EASY = "questions/easy.json";
    public static final String QUESTIONS_HARD = "questions/hard.json";

    // dificuldade
    public static final String DIFFICULTY_EASY = "easy";
    public static final String DIFFICULTY_HARD = "hard";

    // tamanhos de ui
    public static final float TITLE_FONT_SCALE = 3.0f;
    public static final float BUTTON_FONT_SCALE = 1.5f;
    public static final float MESSAGE_FONT_SCALE = 2.5f;
    public static final float INPUT_FONT_SCALE = 1.9f;
    public static final float PROGRESS_FONT_SCALE = 1.5f;
    public static final float TIMER_FONT_SCALE = 1.8f;
    public static final float QUESTION_TEXT_SCALE = 2.0f;
    public static final float FEEDBACK_FONT_SCALE = 1.8f;
    public static final float SCORE_FONT_SCALE = 3.5f;
    public static final float BUTTON_WIDTH = 300f;
    public static final float BUTTON_HEIGHT = 100f;
    public static final float INPUT_WIDTH = 500f;
    public static final float INPUT_HEIGHT = 100f;

    // mensagens
    public static final String MSG_PRESS_START = "CLICK TO START";
    public static final String MSG_ENTER_USERNAME = "DIGITE SEU USERNAME:";
    public static final String MSG_SELECT_DIFFICULTY = "ESCOLHA A DIFICULDADE:";
    public static final String MSG_COUNTDOWN = "Carregando questoes...";
    public static final String MSG_ERROR_RANKING = "ERRO AO CARREGAR RANKING";

    // ranking
    public static final String RANKING_HEADER_POS = "Posicao";
    public static final String RANKING_HEADER_NAME = "Nome";
    public static final String RANKING_HEADER_POINTS = "Pontos";
    public static final float RANKING_HEADER_PAD = 30f;
    public static final float RANKING_ROW_PAD = 8f;
    public static final float RANKING_TABLE_WIDTH = 1280f;
    public static final float RANKING_TABLE_HEIGHT = 600f;

    // botoes
    public static final String BTN_EASY = "FACIL";
    public static final String BTN_HARD = "DIFICIL";
    public static final String BTN_CONTINUE = "CONTINUAR";
    public static final String BTN_BACK = "< VOLTAR";
    public static final float BTN_BACK_PAD = 20f;

    //PONTUAÇÃO
    // pontos por acerto
    public static final int EASY_CORRECT_POINTS = 100;
    public static final int HARD_CORRECT_POINTS = 250;

    // pontos perdidos por erro
    public static final int WRONG_PENALTY = 20;

    // pontos perdidos por tempo (2 pontos por segundo)
    public static final int TIME_PENALTY_PER_SECOND = 2;

    // Músicas
    public static final String MUSIC_MAIN = "musics/main.mp3";
    public static final String MUSIC_FAILED = "musics/failed.mp3";
    public static final String MUSIC_PASSED = "musics/passed.mp3";

    // api
    public static final String SHEETS_API_URL = "https://sheets.googleapis.com/v4/spreadsheets/";
    public static final String SPREADSHEET_ID = "1QOQF2nFjH06r9f4uhQJFXfnFREZ_cFlwoWj863E_pUw";
    public static final String RANGE = "A2:D";
    public static final String API_KEY = "COLOCAR-API-KEY-AQUI";

}


