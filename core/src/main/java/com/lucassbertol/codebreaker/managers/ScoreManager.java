package com.lucassbertol.codebreaker.managers;

import com.lucassbertol.codebreaker.config.Constants;

import static com.lucassbertol.codebreaker.config.Constants.*;

public class ScoreManager {
    private int score;
    private String difficulty;
    private float elapsedTime;
    private boolean stopped;

    public ScoreManager(String difficulty) {
        this.difficulty = difficulty;
        this.score = 0;
        this.elapsedTime = 0;
        this.stopped = false;
    }

    // add pontos por resposta certa
    public void addCorrectAnswer() {
        if (Constants.DIFFICULTY_EASY.equals(difficulty)) {
            score += EASY_CORRECT_POINTS;
        } else if (Constants.DIFFICULTY_HARD.equals(difficulty)) {
            score += HARD_CORRECT_POINTS;
        }
    }

   // remove pontos por resposta errada
    public void addWrongAnswer() {
        score -= WRONG_PENALTY;
        if (score < 0) {
            score = 0; // Evita score negativo
        }
    }

    public void update(float delta) {
        if (!stopped) {
            elapsedTime += delta;

            // A cada segundo completo, aplica a penalidade
            int secondsElapsed = (int) elapsedTime;
            int timePenalty = secondsElapsed * TIME_PENALTY_PER_SECOND;

            // Calcula o score com a penalidade de tempo aplicada
        }
    }

    public void stop() {
        this.stopped = true;
    }

   // get score
    public int getScore() {
        int timePenalty = (int) elapsedTime * TIME_PENALTY_PER_SECOND;
        int finalScore = score - timePenalty;
        return Math.max(0, finalScore); // Nunca retorna score negativo
    }

    // get tempo decorrido
    public float getElapsedTime() {
        return elapsedTime;
    }

    // get penalidade de tempo
    public int getTimePenalty() {
        return (int) elapsedTime * TIME_PENALTY_PER_SECOND;
    }

    // reset score
    public void reset() {
        this.score = 0;
        this.elapsedTime = 0;
    }
}
