package com.example.voicenotes

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.example.voicenotes.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth

class SingUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingUpBinding
    private lateinit var actionBar:ActionBar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Регистрация"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Пожалуйста, подождите")
        progressDialog.setMessage("Аккаунт создаётся...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView3.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.button2.setOnClickListener {
            validateData()
        }
    }

    private fun validateData(){
        //получение данных
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //валидация данных
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Неверный формат email-адреса!"
        }
        else if (TextUtils.isEmpty(password)){
            binding.passwordEt.error = "Пожалуйста, введите пароль!"
        }
        else if (password.length < 6){
            binding.passwordEt.error = "Длина пароля должна быть больше 6 знаков!"
        }
        else {
            firebaseSingUp()
        }
    }

    private fun firebaseSingUp() {
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //успешный вход в систему
                progressDialog.dismiss()
                //получение информации о пользователе
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Создан аккаунт с именем $email", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                //неуспешный вход в систему
                progressDialog.dismiss()
                Toast.makeText(this,"Не удалось зарегистрироваться из-за ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //возвращение к активности, когда кнопка нажата
        return super.onSupportNavigateUp()
    }
}