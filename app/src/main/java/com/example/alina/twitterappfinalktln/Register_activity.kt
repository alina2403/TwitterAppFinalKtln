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

class Register_activity : AppCompatActivity() {
    private var reg_progress: ProgressBar? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        reg_progress = findViewById(R.id.reg_progress)

        reg_login_btn.setOnClickListener { finish() }
        reg_btn.setOnClickListener {
            val email = reg_email!!.text.toString()
            val pass = reg_pass!!.text.toString()
            val confirm_pass = reg_confirm_pass!!.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) and !TextUtils.isEmpty(confirm_pass)) {

                if (pass == confirm_pass) {

                    reg_progress!!.visibility = View.VISIBLE

                    mAuth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val setupIntent = Intent(this@Register_activity, SetUp_activity::class.java)
                            startActivity(setupIntent)
                            finish()
                        } else {
                            val errorMessage = task.exception!!.message
                            Toast.makeText(this@Register_activity, "Error : $errorMessage", Toast.LENGTH_LONG).show()
                        }
                        reg_progress!!.visibility = View.INVISIBLE
                    }

                } else {

                    Toast.makeText(
                        this@Register_activity,
                        "Confirm Password and Password Field doesn't match.",
                        Toast.LENGTH_LONG
                    ).show()

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

        val mainIntent = Intent(this@Register_activity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}

