package com.ayyukana.iliminsaduwa.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayyukana.iliminsaduwa.R
import com.ayyukana.iliminsaduwa.model.Constant
import com.ayyukana.iliminsaduwa.model.DominIyaliModel
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_video.*
import kotlin.random.Random


class UploadVideoActivity : AppCompatActivity() {

    private val TAG = UploadVideoActivity::class.java.name
    private val PICTURE_RESULT = 42
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mStorageRef: StorageReference
    private lateinit var mfirebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    var aToZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" // 36 letter.

    lateinit var progressDialog: ProgressDialog
    lateinit var dominIyaliModel: DominIyaliModel

    private fun generateRandom(aToZ: String): String? {
        val rand = Random
        val res = StringBuilder()
        for (i in 0..16) {
            val randIndex: Int = rand.nextInt(aToZ.length)
            res.append(aToZ[randIndex])
        }
        return res.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

        val id = generateRandom(aToZ)
        Log.d(TAG, "onCreate: $id")


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait...")


        mStorage = FirebaseStorage.getInstance()
        mStorageRef = mStorage.reference.child("private_video")
        mfirebaseDatabase = FirebaseDatabase.getInstance()

        mDatabaseReference = mfirebaseDatabase.getReference("video")
        dominIyaliModel = DominIyaliModel()

        getVideoFromStorage()

        val video = intent.getParcelableExtra<DominIyaliModel>(Constant.SELECTED_ITEMS)

        vTitle.setText(video?.title)
        VideoURL.setText(video?.videoURL)
        showVideo(video?.videoURL.toString())

        btnAddVideo.setOnClickListener {
            if (!TextUtils.isEmpty(VideoURL.text.toString())) {
                dominIyaliModel.videoURL = VideoURL.text.toString()
            }
            if (TextUtils.isEmpty(vTitle.text.toString())) {
                vTitle.error = "Please type title"
            } else {
                dominIyaliModel.title = vTitle.text.toString()
                dominIyaliModel.id = id
                mDatabaseReference.child(id.toString()).setValue(dominIyaliModel)
                backTodominIyali()
            }
        }

        btnUpdate.setOnClickListener {
            updateVideo()
            backTodominIyali()
        }

        btnDelete.setOnClickListener {
            deleteVideo()
            backTodominIyali()
        }
    }

    fun getVideoFromStorage() {
        btnGetVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/mp4"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Insert Video"
                ), PICTURE_RESULT
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressDialog.show()
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            val videoURI = data?.data
            val ref = mStorageRef.child(videoURI?.lastPathSegment + ".mp4")
            ref.putFile(videoURI!!).addOnSuccessListener { taskSnapshot ->
                ref.downloadUrl.addOnSuccessListener { uri ->
                    dominIyaliModel.videoURL = uri.toString()
                    dominIyaliModel.videoName = taskSnapshot?.storage?.path
                    showVideo(uri.toString())
                    progressDialog.dismiss()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun showVideo(videoUri: String) {
        if (!videoUri.isEmpty()) {
            Glide.with(videoViewSample.context)
                .load(videoUri)
                .centerCrop()
                .into(videoViewSample)
        }
    }

    fun backTodominIyali() {
        startActivity(Intent(this, DominIyaliActivity::class.java))
        finish()
    }

    fun updateVideo() {
        val video = intent.getParcelableExtra<DominIyaliModel>(Constant.SELECTED_ITEMS)
        dominIyaliModel.title = vTitle.text.toString()
        dominIyaliModel.videoURL = VideoURL.text.toString()
        dominIyaliModel.id = video?.id.toString()
        mDatabaseReference.child(video?.id.toString()).setValue(dominIyaliModel)
    }

    private fun deleteVideo() {
        progressDialog.show()
        val video = intent.getParcelableExtra<DominIyaliModel>(Constant.SELECTED_ITEMS)
        val videoURL = video!!.videoURL.toString()
        mDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(video.id.toString()).hasChild("videoName")) {
                    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(videoURL)
                    storageRef.delete().addOnSuccessListener {
                        Toast.makeText(
                            this@UploadVideoActivity,
                            "Deleted Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        mDatabaseReference.child(video.id.toString()).removeValue()
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this@UploadVideoActivity,
                            e.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
                } else {
                    mDatabaseReference.child(video.id.toString()).removeValue()
                    Toast.makeText(
                        this@UploadVideoActivity,
                        "Deleted Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }

        })

    }

}