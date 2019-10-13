package com.example.kosmozahvatchik

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_fullscreen.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */


class StartActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer


    private lateinit var mcontentView: View
    private lateinit var mloadingView: View
    private var shortAnimationDuration: Int = 0


    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }
    private lateinit var mAdView: AdView
    private lateinit var mInterstitialAd: InterstitialAd
//    private val context:Context = this.applicationContext
//    private val soundPlayer = SoundPlayer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        setContentView(R.layout.activity_fullscreen)




        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        MobileAds.initialize(
            this
        ) { }

        mAdView = findViewById(R.id.adView)
//////        mAdView.adSize = AdSize.SMART_BANNER
//////        mAdView.adUnitId = (R.string.admob_app_id).toString()
        val adRequest = AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("BA9723C4E9664D3AD0E7D0E39D3A4274")
            .build()
        mAdView.loadAd(adRequest)
        mAdView.adListener

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-9051542338788579~3256817607"
//        Toast.makeText(this, "mInterstitialAd.adUnitId", Toast.LENGTH_LONG).show()
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        mInterstitialAd.loadAd(
            AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("BA9723C4E9664D3AD0E7D0E39D3A4274")
                .build()
        )

        // Play_MUSIC_on_start_screen
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.start)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(22f, 22f)
//        mediaPlayer.setLooping(true)

        mediaPlayer.start()
        Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()

//        var player = MediaPlayer()
//        try {
//
//            player!!.setDataSource(assets.damageshelter.ogg)
//            player!!.prepare()
//            player!!.start()
//        }catch (ex:Exception){
//        }

//        val soundPlayer = SoundPlayer(this)
//
//        soundPlayer.playSound(SoundPlayer.startMusicID)

        // Set up the user interaction to manually show or hide the system UI.
        fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        start_button.setOnClickListener {
            Log.d(this.toString(), "Try to catch start GAME")

            // launch the StartGAME activity somehow
            val intent = Intent(this, KotlinInvadersActivity::class.java)


            mediaPlayer.stop()

            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
//            Toast.makeText(this, "The interstitial wasn't loaded yet.", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
//            transFlow()
            startActivity(intent)
        }
        mcontentView = findViewById(R.id.fullscreen_content_controls)
        mloadingView = findViewById(R.id.fullscreen_content)

        // Initially hide the content view.
        mcontentView.visibility = View.GONE

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)


    }

    override fun onResume() {
        super.onResume()

        mediaPlayer.start()

        // Tell the gameView resume method to execute
//        kotlinInvadersView?.resume()
    }

    // This method executes when the player quits the game
    override fun onPause() {
        super.onPause()

        mediaPlayer.stop()

        // Tell the gameView pause method to execute
//        kotlinInvadersView?.pause()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
//            crossfade()

            show()
        }
    }

    private fun transFlow() {
        val animator = ValueAnimator.ofFloat(0f, 1700f)
        animator.duration = 1000
        animator.start()

        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val animatedValue = animation.animatedValue as Float
                mloadingView.translationX = animatedValue
                mcontentView.translationX = animatedValue
            }
        })
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
//        fullscreen_content.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    private fun crossfade() {
        mcontentView.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mloadingView.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mloadingView.visibility = View.GONE
                }
            })
    }


    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
