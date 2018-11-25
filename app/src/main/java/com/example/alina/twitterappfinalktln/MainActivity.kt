package com.example.alina.twitterappfinalktln

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.support.annotation.NonNull
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mainToolbar: Toolbar? = null
    private var mAuth: FirebaseAuth? = null
    private var firebaseFirestore: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()



        add_post_btn.setOnClickListener {
            val newPost = Intent(this@MainActivity, NewPost_activity::class.java)
            startActivity(newPost)
        }

    }

    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            sendtoLogin()
        }
        else {
            var current_user_id = mAuth!!.currentUser!!.uid
            firebaseFirestore!!.collection("Users").document(current_user_id).get()
                .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                    override fun onComplete(@NonNull task: Task<DocumentSnapshot>) {
                        if (task.isSuccessful()) {
                            if (!task.getResult()!!.exists()) {
                                val setupIntent = Intent(this@MainActivity, SetUp_activity::class.java)
                                startActivity(setupIntent)
                                finish()
                            }
                        } else {
                            val errorMessage = task.getException()!!.message
                            Toast.makeText(this@MainActivity, "Error : " + errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout_btn -> {
                logout()
                return true
            }

            else -> return false
        }

        return false
    }

    private fun logout() {
        mAuth!!.signOut()
        sendtoLogin()
    }

    private fun sendtoLogin() {
        val loginIntent = Intent(this@MainActivity, Login_activity::class.java)
        startActivity(loginIntent)
        finish()
    }
}
