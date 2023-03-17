package com.example.oyika_slideshow_sample

import ZoomOutPageTransformer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.oyika_slideshow_sample.databinding.ActivityMainBinding
import com.example.oyika_slideshow_sample.databinding.BottomSheetSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class MainActivity : AppCompatActivity(), SettingListener, ClickListener {
    private var IMAGE_INTERVAL = 5000L
    private var SLIDESHOW_TIMEOUT = 12000L

    private var handler = Handler(Looper.getMainLooper())
    private var timer: Timer? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var imageView: ImageView
    private lateinit var settingButton: ImageView
    private lateinit var startButton: Button

    private val imageUrls = listOf(
        "https://leo-oyika-kotlin.s3.ap-southeast-1.amazonaws.com/img1.jpeg",
        "https://leo-oyika-kotlin.s3.ap-southeast-1.amazonaws.com/img2.jpeg",
        "https://leo-oyika-kotlin.s3.ap-southeast-1.amazonaws.com/img3.jpeg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        imageView = binding.imageView
        viewPager = binding.viewPager
        settingButton = binding.settingButton
        startButton = binding.buttonStart

        setContentView(binding.root)

        settingButton.setOnClickListener {
            showSettingBottomSheet(this)
        }

        startButton.setOnClickListener {
            cleanResource()
            setupViewPager()
        }

        hideSystemUI(binding.root)

        // request write permission for download.
        writePermission.runWithPermission {
        }

        handler.postDelayed({
            setupViewPager()
        }, SLIDESHOW_TIMEOUT)
    }

    private fun showSettingBottomSheet(listener: SettingListener) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val binding = BottomSheetSettingsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        val seekBar = binding.intervalSeekBar
        val textView = binding.intervalTextView
        textView.text = "Interval: ${seekBar.progress}"
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekBar.progress = progress
                    textView.text = "Interval: $progress"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        val applyButton = binding.buttonApply
        applyButton.setOnClickListener {
            listener.onChange(seekBar.progress.toLong())
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun setupViewPager() {
        val adapter = SlideShowAdapter(imageUrls, this)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        setupAutoScroll()
        toggleButtons()
    }

    private fun toggleButtons() {
        settingButton.isVisible = !settingButton.isVisible
        startButton.isVisible = !startButton.isVisible
        viewPager.isVisible = !viewPager.isVisible
    }

    private fun setupAutoScroll() {
        cleanResource()
        timer = Timer()
        val update = Runnable {
            val currentItem = viewPager.currentItem
            val nexItem = if (currentItem >= imageUrls.lastIndex) 0 else currentItem + 1
            viewPager.currentItem = nexItem
        }

        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, IMAGE_INTERVAL, IMAGE_INTERVAL)
    }

    override fun onChange(interval: Long) {
        IMAGE_INTERVAL = interval
    }

    override fun onClick() {
        toggleButtons()
        handler.removeCallbacksAndMessages(null)
        resetHandler()
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanResource()
    }

    private fun cleanResource() {
        handler.removeCallbacksAndMessages(null)
        timer?.cancel()
    }

    private fun resetHandler() {
        toast("Restart timer")
        cleanResource()
        startHandler()
    }

    private fun startHandler() {
        handler.postDelayed({
            setupViewPager()
        }, SLIDESHOW_TIMEOUT)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            Log.i("[paul]", "onTouchEvent: ${event?.x} ${event?.y}")
            resetHandler()
        }
        return super.onTouchEvent(event)
    }
}