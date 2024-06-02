package com.example.wardrobe.ui.screens.appScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wardrobe.ui.navigation.Routes
import com.example.wardrobe.utils.AuthManager
import com.example.wardrobe.utils.CloudStorageManager
import com.example.wardrobe.utils.FirestoreManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(firestore: FirestoreManager, navigation : NavController, authManager: AuthManager){
    val prendas by firestore.getPrendasFlow("", "").collectAsState(initial = emptyList())
    val numeroPrendas = prendas.count()
    val outfits by firestore.getOutfitsFlow().collectAsState(initial = emptyList())
    val numeroOutfits = outfits.count()

    Column{
        Card(
            modifier = Modifier.padding(6.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Tienes ${numeroPrendas} Prendas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,)
            }
        }
        Card(
            modifier = Modifier.padding(6.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Tienes ${numeroOutfits} Outfits",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,)
            }
        }
    }
}