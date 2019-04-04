package com.example.symbolrecognition

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*
//TODO Dodelat ukladani accuracy do shared Preferences
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        accuracy_seekbar.progress = getCurrentConvertedAccuracy()
    }
    fun saveSettings(view: View)
    {
        Toast.makeText(this,accuracy_seekbar.progress.toString(),Toast.LENGTH_SHORT).show()
    }

    /**
     * Metoda vraci aktualni presnost, prevedeno z float 0-1 na int 0-100
     */
    private fun getCurrentConvertedAccuracy()  : Int {
        val sharedPref = (this as Activity)?.getPreferences(Context.MODE_PRIVATE)
        val accurayFloat = sharedPref.getFloat(getString(R.string.settings_accuracy),Constants.ACCURACY_DEFAULT_VALUE)
        return (accurayFloat*100).toInt()
    }

}
