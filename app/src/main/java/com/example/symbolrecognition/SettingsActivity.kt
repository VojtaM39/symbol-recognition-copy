package com.example.symbolrecognition

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
    fun saveSettings(view: View)
    {
        Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show()
    }

}
