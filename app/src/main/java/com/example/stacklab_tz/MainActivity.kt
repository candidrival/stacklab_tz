package com.example.stacklab_tz

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stacklab_tz.presentation.main.MainScreenFragment
import com.example.stacklab_tz.utils.doubleClickExit

class MainActivity : AppCompatActivity() {

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (getCurrentFragment()) {
                is MainScreenFragment -> {
                    doubleClickExit(this@MainActivity)
                }

                else -> navHostFragment?.findNavController()?.popBackStack()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    fun getCurrentFragment(): Fragment? {
        navHostFragment?.childFragmentManager?.backStackEntryCount
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }
}