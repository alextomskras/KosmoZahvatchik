package com.dreamer.kosmozahvatchik


import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_game_over.*


/**
 * An dreamer full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GameOver : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var mbuttonView: View
    private lateinit var mgameoverView: View
    private lateinit var mHiscoreView: View
    private lateinit var mYourscoreView: View


    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        game_over_content.systemUiVisibility =
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
    //    private val TAG = "GameOverActivity"
//
    private lateinit var mAdView: AdView
    private lateinit var mInterstitialAd: InterstitialAd


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_over)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true
        MobileAds.initialize(this, getString(R.string.admob_app_id))


        // AdMob BANNER on GameOver screen
        mAdView = findViewById(R.id.adView)
//        mAdView.adSize = AdSize.SMART_BANNER
//        mAdView.adUnitId = (R.string.admob_app_id).toString()
        val adRequest = AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("BA9723C4E9664D3AD0E7D0E39D3A4274")
            .build()
        mAdView.loadAd(adRequest)

        // add interstishial admob
        mInterstitialAd = InterstitialAd(this).apply {
            //            adUnitId = "ca-app-pub-9051542338788579/2908902045"
            adUnitId = getString(R.string.admob_interstishial_banner_ad)

//        Toast.makeText(this, "mInterstitialAd.adUnitId", Toast.LENGTH_LONG).show()
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Toast.makeText(this@GameOver, "onAdLoaded()", Toast.LENGTH_SHORT).show()
//                    mInterstitialAd.show()
                }

//                override fun onAdClosed() {
//                    mInterstitialAd.loadAd(AdRequest.Builder().build())
//                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    Toast.makeText(
                        this@GameOver,
                        "onAdFailedToLoad() with error code: $errorCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        mInterstitialAd.loadAd(
            AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("BA9723C4E9664D3AD0E7D0E39D3A4274")
                .build()
        )


        // Play_MUSIC_on_gameover_screen
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.end)
        mediaPlayer.isLooping = false
        mediaPlayer.start()
        Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()

        val hiScore: String = intent.getStringExtra("hi-score")
        Log.d(this.toString(), "!!22+$hiScore")

        val youScore: String = intent.getStringExtra("you-score")
        Log.d(this.toString(), "!!221+$youScore")

        var txtyouscore = "${txt_gameover_you_score.text} $youScore".toString()
        Log.d(this.toString(), "!!!!+$txtyouscore")

        txt_gameover_you_score.text = txtyouscore.toString()
        txt_HI_you_score.text = "${txt_HI_you_score.text} $hiScore".toString()
//        txt_gameover_you_score.text = "555"
//        txt_HI_you_score.text = "999"
//        val youscore = txt_gameover_you_score


        // Set up the user interaction to manually show or hide the system UI.
        game_over_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        exit_button.setOnClickListener {
            Log.d(this.toString(), "Try to catch EXIT_GAME")
            bigBanner()
            // launch the StartGAME activity somehow
            val intent = Intent(this, KotlinInvadersActivity::class.java)
            transFlow()

            mediaPlayer.stop()
            startActivity(intent)
            finish()
        }

        mbuttonView = findViewById(R.id.fullscreen_content_controls)
        mgameoverView = findViewById(R.id.game_over_content)
        mHiscoreView = findViewById(R.id.txt_HI_you_score)
        mYourscoreView = findViewById(R.id.txt_gameover_you_score)

    }



    private fun bigBanner() {
        mInterstitialAd.show()

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        getHIscores()


        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun transFlow() {
        val animator = ValueAnimator.ofFloat(0f, -1700f)
        animator.duration = 1000
        animator.start()

        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val animatedValue = animation.animatedValue as Float
                mHiscoreView.translationX = animatedValue
                mYourscoreView.translationX = animatedValue
                mgameoverView.translationX = animatedValue
                mbuttonView.translationX = animatedValue
            }
        })
    }

    private fun getHIscores(): String {
        val contt = this.applicationContext
        val prefs = contt.getSharedPreferences(
            "Kotlin Invaders",
            Context.MODE_PRIVATE
        )

        var chkHighScore = prefs.getInt("highScore", 0)
        return chkHighScore.toString()


    }

    override fun onBackPressed() {
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        bigBanner()
        super.onBackPressed()

    }

    override fun finish() {
        bigBanner()
        super.finish()
    }

    override fun onDestroy() {
        bigBanner()
        super.onDestroy()
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
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
//        game_over_content.systemUiVisibility =
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
