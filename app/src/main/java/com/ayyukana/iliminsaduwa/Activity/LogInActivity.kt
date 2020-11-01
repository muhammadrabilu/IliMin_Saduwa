package com.ayyukana.iliminsaduwa.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayyukana.iliminsaduwa.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Login")
        progressDialog.setMessage("please wait...")


        btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(lEmail.text) || TextUtils.isEmpty(lEmail.text)) {
                Toast.makeText(this, "please complete all field", Toast.LENGTH_SHORT).show()
            }
            progressDialog.show()
            auth.signInWithEmailAndPassword(lEmail.text.toString(), lPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progressDialog
                        Toast.makeText(this, "Login Successesfull", Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(Intent(this, DominIyaliActivity::class.java))
                    } else {
                        Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }

        new_user.setOnClickListener {
            finish()
            startActivity(Intent(this, RegisterationActivity::class.java))
        }

    }
}