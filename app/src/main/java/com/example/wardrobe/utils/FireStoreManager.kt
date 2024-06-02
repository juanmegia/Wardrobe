package com.example.wardrobe.utils

import android.content.Context
import com.example.wardrobe.model.Outfit
import com.example.wardrobe.model.Prenda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreManager(context: Context) {
    private val firestore = FirebaseFirestore.getInstance()

    private val auth = AuthManager(context)
    var userId = auth.getCurrentUser()?.uid

    suspend fun addPrenda(prenda: Prenda) {
        prenda.userId = userId.toString()
        firestore.collection("prendas").add(prenda).await()
    }

    suspend fun updatePrenda(prenda: Prenda) {
        val prendaRef = prenda.id?.let { firestore.collection("prendas").document(it) }
        prendaRef?.set(prenda)?.await()
    }

    suspend fun deletePrenda(prendaId: String) {
        val prendaRef = firestore.collection("prendas").document(prendaId)
        prendaRef.delete().await()
    }

    fun getPrendasFlow(estilo: String, categoria: String): Flow<List<Prenda>> = callbackFlow {
        var prendasRef = firestore.collection("prendas")
            .whereEqualTo("userId", userId).orderBy("name")
        if (estilo == "" && categoria != "") {
            prendasRef = firestore.collection("prendas")
                .whereEqualTo("userId", userId).whereEqualTo("category", categoria).orderBy("name")
        }
        if (estilo != "" && categoria == "") {
            prendasRef = firestore.collection("prendas")
                .whereEqualTo("userId", userId).whereEqualTo("style", estilo).orderBy("name")
        }
        if (estilo != "" && categoria != "") {
            prendasRef = firestore.collection("prendas")
                .whereEqualTo("userId", userId).whereEqualTo("style", estilo)
                .whereEqualTo("category", categoria).orderBy("name")
        }


        val subscription = prendasRef.addSnapshotListener { snapshot, _ ->
            snapshot?.let { querySnapshot ->
                val prendas = mutableListOf<Prenda>()
                for (document in querySnapshot.documents) {
                    val prenda = document.toObject(Prenda::class.java)
                    prenda?.id = document.id
                    prenda?.let { prendas.add(it) }
                }
                trySend(prendas).isSuccess
            }
        }
        awaitClose { subscription.remove() }
    }

    fun getOutfitsFlow(): Flow<List<Outfit>> = callbackFlow {
        var outfitsRef = firestore.collection("outfits")
            .whereEqualTo("userId", userId).orderBy("name")
        val subscription = outfitsRef.addSnapshotListener { snapshot, _ ->
            snapshot?.let { querySnapshot ->
                val prendas = mutableListOf<Outfit>()
                for (document in querySnapshot.documents) {
                    val Outfit = document.toObject(Outfit::class.java)
                    Outfit?.id = document.id
                    Outfit?.let { prendas.add(it) }
                }
                trySend(prendas).isSuccess
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun addOutfit(outfit: Outfit) {
        outfit.userId = userId.toString()
        firestore.collection("outfits").add(outfit).await()
    }

    suspend fun deleteOutfit(outfitId: String) {
        val prendaRef = firestore.collection("outfits").document(outfitId)
        prendaRef.delete().await()
    }
}