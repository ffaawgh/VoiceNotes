package com.example.voicenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.voicenotes.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding
    private lateinit var actionBar: ActionBar
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Профиль"

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.textView7.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        binding.createNote.setOnClickListener{
            startActivity(Intent(this, CreateNote::class.java))
        }

        binding.allNotes.setOnClickListener{
            startActivity(Intent(this, NoteList::class.java))
        }

    }

    private fun checkUser(){
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            val email = firebaseUser.email
            binding.textView8.text = email
        }
        else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}