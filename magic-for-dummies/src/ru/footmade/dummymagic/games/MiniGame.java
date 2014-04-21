package ru.footmade.dummymagic.games;

import com.badlogic.gdx.utils.Disposable;

public abstract class MiniGame implements Disposable {
	private MiniGameCallback callback;
	
	public final void setGameCallback(MiniGameCallback callback) {
		this.callback = callback;
	}
	
	public final void exit(int result) {
		callback.onExit(result);
	}
	
	public abstract void start(String argument);
	
	public abstract void render();
	
	public interface MiniGameCallback {
		public void onExit(int result);
	}
}
