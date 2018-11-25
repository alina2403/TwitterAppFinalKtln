package com.example.alina.twitterappfinalktln

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.text.TextUtils
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_new_post.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class NewPost_activity : AppCompatActivity() {

    var postImageUri : Uri? = null
    var storageReference = FirebaseStorage.getInstance()
    var firebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var compressedImageFile : Bitmap
    var current_user_id = String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        setSupportActionBar(new_post_toolbar)
        supportActionBar!!.title = "new post"

        new_post_image.setOnClickListener{
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(this@NewPost_activity)
        }
        post_btn.setOnClickListener{
            var desc = new_post_desc.text.toString()
            if (!TextUtils.isEmpty(desc) && postImageUri !=null){
                new_post_progress.visibility=VISIBLE
                var randomName = UUID.randomUUID().toString()
                var newImageFile = File(postImageUri!!.path)
                try
                {
                    var compressedImageFile = Compressor(this@NewPost_activity)
                        .setMaxHeight(720)
                        .setMaxWidth(720)
                        .setQuality(50)
                        .compressToBitmap(newImageFile)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }

                val baos = ByteArrayOutputStream()
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageData = baos.toByteArray()

                val filePath = storageReference.reference.child("post_images").child(randomName + ".jpg").putBytes(imageData)
                filePath.addOnCompleteListener(object: OnCompleteListener<UploadTask.TaskSnapshot>{
                    override fun onComplete(@NonNull task:Task<UploadTask.TaskSnapshot>) {
                        val downloadUri = task.result!!.uploadSessionUri.toString()
                        if (task.isSuccessful()) {
                            val newThumbFile = File(postImageUri!!.getPath())
                            try {
                                var compressedImageFile = Compressor(this@NewPost_activity)
                                    .setMaxHeight(100)
                                    .setMaxWidth(100)
                                    .setQuality(1)
                                    .compressToBitmap(newThumbFile)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            val baos = ByteArrayOutputStream()
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val thumbData = baos.toByteArray()
                            val uploadTask = storageReference.reference.child("post_images/thumbs")
                                .child(randomName + ".jpg").putBytes(thumbData)
                            uploadTask.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                                    val downloadthumbUri = taskSnapshot.uploadSessionUri.toString()
                                    val postMap = HashMap<Any,Any>()
                                    postMap.put("image_url", downloadUri)
                                    postMap.put("image_thumb", downloadthumbUri)
                                    postMap.put("desc", desc)
                                    postMap.put("user_id", current_user_id)
                                    postMap.put("timestamp", FieldValue.serverTimestamp())
                                    firebaseFirestore.collection("Posts").add(postMap)
                                        .addOnCompleteListener(object : OnCompleteListener<DocumentReference>{
                                            override fun onComplete(@NonNull task: Task<DocumentReference>) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(
                                                        this@NewPost_activity,
                                                        "Post was added",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    val mainIntent =
                                                        Intent(this@NewPost_activity, MainActivity::class.java)
                                                    startActivity(mainIntent)
                                                    finish()
                                                } else {
                                                }
                                                new_post_progress.setVisibility(View.INVISIBLE)
                                            }
                                        })
                                }
                            }).addOnFailureListener(object : OnFailureListener {
                                override fun onFailure(e: Exception) {
                                    //Error handling
                                }
                            })
                            run({
                                new_post_progress.setVisibility(View.INVISIBLE)
                            })
                        }}})}}
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {


                   new_post_image.setImageURI(postImageUri)

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    val error = result.getError()

                }}}}}

