package com.example.symbolrecognition

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*
//TODO Dodelat ukladani accuracy do shared Preferences
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        accuracy_value.text = getCurrentConvertedAccuracy().toString()
        Log.i("Accuracy", getCurrentConvertedAccuracy().toString())
        accuracy_seekbar.progress = getCurrentConvertedAccuracy()
        //https://android--code.blogspot.com/2018/02/android-kotlin-seekbar-example.html
        accuracy_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                accuracy_value.text = i.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        }

    fun saveSettings(view: View)
    {

        val value = accuracy_seekbar.progress.toFloat()/100
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        with (sharedPref.edit()) {
            putFloat(getString(R.string.settings_accuracy), value)
            commit()
        }
        Toast.makeText(this,"The accuracy value was saved.", Toast.LENGTH_SHORT).show()
        var intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Metoda vraci aktualni presnost, prevedeno z float 0-1 na int 0-100
     */
    private fun getCurrentConvertedAccuracy()  : Int {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val accurayFloat = sharedPref.getFloat(getString(R.string.settings_accuracy),Constants.ACCURACY_DEFAULT_VALUE)
        return (accurayFloat*100).toInt()
    }


}
