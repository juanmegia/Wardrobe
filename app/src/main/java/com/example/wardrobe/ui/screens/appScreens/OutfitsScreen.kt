package com.example.wardrobe.ui.screens.appScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wardrobe.model.Outfit
import com.example.wardrobe.utils.AuthManager
import com.example.wardrobe.utils.CloudStorageManager
import com.example.wardrobe.utils.FirestoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(firestore: FirestoreManager, navigation : NavController, authManager: AuthManager){
    var showAddOutfitDialog by remember { mutableStateOf(false) }
    val outfits by firestore.getOutfitsFlow().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddOutfitDialog = true
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact")
            }

            if (showAddOutfitDialog) {
                AddOutfitDialog(
                    onOutfitAdded = { outfit ->
                        scope.launch {
                            firestore.addOutfit(outfit)
                        }
                            showAddOutfitDialog = false

                    },
                    onDialogDismissed = { showAddOutfitDialog = false },
                    authManager = authManager,
                    firestore =firestore
                )
            }
        }
    ) { _  ->
        if(!outfits.isNullOrEmpty()) {
            LazyColumn {
                outfits.forEach { outfit ->
                    item {
                        OutfitItem(outfit = outfit, firestore = firestore)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "No se encontraron \nOutfits",
                    fontSize = 18.sp, fontWeight = FontWeight.Thin, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun OutfitItem(outfit: Outfit, firestore: FirestoreManager) {
    var showDeleteOutfitDialog by remember { mutableStateOf(false) }

    val onDeleteOutfitConfirmed: () -> Unit = {
        CoroutineScope(Dispatchers.Default).launch {
            firestore.deleteOutfit(outfit.id ?: "")
        }
    }

    if (showDeleteOutfitDialog) {
        DeleteOutfitDialog(
            onConfirmDelete = {
                onDeleteOutfitConfirmed()
                showDeleteOutfitDialog = false
            },
            onDismiss = {
                showDeleteOutfitDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 0.dp)
            .fillMaxWidth())
    {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(3f)) {
                Text(
                    text = outfit.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = outfit.style,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = outfit.prenda1 + "\n"+ outfit.prenda2+ "\n"+ outfit.prenda3+ "\n"+ outfit.prenda4,
                    fontWeight = FontWeight.Thin,
                    fontSize = 12.sp,
                    maxLines =6 ,
                    overflow = TextOverflow.Ellipsis)
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    onClick = {
                        showDeleteOutfitDialog = true
                    },
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
                }
            }
        }
    }
}
@Composable
fun DeleteOutfitDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar outfit") },
        text = { Text("¿Estás seguro que deseas eliminar el outfit?") },
        confirmButton = {
            Button(
                onClick = onConfirmDelete
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOutfitDialog(onOutfitAdded: (Outfit) -> Unit, onDialogDismissed: () -> Unit, authManager: AuthManager, firestore: FirestoreManager) {
    var name by remember { mutableStateOf("") }
    var estilo by remember { mutableStateOf("") }
    val estiloRopa = arrayOf("Casual", "Formal", "Deportiva")
    var prenda1 by remember { mutableStateOf("") }
    var prenda2 by remember { mutableStateOf("") }
    var prenda3 by remember { mutableStateOf("") }
    var prenda4 by remember { mutableStateOf("") }
    var uid = authManager.getCurrentUser()?.uid
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var expanded3 by remember { mutableStateOf(false) }
    var expanded4 by remember { mutableStateOf(false) }
    var expanded5 by remember { mutableStateOf(false) }
    val prendas by firestore.getPrendasFlow(estilo= "", categoria="").collectAsState(initial = emptyList())
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Agregar Contacto") },
        confirmButton = {
            Button(
                onClick = {
                    val newOutfit = Outfit(
                        name = name,
                        style = estilo,
                        prenda1 = prenda1,
                        prenda4 = prenda4,
                                prenda2 = prenda2,
                                prenda3 = prenda4,
                        userId = uid.toString())
                    onOutfitAdded(newOutfit)
                    name = ""
                    prenda1 = ""
                    prenda2 = ""
                    prenda3 = ""
                    prenda4 =""
                }
            ) {
                Text(text = "Agregar")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDialogDismissed()
                }
            ) {
                Text(text = "Cancelar")
            }
        },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Nombre") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        TextField(
                            value = estilo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = "Estilo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            estiloRopa.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        estilo = item
                                        expanded = false
                                    }
                                )
                            }

                        }
                    }
                }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded2,
                            onExpandedChange = {
                                expanded2 = !expanded2
                            }
                        ) {
                            TextField(
                                value = prenda1,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(text = "Prenda1") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded2,
                                onDismissRequest = { expanded = false }
                            ) {
                                prendas.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item.name) },
                                        onClick = {
                                            prenda1 = item.name
                                            expanded2 = false
                                        }
                                    )
                                }

                            }
                        }
                    }
                Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expanded3,
                                onExpandedChange = {
                                    expanded3 = !expanded3
                                }
                            ) {
                                TextField(
                                    value = prenda2,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(text = "Prenda2") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        )
                                    },
                                    modifier = Modifier.menuAnchor()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded3,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    prendas.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(text = item.name) },
                                            onClick = {
                                                prenda2 = item.name
                                                expanded3 = false
                                            }
                                        )
                                    }

                                }
                            }
                        }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = expanded4,
                                    onExpandedChange = {
                                        expanded4 = !expanded4
                                    }
                                ) {
                                    TextField(
                                        value = prenda3,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(text = "Prenda3") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },
                                        modifier = Modifier.menuAnchor()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded4,
                                        onDismissRequest = { expanded4 = false }
                                    ) {
                                        prendas.forEach { item ->
                                            DropdownMenuItem(
                                                text = { Text(text = item.name) },
                                                onClick = {
                                                    prenda3 = item.name
                                                    expanded4 = false
                                                }
                                            )
                                        }

                                    }
                                }
                            }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    ExposedDropdownMenuBox(
                                        expanded = expanded5,
                                        onExpandedChange = {
                                            expanded5 = !expanded5
                                        }
                                    ) {
                                        TextField(
                                            value = prenda4,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text(text = "Prenda4") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expanded5
                                                )
                                            },
                                            modifier = Modifier.menuAnchor()
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expanded5,
                                            onDismissRequest = { expanded5 = false }
                                        ) {
                                            prendas.forEach { item ->
                                                DropdownMenuItem(
                                                    text = { Text(text = item.name) },
                                                    onClick = {
                                                        prenda4 = item.name
                                                        expanded5 = false
                                                    }
                                                )
                                            }

                                        }
                                    }
                                }


            }
        }
    )
}




