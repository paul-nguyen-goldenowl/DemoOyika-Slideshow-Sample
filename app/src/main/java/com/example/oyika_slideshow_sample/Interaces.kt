package com.example.oyika_slideshow_sample

interface SettingListener {
    fun onChange(interval: Long, timout: Long)
}

interface  ClickListener {
    fun onClick()
}