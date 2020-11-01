package com.ayyukana.iliminsaduwa.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayyukana.iliminsaduwa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_registeration.*

class RegisterationActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var firebaseFireStore: FirebaseFirestore

    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration)

        auth = FirebaseAuth.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Registration")
        progressDialog.setMessage("please wait...")

        btn_register.setOnClickListener {

            if (TextUtils.isEmpty(email.text) || TextUtils.isEmpty(password.text) ||
                fullName.text.isEmpty() || conf_pass.text.isEmpty()
            ) {
                Toast.makeText(this, "please complete all field", Toast.LENGTH_SHORT).show()
                if (password.text.toString() != conf_pass.text.toString()) {
                    conf_pass.error = "Not same with password"
                }
            }
            progressDialog.show()
            auth.createUserWithEmailAndPassword(
                email.text.toString(),
                password.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid.toString()
                    val user = hashMapOf(
                        "fullName" to fullName.text.toString(),
                        "email" to email.text.toString(),
                    )
                    firebaseFireStore.collection("users")
                        .document(userId)
                        .set(user as Map<String, Any>).addOnSuccessListener {
                            progressDialog.cancel()
                            finish()
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, DominIyaliActivity::class.java))
                        }

                } else {
                    progressDialog.cancel()
                    Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        btn_login.setOnClickListener {
            finish()
            startActivity(Intent(this, LogInActivity::class.java))
        }
    }
}