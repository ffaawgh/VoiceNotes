package com.example.voicenotes

import android.app.Activity
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.voicenotes.databinding.CreateNoteBinding
import com.example.voicenotes.databinding.NoteListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


/*
            Объект "Заметка".
Поля:
        дата заметки
        кем создана заметка
        название заметки (например, станок №28)
        содержание заметки (перевод в текст)
*/

class CreateNote : AppCompatActivity(){

    private val RQ_SPEECH_REC_NAME= 102
    private val RQ_SPEECH_REC_TEXT= 103

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
        setContentView(binding.root)
        firebaseAuth = Firebase.auth
        val database = Firebase.database
        val myRef = database.getReference("notes")
        binding.bSend.setOnClickListener {
            myRef.child(myRef.push().key ?: "blabla").setValue(Note( "Дата:" + outputDate, "Пользователь:" + firebaseAuth.currentUser?.email, "Название:" + binding.edName.text.toString(), "Данные:" + binding.edMessage.text.toString()))
        }

        onChangeListener(myRef)
        initRcView()

        binding.btRecord.setOnClickListener(){
            askSpeechInputName()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SPEECH_REC_NAME && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.edName.setText(result?.get(0)?.capitalize())
            askSpeechInputText()
            Log.d("чек 1","Вызов второй функции")
        }


        if (requestCode == RQ_SPEECH_REC_TEXT && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.edMessage.setText(result?.get(0)?.capitalize())
            binding.tvText.setText(result?.get(0)?.capitalize())
        }
    }

    private fun askSpeechInputName(){
        if (!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this, "Голосовая запись недоступна!",Toast.LENGTH_SHORT).show()
        }
        else{
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Назовите имя заметки!")
            startActivityForResult(i, RQ_SPEECH_REC_NAME)
            Log.d("чек 2","Отправка интента на активность")
        }
    }

    private fun askSpeechInputText(){
        if (!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this, "Голосовая запись недоступна!",Toast.LENGTH_SHORT).show()
        }
        else{
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажите что-нибудь!")
            startActivityForResult(i, RQ_SPEECH_REC_TEXT)
        }
    }

    private fun initRcView() = with(bindingNew){
        adapter = AdapterNote()
        rcView.layoutManager = LinearLayoutManager(this@CreateNote)
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
        dRef.addValueEventListener(object:ValueEventListener{
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









