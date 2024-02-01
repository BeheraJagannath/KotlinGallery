package com.example.kotlingallerypro.Preference

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager (context: Context){

    lateinit var editor: SharedPreferences.Editor
    val sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE)

    fun saveGalleryView(isAlbumView: Boolean) {
        editor = sharedPreferences!!.edit()
        editor.putBoolean("galleryView", isAlbumView)
        editor.apply()
    }

    fun getGalleryView(): Boolean {
        return sharedPreferences!!.getBoolean("galleryView", true)
    }

    fun saveSecurityQuestion(question: String?) {
        editor = sharedPreferences!!.edit()
        editor.putString("sec_ques", question)
        editor.apply()
    }

    fun getSecurityQuestion(): String? {
        return sharedPreferences!!.getString("sec_ques", null)
    }

    fun saveSecurityAnswer(answer: String?) {
        editor = sharedPreferences!!.edit()
        editor.putString("sec_ans", answer)
        editor.apply()
    }

    fun getSecurityAnswer(): String? {
        return sharedPreferences!!.getString("sec_ans", null)
    }

    fun savePin(pin: String?) {
        editor = sharedPreferences!!.edit()
        editor.putString("pin", pin)
        editor.apply()
    }

    fun getPin(): String? {
        return sharedPreferences!!.getString("pin", null)
    }

    fun pinSet(isSeteed: Boolean) {
        editor = sharedPreferences!!.edit()
        editor.putBoolean("pinSet", isSeteed)
        editor.apply()
    }

    fun isPinSeted(): Boolean {
        return sharedPreferences!!.getBoolean("pinSet", false)
    }
    fun isNightMode(nightMode: Boolean) {
        editor = sharedPreferences!!.edit()
        editor.putBoolean("night_mode", nightMode)
        editor.apply()
    }

    fun checkMode(): Boolean? {
        return sharedPreferences?.getBoolean("night_mode", false)
    }

    fun saveAdsTime(time: Long) {
        editor = sharedPreferences!!.edit()
        editor.putLong("adsTime", time)
        editor.apply()
    }

    fun getAdsTime(): Long {
        return sharedPreferences!!.getLong("adsTime", 0)
    }
}