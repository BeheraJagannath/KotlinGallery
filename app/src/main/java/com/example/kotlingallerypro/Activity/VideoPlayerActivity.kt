package com.example.kotlingallerypro.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.kotlingallerypro.Activity.VideoSliderActivity.Companion.position
import com.example.kotlingallerypro.Database.DbHelper
import com.example.kotlingallerypro.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_imageshow.view.*
import kotlinx.android.synthetic.main.exoplayer_controls.*
import java.lang.String
import java.util.*

class VideoPlayerActivity : AppCompatActivity() , View.OnClickListener , GestureDetector.OnGestureListener,
    AudioManager.OnAudioFocusChangeListener  {
    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var playerview: PlayerView
    var isCrossChecked = false
    private var isLocked: Boolean = false
    private var brightness : Int = 0
    private var volume : Int = 0
    private var audioManager: AudioManager? = null
    private lateinit var gestureDetectorCompact : GestureDetectorCompat
    private  lateinit var dbHelper: DbHelper

    lateinit var play_button: ImageView
    lateinit var back :ImageView
    lateinit var forward :ImageView
    lateinit var backward :ImageView
    lateinit var rotate :ImageView
    lateinit var lock :LinearLayout
    lateinit var unlock :LinearLayout
    lateinit var bottom :LinearLayout
    lateinit var bottom_opt :LinearLayout
    lateinit var stretch :ImageView
    lateinit var fullScreen :ImageView
    lateinit var full :ImageView
    lateinit var video_title: TextView
    lateinit var volume_seek: SeekBar
    lateinit var bright_seek: SeekBar
    lateinit var brightnessIcon: RelativeLayout
    lateinit var volumeIcon: RelativeLayout
    lateinit var volumedialogIcon: ImageView
    lateinit var brightdealogicon : ImageView




    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        playerview = findViewById(R.id.exo_player_view)
        play_button = findViewById(R.id.play_button)
        back =findViewById(R.id.ic_back)
        forward =findViewById(R.id.for_word)
        backward =findViewById(R.id.back_word)
        rotate =findViewById(R.id.rotate)
        lock =findViewById(R.id.lock)
        unlock =findViewById(R.id.unlock)
        bottom_opt =findViewById(R.id.bottom_opt)
        bottom =findViewById(R.id.bottom)
        stretch = findViewById(R.id.strech)
        full = findViewById(R.id.full)
        fullScreen = findViewById(R.id.fullScreen)
        video_title = findViewById(R.id.videoView_title)
        volume_seek = findViewById(R.id.seekbar_vol)
        bright_seek = findViewById(R.id.seekbar_bright)
        brightnessIcon = findViewById(R.id.brightnessIcon)
        volumeIcon = findViewById(R.id.volumeIcon)
        volumedialogIcon = findViewById(R.id.vol_dialog_icon)
        brightdealogicon = findViewById(R.id.brt_dialog_icon)


        val vdoPath = intent.getStringExtra("path")
        dbHelper = DbHelper( this,null)


        video_title.text = VideoSliderActivity .allVideoList[position].title


        play_button.setOnClickListener(this)
        back.setOnClickListener(this)
        forward.setOnClickListener(this)
        backward.setOnClickListener(this)
        rotate.setOnClickListener(this)
        lock.setOnClickListener(this)
        unlock.setOnClickListener(this)
        stretch.setOnClickListener(this)
        full.setOnClickListener(this)
        fullScreen.setOnClickListener(this)

        playVideo()
        seekBarSwipe()
        gestureDetectorCompact = GestureDetectorCompat(this, this)

    }

    private fun playVideo() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        playerview.player = simpleExoPlayer
        val mediaItem: MediaItem = MediaItem.fromUri(VideoSliderActivity.allVideoList[position].path)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
                    playerview.keepScreenOn = false
                } else {
                    playerview.keepScreenOn = true
                }
            }
        })
        playError()

    }

    private fun seekBarSwipe() {
        playerview.setOnTouchListener { _, event ->
            if (!isLocked){

                gestureDetectorCompact.onTouchEvent(event)

                if (event.action == MotionEvent.ACTION_UP){
                    brightnessIcon.visibility = View.GONE
                    volumeIcon.visibility = View.GONE

                }

            }
            return@setOnTouchListener false
        }
    }

