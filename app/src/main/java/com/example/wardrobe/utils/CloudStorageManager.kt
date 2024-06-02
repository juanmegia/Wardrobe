package com.example.wardrobe.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class CloudStorageManager(context: Context) {
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val authManager = AuthManager(context)
    private val userId = authManager.getCurrentUser()?.uid

    //fun uploadToStorage(uri: Uri, context: Context){
      //  var spaceRef = storageRef.child("images/$unique_image_name.jpg")
        //val byteArray: ByteArray? = context.contentResolver.openInputStream(uri)?.use{it.readBytes()}
        //byteArray?.let {
          //  var uploadtask = spaceRef.putBytes(byteArray)
            //uploadtask.addOnFailureListener{
              //  Toast.makeText(
                //    context, "upload failed", Toast.LENGTH_SHORT).show()


            //}.addOnSuccessListener { taskSnapshot ->
              //  Toast.makeText(
                //    context, "upload successed", Toast.LENGTH_SHORT).show()
            //}
        //}
    //}


    fun getStorageReference(): StorageReference {
        return storageRef.child("photos").child(userId ?: "")
    }

    suspend fun uploadFile(fileName: String, filePath: Uri) {
        val fileRef = getStorageReference().child(fileName)
        val uploadTask = fileRef.putFile(filePath)
        uploadTask.await()
    }

    suspend fun getUserImages(): List<String> {
        val imageUrls = mutableListOf<String>()
        val listResult: ListResult = getStorageReference().listAll().await()
        for (item in listResult.items) {
            val url = item.downloadUrl.await().toString()
            imageUrls.add(url)
        }
        return imageUrls
    }
}