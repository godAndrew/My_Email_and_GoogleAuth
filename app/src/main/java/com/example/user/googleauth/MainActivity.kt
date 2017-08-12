package com.example.user.googleauth


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseUser
import android.text.TextUtils
import android.util.Log
import android.support.v7.widget.Toolbar
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import kotlinx.android.synthetic.main.activity_google_sign_in.*
//import jdk.nashorn.internal.runtime.ECMAException.getException
import android.content.ContentValues.TAG
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount




class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mAuth : FirebaseAuth
    //private lateinit var mAuthListener : FirebaseAuth.AuthStateListener
    val TAG : String = "EmailPassword"



    private fun  shortToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonLogin.setOnClickListener(this)
        buttonRegister.setOnClickListener(this)
        buttonLogOut.setOnClickListener(this)
        buttonVerifyEmail.setOnClickListener(this)
        redirect_gAuth.setOnClickListener(this)

        initToolbar()
        mAuth = FirebaseAuth.getInstance()

    }

    private fun initToolbar(){
        //val toolbar : Toolbar = findViewById(R.id.toolbarMain) as android.support.v7.widget.Toolbar
        val toolbar : Toolbar = toolbarMain as Toolbar
        setSupportActionBar(toolbar)
        //setSupportActionBar(toolbar)
        supportActionBar?.title = "Login/Register"
    }

    override fun onStart() {
        super.onStart()
        // check if user is signed in (non-null) and update UI accordingly
        val currentuser : FirebaseUser? = mAuth.currentUser
        updateUI(currentuser)
    }

    override fun onStop() {
        super.onStop()
    }

    private fun createAccount(email: String, password:String){
        Log.d(TAG,"create account" + email)
        if(!validateForm()){
            return
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> {task ->
                    if (task.isSuccessful()){
                        val user: FirebaseUser? = mAuth.currentUser
                        updateUI(user)
                    }else {
                        shortToast("Authentication failed")
                        updateUI(null)
                    }
        })
    }



    private fun signIn(email:String,password: String){
        if (!validateForm()){
            return
        }
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> {task ->
                    if (task.isSuccessful()){
                        val user : FirebaseUser? = mAuth.currentUser
                        updateUI(user)
                    }else{
                        shortToast("Login Fail")
                        updateUI(null)
                    }
                })
    }
    private fun signOut(){
        mAuth.signOut()
        updateUI(null)
    }
    private fun sendEmailVerification(){
        buttonVerifyEmail.setEnabled(false)

        // start send verification email
        val user: FirebaseUser? = mAuth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener(this, OnCompleteListener<Void>{ task ->
            buttonVerifyEmail.setEnabled(true)

            if (task.isSuccessful()){
                shortToast("Verificarion email sent to " + user.email)
            }else{
                shortToast("Failed to send email")
            }
        })
    }



    private fun  validateForm(): Boolean {
        var valid : Boolean = true

        val email : String = editEmail.text.toString()
        if(TextUtils.isEmpty(email)){
            shortToast("E-mail required")
            valid = false
        }else{editEmail.setError(null)}

        val password : String = editPassword.text.toString()

        if(TextUtils.isEmpty(password)){
            shortToast("Password required")
            valid = false
        }else{editPassword.setError(null)}
        return valid
    }

    private fun updateUI(user:FirebaseUser?){
        if (user != null){
            statusText.text = getString(R.string.emailpassword_status_fmt,user.email,
                    user.isEmailVerified)
            detailText.setText(getString(R.string.firebase_status_fmt,user.uid))
            email_password_buttons.setVisibility(View.GONE)
            email_password_fields.setVisibility(View.GONE)
            signed_in_buttons.setVisibility(View.VISIBLE)
            redirect_gAuth.visibility = View.GONE
            buttonVerifyEmail.setEnabled(!user.isEmailVerified())

        }else{

            statusText.setText(R.string.signed_out)
            detailText.setText(null)

            email_password_buttons.setVisibility(View.VISIBLE)
            email_password_fields.setVisibility(View.VISIBLE)
            signed_in_buttons.setVisibility(View.GONE)
            redirect_gAuth.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            buttonRegister -> {
                createAccount(editEmail.text.toString(),editPassword.text.toString())
            }
            buttonLogin -> {
                signIn(editEmail.text.toString(),editPassword.text.toString())
            }
            buttonVerifyEmail -> {
                sendEmailVerification()
            }
            buttonLogOut -> {
                signOut()
            }
            redirect_gAuth ->{
                val intent : Intent = Intent(this,GoogleSignInActivity::class.java)
                startActivity(intent)
            }
        }
    }
}




