package com.example.resqai.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.resqai.viewmodel.SignupViewModel
import com.example.resqai.model.User
import com.example.resqai.viewmodel.LoginViewModel

class UserRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun signupUser(email: String, password: String, role: String, callback: (SignupViewModel.SignupState) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val user = User(uid = firebaseUser.uid, email = email, role = role)
                    db.collection("users").document(firebaseUser.uid)
                        .set(user)
                        .addOnSuccessListener {
                            callback(SignupViewModel.SignupState.Success(firebaseUser.uid, role))
                        }
                        .addOnFailureListener { e ->
                            callback(SignupViewModel.SignupState.Error("Firestore error: ${e.message}"))
                        }
                } else {
                    callback(SignupViewModel.SignupState.Error("Authentication successful but user is null"))
                }
            }
            .addOnFailureListener { e ->
                callback(SignupViewModel.SignupState.Error("Signup failed: ${e.message}"))
            }
    }

    fun loginUser(email: String, password: String, callback: (LoginViewModel.LoginState) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    db.collection("users").document(firebaseUser.uid).get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val user = document.toObject(User::class.java)
                                if (user != null) {
                                    callback(LoginViewModel.LoginState.Success(firebaseUser.uid, user.role))
                                } else {
                                    callback(LoginViewModel.LoginState.Error("User data is null"))
                                }
                            } else {
                                callback(LoginViewModel.LoginState.Error("User data not found in Firestore"))
                            }
                        }
                        .addOnFailureListener { e ->
                            callback(LoginViewModel.LoginState.Error("Firestore error: ${e.message}"))
                        }
                } else {
                    callback(LoginViewModel.LoginState.Error("Authentication successful but user is null"))
                }
            }
            .addOnFailureListener { e ->
                callback(LoginViewModel.LoginState.Error("Login failed: ${e.message}"))
            }
    }
}
