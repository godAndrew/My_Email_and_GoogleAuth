package com.example.user.googleauth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.support.annotation.NonNull
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import kotlinx.android.synthetic.main.activity_google_sign_in.*


class GoogleSignInActivity : AppCompatActivity()
        ,GoogleApiClient.OnConnectionFailedListener
        ,View.OnClickListener {

    val TAG : String = "GoogleActivity"
    val RC_SIGN_IN : Int = 9001

    // declare_auth
    lateinit var mAuth : FirebaseAuth
    // end declare_auth

    lateinit var mGoogleApiClient : GoogleApiClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        //Button Listeners
        google_sign_in_button.setOnClickListener(this)
        google_sign_out_button.setOnClickListener(this)
        google_disconnect_button.setOnClickListener(this)

        //Config Google Sign In
        var gso : GoogleSignInOptions = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()
        //end config_sign_in

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this/*on conection FAiledListener*/)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build()

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        // check if user is signed in (non-null) and updateUI Accordingly
        var currentUser :FirebaseUser? = mAuth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the intent from
        // googleSignINApi.getSingInIntent()
        if (requestCode == RC_SIGN_IN) {
            var result: GoogleSignInResult = Auth.GoogleSignInApi
                    .getSignInResultFromIntent(data)
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                var account: GoogleSignInAccount? = result.getSignInAccount()
                firebaseAuthWithGoogle(account)
            } else {
                // google SingIn Failed, Upda
                updateUI(null)
            }
        }
    }
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

        var credential : AuthCredential = GoogleAuthProvider
                .getCredential(account?.idToken,null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> {task ->

                    if (task.isSuccessful){
                        var user : FirebaseUser? = mAuth.currentUser
                        updateUI(user)
                    }else{
                        Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
        })
    }



    private fun signIn(){
        var signInIntent : Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun signOut(){
        mAuth.signOut()
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            updateUI(null)
        }
    }


    private fun updateUI(user: FirebaseUser?) {
        if(user != null){

            google_status.setText(getString(R.string.google_status_fmt,user.email))
            google_detail.setText(getString(R.string.firebase_status_fmt,user.uid))
            google_sign_in_button.visibility = View.GONE
            google_sign_out_and_disconnect.visibility = View.VISIBLE
        }else{

            google_status.setText(R.string.signed_out)
            google_detail.setText(null)
            google_sign_in_button.visibility = View.VISIBLE
            google_sign_out_and_disconnect.visibility = View.GONE
        }
    }

    private fun revokeAcces(){
        mAuth.signOut()
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback { updateUI(null) }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this,"Google Play Services error.",Toast.LENGTH_SHORT).show()
    }

    override fun onClick(p0: View?) {
        when (p0){
            google_sign_in_button -> {
                signIn()
            }
            google_sign_out_button -> {
                signOut()
                finish()
            }
            google_disconnect_button -> {
                revokeAcces()

            }
        }
    }

}












