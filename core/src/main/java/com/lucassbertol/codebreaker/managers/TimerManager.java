package com.lucassbertol.codebreaker.managers;

import com.lucassbertol.codebreaker.config.Constants;

public class TimerManager {
    private float timeRemaining;
    private boolean timeUp;
    private boolean stopped;

    public TimerManager(String difficulty) {
        if (Constants.DIFFICULTY_EASY.equals(difficulty)) {
            this.timeRemaining = 80; // 1 minuto e 15 segundos
        } else {
            this.timeRemaining = 155; // 2 minutos e 30 segundos
        }
        this.timeUp = false;
        this.stopped = false;
    }

    public void update(float delta) {
        if (!stopped && timeRemaining > 0) {
            timeRemaining -= delta;
            if (timeRemaining <= 0) {
                timeRemaining = 0;
                timeUp = true;
            }
        }
    }

    public void stop() {
        this.stopped = true;
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public String getFormattedTime() {
        int minutes = (int) timeRemaining / 60;
        int seconds = (int) timeRemaining % 60;

        String minStr = (minutes < 10 ? "0" : "") + minutes;
        String secStr = (seconds < 10 ? "0" : "") + seconds;
        return minStr + ":" + secStr;
    }
}
