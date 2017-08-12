package com.example.user.googleauth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by User on 10/08/2017.
 */
data class Usuario(val nome: String,
                   val email: String,
                   val age: String? = null,
                   val cpf: String? = null,
                   val senha: String?){

    //var myRef : DatabaseReference = FirebaseDatabase.getInstance().getReference()



}





