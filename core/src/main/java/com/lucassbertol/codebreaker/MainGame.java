package com.lucassbertol.codebreaker;

import com.badlogic.gdx.Game;
import com.lucassbertol.codebreaker.screens.MenuScreen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;
import com.lucassbertol.codebreaker.config.Constants;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends Game {
    public String playerName;
    public Music mainMusic;      // Música de fundo geral
    public Music failedMusic;    // Música de game over
    public Music passedMusic;    // Música de vitória

    public void stopAllMusic() {
        if (mainMusic != null && mainMusic.isPlaying()) mainMusic.stop();
        if (failedMusic != null && failedMusic.isPlaying()) failedMusic.stop();
        if (passedMusic != null && passedMusic.isPlaying()) passedMusic.stop();
    }

    // Toca música de game over
    public void playFailedMusic() {
        stopAllMusic();
        failedMusic.setLooping(false);
        failedMusic.setVolume(0.2f);
        failedMusic.play();
    }

    // Toca música de vitória
    public void playPassedMusic() {
        stopAllMusic();
        passedMusic.setLooping(false);
        passedMusic.setVolume(0.2f);
        passedMusic.play();
    }

    // Volta para música principal
    public void playMainMusic() {
        stopAllMusic();
        mainMusic.setLooping(true);
        mainMusic.setVolume(0.2f);
        mainMusic.play();
    }

	@Override
	public void create() {
		// Define a tela inicial do jogo como a nossa tela de menu e starta as musicas.
        mainMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.MUSIC_MAIN));
        failedMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.MUSIC_FAILED));
        passedMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.MUSIC_PASSED));

        // Configura a música principal para loop e inicia
        mainMusic.setLooping(true);
        mainMusic.setVolume(0.2f);  // Volume a 50%
        mainMusic.play();
		setScreen(new MenuScreen(this));
	}
        // setter chamado em UserInputScreen
        public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

        // getter usado depois (ex.: QuestionScreen)
        public String getPlayerName() {
        return playerName;
    }
    @Override
    public void dispose() {
        super.dispose();
        if (mainMusic != null) mainMusic.dispose();
        if (failedMusic != null) failedMusic.dispose();
        if (passedMusic != null) passedMusic.dispose();
    }
}
