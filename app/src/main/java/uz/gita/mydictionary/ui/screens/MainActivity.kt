package uz.gita.mydictionary.ui.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import uz.gita.mydictionary.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val graph = navigation.navController.navInflater.inflate(R.navigation.app_nav)
        navigation.navController.graph = graph

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}