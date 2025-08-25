package com.example.aidm

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen() {
    var name by remember { mutableStateOf("User") }
    var phone by remember { mutableStateOf("+91-") }
    var allergies by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(allergies, { allergies = it }, label = { Text("Allergies / Conditions") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = { /* TODO: save secure */ }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
    }
}
