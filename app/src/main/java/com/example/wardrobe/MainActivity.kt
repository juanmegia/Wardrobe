package com.example.wardrobe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wardrobe.ui.navigation.Navigation
import com.example.wardrobe.ui.theme.WardrobeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WardrobeTheme {
                Navigation(this)
            }
        }
    }


}