//    private fun playVideo() {
//        simpleExoPlayer = SimpleExoPlayer.Builder(this)
//            .setSeekForwardIncrementMs(1000)
//            .setSeekBackIncrementMs(1000)
//            .build()
//        playerview.player = simpleExoPlayer
//        playerview.keepScreenOn = true
//        simpleExoPlayer.addListener(object : Player.Listener {
//            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
//                    play_button.setImageDrawable(resources.getDrawable(R.drawable.pv))
//                    playerview.keepScreenOn = false
//                    simpleExoPlayer.playWhenReady = false
//
//
//                }
//
//            }
//
//        })
//        val videoSource = Uri.parse("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4")
//        val mediaItem = MediaItem.fromUri(VideoSliderActivity.allVideoList[position].path)
////        val mediaItem = MediaItem.fromUri(videoSource)
//        simpleExoPlayer.setMediaItem(mediaItem)
//        simpleExoPlayer.prepare()
//        simpleExoPlayer.play()
//
//
//        playError()
//    }
//
    private fun playError() {
        simpleExoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(this@VideoPlayerActivity, "Video Playing Error", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        simpleExoPlayer.playWhenReady = true
        play_button.setImageDrawable(resources.getDrawable(R.drawable.pause))
    }
    private fun pauseVideo(){
        simpleExoPlayer.pause()
        play_button.setImageDrawable(resources.getDrawable(R.drawable.pv))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (simpleExoPlayer.isPlaying) {
            simpleExoPlayer.stop()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onPause() {
        super.onPause()
        simpleExoPlayer.playWhenReady = false
        simpleExoPlayer.playbackState
        if (isInPictureInPictureMode) {
            simpleExoPlayer.playWhenReady = true
            play_button.setImageDrawable(resources.getDrawable(R.drawable.pause))
        } else {
            simpleExoPlayer.playWhenReady = false
            play_button.setImageDrawable(resources.getDrawable(R.drawable.play))
            simpleExoPlayer.playbackState
        }
    }

    override fun onResume() {
        super.onResume()
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.playbackState

        if (audioManager == null) audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        if (brightness != 0) setScreenBrightness(brightness)
    }

    override fun onRestart() {
        super.onRestart()
        simpleExoPlayer.playWhenReady = true
        play_button.setImageDrawable(resources.getDrawable(R.drawable.pause))
        simpleExoPlayer.playbackState
    }

    override fun onStop() {
        super.onStop()
        if (isCrossChecked) {
            simpleExoPlayer.release()
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ic_back -> {
                finish()
            }
            R.id.back_word -> {
                simpleExoPlayer.seekTo(simpleExoPlayer.currentPosition -10000)
            }
            R.id.for_word -> {
                simpleExoPlayer.seekTo(simpleExoPlayer.currentPosition +10000)
            }



            R.id.play_button -> {
                if (simpleExoPlayer.isPlaying) {
                    simpleExoPlayer.pause()
                    play_button.setImageDrawable(resources.getDrawable(R.drawable.pv))
                    simpleExoPlayer.playWhenReady = false
                } else {
                    simpleExoPlayer.play()
                    simpleExoPlayer.playWhenReady = true
                    play_button.setImageDrawable(resources.getDrawable(R.drawable.pause))
                }
            }
            R.id.rotate -> {

                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //set in landscape
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                } else if (orientation == Configuration.ORIENTATION_LANDSCAPE ) {
                    //set in portrait
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
            R.id.lock -> {
                isLocked = true
                unlock.visibility = View.VISIBLE
                lock.visibility = View.GONE
                bottom.visibility = View.GONE
                bottom_opt.visibility = View.GONE
//                brightnessIcon.visibility = View.GONE
//                volumeIcon.visibility = View.GONE

            }
            R.id.unlock -> {
                isLocked = false
                unlock.visibility = View.GONE
                lock.visibility = View.VISIBLE
                bottom.visibility = View.VISIBLE
                bottom_opt.visibility = View.VISIBLE
//                brightnessIcon.visibility = View.VISIBLE
//                volumeIcon.visibility = View.VISIBLE
            }
            R.id.fullScreen -> {
                // full screen vdo
                playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                stretch.visibility = View.VISIBLE
                full.visibility = View.GONE
                fullScreen.visibility = View.GONE
            }
            R.id.strech -> {
                // stretch vdo
                playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                stretch.visibility = View.GONE
                full.visibility = View.VISIBLE
                fullScreen.visibility = View.GONE
            }
            R.id.full -> {

                playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

                stretch.visibility = View.GONE
                full.visibility = View.GONE
                fullScreen.visibility = View.VISIBLE
            }

            else -> {
            }


        }

    }

    private fun setScreenBrightness(value: Int){
        val d = 1.0f/100
        val lp = this.window.attributes
        lp.screenBrightness = d * value
        this.window.attributes = lp
    }

    override fun onDown(e: MotionEvent?): Boolean = false
    override fun onShowPress(e: MotionEvent?) = Unit
    override fun onSingleTapUp(e: MotionEvent?): Boolean = false
    override fun onLongPress(e: MotionEvent?) = Unit
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) pauseVideo()
    }

    override fun onScroll(event: MotionEvent?, event1: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {

        val sWidth = Resources.getSystem().displayMetrics.widthPixels
        if (Math.abs(distanceX) < Math.abs(distanceY)){
            if (event!!.x < sWidth/2){
                //brightness
                brightnessIcon.visibility = View.VISIBLE
                volumeIcon.visibility = View.GONE

                val increase = distanceY > 0
                val newValue = if (increase) brightness+1 else brightness-1
                if (newValue in 0..100) brightness = newValue

                bright_seek . progress = newValue
                bright_seek . setOnSeekBarChangeListener ( object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {

                        seek.progress = progress
                        if (progress > 80) {
                            brightdealogicon.setImageResource(R.drawable.brt_high)
                        } else if (progress > 55) {
                            brightdealogicon.setImageResource(R.drawable.brt_ls_high)
                        } else if (progress > 20) {
                            brightdealogicon.setImageResource(R.drawable.brt_mid)
                        } else {
                            brightdealogicon.setImageResource(R.drawable.brt_low)
                        }
                    }

                    override fun onStartTrackingTouch(seek: SeekBar) {}

                    override fun onStopTrackingTouch(seek: SeekBar) {}
                })

                setScreenBrightness(brightness)


            }else{
                //volume
                brightnessIcon.visibility = View.GONE
                volumeIcon.visibility = View.VISIBLE

                val maxVol = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val increase = distanceY > 0
                val newValue = if (increase) volume+1 else volume-1
                if (newValue in 0..maxVol) volume = newValue
                audioManager!!. setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)

                volume_seek.setMax(audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

                volume_seek.progress = newValue

                volume_seek . setOnSeekBarChangeListener ( object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(Volseek: SeekBar, Volprogress: Int, fromUser: Boolean) {

                        Volseek.progress = Volprogress
                        if (Volprogress > 10) {
                            volumedialogIcon.setImageResource(R.drawable.vol_high)
                        } else if (newValue > 6) {
                            volumedialogIcon.setImageResource(R.drawable.vol_ls_high)
                        } else if (newValue > 3) {
                            volumedialogIcon.setImageResource(R.drawable.vol_mid)
                        } else {
                            volumedialogIcon.setImageResource(R.drawable.vol_low)
                        }
                    }

                    override fun onStartTrackingTouch(seek: SeekBar) {}

                    override fun onStopTrackingTouch(seek: SeekBar) {}
                })


            }
        }

        return true
    }
}