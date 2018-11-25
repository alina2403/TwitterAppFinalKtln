package com.example.alina.twitterappfinalktln

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*


class Login_activity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var loginProgress: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mAuth = FirebaseAuth.getInstance()

        reg_login_btn.setOnClickListener {
            val loginEmail = reg_email.text.toString()
            val loginPassword = reg_pass.text.toString()

            if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {
                loginProgress!!.visibility = View.VISIBLE
                mAuth!!.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendToMain()
                    } else {
                        val e = task.exception!!.message
                        Toast.makeText(this@Login_activity, e, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            sendToMain()
        }
    }

    private fun sendToMain() {
        val mainIntent = Intent(this@Login_activity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
