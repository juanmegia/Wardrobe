package com.example.wardrobe.ui.screens.appScreens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.wardrobe.model.Prenda
import com.example.wardrobe.ui.navigation.Routes
import com.example.wardrobe.utils.CloudStorageManager
import com.example.wardrobe.utils.FirestoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrendasScreen(firestore: FirestoreManager, navigation : NavController,storage: CloudStorageManager) {
    var showAddPrendaDialog by remember { mutableStateOf(false) }
    var categoria by remember { mutableStateOf("") }
    var estilo by remember { mutableStateOf("") }
    val prendas by firestore.getPrendasFlow(estilo, categoria).collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    val categoriasRopa = arrayOf(
        "Camisetas",
        "Camisas",
        "Pantalones",
        "Vaqueros",
        "Jogger",
        "Sudaderas",
        "Zapatillas",
        "Zapatos"
    )
    val estiloRopa = arrayOf("Casual", "Formal", "Deportiva")
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddPrendaDialog = true
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Prenda")
            }
            if (showAddPrendaDialog) {
                AddPrendaDialog(
                    onPrendaAdded = { prenda ->
                        scope.launch {
                            firestore.addPrenda(prenda)
                        }
                        showAddPrendaDialog = false
                    },
                    onDialogDismissed = { showAddPrendaDialog = false },
                    storage = storage
                )
            }
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        TextField(
                            value = categoria,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = "Categoria") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categoriasRopa.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        categoria = item
                                        expanded = false
                                    }
                                )
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded2,
                        onExpandedChange = {
                            expanded2 = !expanded2
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
                            expanded = expanded2,
                            onDismissRequest = { expanded2 = false }
                        ) {
                            estiloRopa.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        estilo = item
                                        expanded2 = false
                                    }
                                )
                            }

                        }
                    }
                }
            }
            Button(
                onClick = {
                    estilo = ""
                    categoria = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Limpiar filtros")
            }
            if (!prendas.isNullOrEmpty()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    prendas.forEach {
                        item {
                            PrendaItem(prenda = it, firestore = firestore)
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
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No se encontraron \nPrendas",
                        fontSize = 18.sp, fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}
@Composable
fun PrendaItem(prenda: Prenda, firestore: FirestoreManager) {
    var showDeletePrendaDialog by remember { mutableStateOf(false) }

    val onDeletePrendaConfirmed: () -> Unit = {
        CoroutineScope(Dispatchers.Default).launch {
            firestore.deletePrenda(prenda.id ?: "")
        }
    }

    if (showDeletePrendaDialog) {
        DeletePrendaDialog(
            onConfirmDelete = {
                onDeletePrendaConfirmed()
                showDeletePrendaDialog = false
            },
            onDismiss = {
                showDeletePrendaDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.padding(6.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(text = prenda.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = prenda.brand + " - " +prenda.category + " - " +prenda.style,
                fontWeight = FontWeight.Thin,
                fontSize = 13.sp,
                lineHeight = 15.sp)
            IconButton(
                onClick = { showDeletePrendaDialog = true },
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
            }
        }
    }
}
@Composable
fun DeletePrendaDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Prenda") },
        text = { Text("¿Estás seguro que deseas eliminar la prenda?") },
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
fun AddPrendaDialog(onPrendaAdded: (Prenda) -> Unit, onDialogDismissed: () -> Unit, storage: CloudStorageManager) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    val categoriasRopa = arrayOf("Camisetas", "Camisas", "Pantalones", "Vaqueros", "Jogger", "Sudaderas", "Zapatillas", "Zapatos")
    val estiloRopa = arrayOf("Casual", "Formal", "Deportiva")
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var categoria by remember { mutableStateOf("") }
    var estilo by remember { mutableStateOf("") }
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    val scope = rememberCoroutineScope()
    var fileRef by remember { mutableStateOf("") }
    val context = LocalContext.current
    val file : Uri? =null

    //val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        //if (it) {
            //Toast.makeText(context, "Foto capturada", Toast.LENGTH_SHORT).show()
           // capturedImageUri = file
            //capturedImageUri?.let { uri ->
               // scope.launch {
               //     storage.uploadFile(file.name, uri)
                 //   fileRef = storage.getStorageReference().child(file.name).toString()
          //      }
           // }
        //} else {
          //  Toast.makeText(context, "No se capturo ninguna foto $it", Toast.LENGTH_SHORT).show()
        //}
    //}
    //val permissionLauncher = rememberLauncherForActivityResult(
      //  ActivityResultContracts.RequestPermission()) {
        //if (it) {
            //Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
          //  cameraLauncher.launch(uri)
      //  } else {
          //  Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        //}
    //}

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Agregar Prenda") },
        confirmButton = {
            Button(
                onClick = {
                    val newPrenda = Prenda(
                        name = name,
                        brand = brand,
                        category = categoria,
                        style = estilo,
                        url = fileRef)
                    onPrendaAdded(newPrenda)
                    name = ""
                    brand = ""
                    estilo = ""
                    categoria = ""
                    fileRef = ""
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
                // Button(onClick = {val permissionCheckResult =
                //  ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                //if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                //cameraLauncher.launch(uri)
                // } else {
                //        permissionLauncher.launch(Manifest.permission.CAMERA)
                //  }
                //},
                // ) {
                //      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Photo")
                // }

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Nombre") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = brand,
                    onValueChange = { brand = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    maxLines = 4,
                    label = { Text(text = "Brand") }
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
                            value = categoria,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = "Categoria") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categoriasRopa.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        categoria = item
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
                            value = estilo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = "Estilo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded2,
                            onDismissRequest = { expanded2 = false }
                        ) {
                            estiloRopa.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        estilo = item
                                        expanded2 = false
                                    }
                                )
                            }

                        }
                    }
                }
            }
        })}







