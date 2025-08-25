package com.example.aidm

import androidx.activity.ComponentActivity // Or import android.app.Activity, etc.
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp // Required for specifying dimensions like padding and height

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp) // Added .dp for padding
    ) {
        Text(
            text = "Welcome", // Explicitly named the 'text' parameter for clarity
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp)) // Added a little space before the fields

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp)) // Added space between text fields

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp)) // Increased space before the button for better visual separation

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}

