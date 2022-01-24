package com.example.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phonebook = getPhonebook()
    }

    external fun getPhonebook(): Array<PhonebookEntry>

    companion object {
        // Used to load the 'testapp' library on application startup.
        init {
            System.loadLibrary("testapp")
        }
    }
}

class PhonebookEntry(name: String, number: String)