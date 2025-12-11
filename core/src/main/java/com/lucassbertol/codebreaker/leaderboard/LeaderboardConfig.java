package com.lucassbertol.codebreaker.leaderboard;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public final class LeaderboardConfig {

    // URL do Web App do Apps Script (cole exatamente a URL gerada na implantação)
    private static final String APPS_SCRIPT_URL =
        "https://script.google.com/macros/s/AKfycbzTbK2onmRt8krhZ1tDLBeoAsMr_RM9Q8WK33A92DQvzr0n-nMVc4zBkGaOTa5rEjpfWw/exec";

    private LeaderboardConfig() {
    }

    public static String getBaseUrl() {
        return APPS_SCRIPT_URL;
    }

    public static String getFetchUrl() {
        if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
            return "https://corsproxy.io/?" + APPS_SCRIPT_URL;
        }
        return APPS_SCRIPT_URL;
    }

    public static String getAppendUrl() {
        // Para GWT, usa proxy
        if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
            return "https://corsproxy.io/?" + APPS_SCRIPT_URL;
        }
        return APPS_SCRIPT_URL;
    }
}
