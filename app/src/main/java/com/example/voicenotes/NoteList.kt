package com.example.voicenotes

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicenotes.databinding.CreateNoteBinding
import com.example.voicenotes.databinding.NoteListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NoteList : AppCompatActivity(){

    lateinit var binding: CreateNoteBinding
    lateinit var bindingNew: NoteListBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var adapter: AdapterNote

    @RequiresApi(Build.VERSION_CODES.O)
    var date : LocalDateTime = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    @RequiresApi(Build.VERSION_CODES.O)
    var outputDate: String = formatter.format(date)

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = CreateNoteBinding.inflate(layoutInflater)
        bindingNew = NoteListBinding.inflate(layoutInflater)
        setContentView(bindingNew.root)
        firebaseAuth = Firebase.auth
        //setUpActionBar()
        val database = Firebase.database
        val myRef = database.getReference("notes")
        binding.bSend.setOnClickListener {
            myRef.child(myRef.push().key ?: "blabla").setValue(Note( "Дата:" + outputDate, "Пользователь:" + firebaseAuth.currentUser?.email,  "Название:" + binding.edName.text.toString(), "Данные:" + binding.edMessage.text.toString()))
        }
        onChangeListener(myRef)
        initRcView()
    }

    private fun initRcView() = with(bindingNew){
        adapter = AdapterNote()
        rcView.layoutManager = LinearLayoutManager(this@NoteList)
        rcView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.textView7){
            firebaseAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onChangeListener(dRef: DatabaseReference){
        dRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Note>()
                for(s in snapshot.children){
                    val note = s.getValue(Note::class.java)
                    if(note != null)list.add(note)
                }
                adapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}