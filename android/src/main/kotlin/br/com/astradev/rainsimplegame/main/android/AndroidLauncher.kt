package br.com.astradev.rainsimplegame.main.android

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import br.com.astradev.rainsimplegame.main.Main

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {

    // yeah yeah, should be dynamic, but not in this game
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * initialize function recieves an AndroidApplivationConfig, you can
         * instantiate a config var before calling initialize, but libGDX recommends
         * doing this way (as we are using kotlin, not java).
         * https://libgdx.com/wiki/input/configuration-and-querying
         */

        initialize(Main(), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // android buttons does appear or not?
            title = "Rain Simple Game"
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        })
    }
}
