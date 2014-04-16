package ru.footmade.dummymagic;

import ru.footmade.dummymagic.entities.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class AudioPlayer implements Disposable {
	private Script script;
	
	public AudioPlayer(Script script) {
		this.script = script;
	}
	
	private int oldScene = -1;
	
	private Music currentMusic, currentSound, currentVoice;
	
	public void update() {
		int currentSceneId = script.currentScene;
		if (currentSceneId != oldScene) {
			Scene currentScene = script.getCurrentScene();
			if (currentScene.music != null) {
				if (currentMusic != null)
					currentMusic.stop();
				if (currentScene.music.name != null) {
					currentMusic = Gdx.audio.newMusic(Gdx.files.internal("snd/" + currentScene.music.name));
					currentMusic.setLooping(currentScene.music.looped);
					currentMusic.play();
				}
			}
			if (currentScene.sound != null) {
				if (currentSound != null)
					currentSound.stop();
				if (currentScene.sound.name != null) {
					currentSound = Gdx.audio.newMusic(Gdx.files.internal("snd/" + currentScene.sound.name));
					currentSound.setLooping(currentScene.sound.looped);
					currentSound.play();
				}
			}
			if (currentScene.voice != null) {
				if (currentVoice != null)
					currentVoice.stop();
				if (currentScene.voice.name != null) {
					currentVoice = Gdx.audio.newMusic(Gdx.files.internal("snd/" + currentScene.voice.name));
					currentVoice.setLooping(currentScene.voice.looped);
					currentVoice.play();
				}
			}
			oldScene = currentSceneId;
		}
	}

	@Override
	public void dispose() {
		if (currentMusic != null)
			currentMusic.dispose();
		if (currentSound != null)
			currentSound.dispose();
		if (currentVoice != null)
			currentVoice.dispose();
	}
}
