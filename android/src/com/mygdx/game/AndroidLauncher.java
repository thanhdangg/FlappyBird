package com.mygdx.game;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		// Tắt sử dụng đa cảm ứng
		config.useImmersiveMode = true;
		// Tắt sử dụng vẽ màn hình đồng bộ với vSync
//		config.useGLSurfaceView20API18 = true;
		initialize(new FlappyBird(), config);
	}
}
