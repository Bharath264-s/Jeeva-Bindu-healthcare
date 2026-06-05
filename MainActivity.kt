package com.example.jb

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jb.ui.theme.JBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JBTheme {
                JeevaBinduApp()
            }
        }
    }
}

@Composable
fun JeevaBinduApp() {
    // In-memory list to store donors during the app session
    val donorList = remember { mutableStateListOf<Donor>() }
    var currentScreen by remember { mutableStateOf("Register") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == "Register",
                    onClick = { currentScreen = "Register" },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Register") },
                    label = { Text("Register") }
                )
                NavigationBarItem(
                    selected = currentScreen == "Emergency",
                    onClick = { currentScreen = "Emergency" },
                    icon = { Icon(Icons.Default.Warning, contentDescription = "Emergency") },
                    label = { Text("Emergency") }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (currentScreen == "Register") {
                RegisterScreen(onDonorRegistered = { donorList.add(it) })
            } else {
                EmergencyScreen(donors = donorList)
            }
        }
    }
}

@Composable
fun RegisterScreen(onDonorRegistered: (Donor) -> Unit) {
    var name by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register as a Donor",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32) // Green for donor availability
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = bloodGroup,
            onValueChange = { bloodGroup = it },
            label = { Text("Blood Group (e.g., O+, A-)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && bloodGroup.isNotBlank() && phoneNumber.isNotBlank()) {
                    onDonorRegistered(Donor(name, bloodGroup.uppercase().trim(), phoneNumber))
                    name = ""; bloodGroup = ""; phoneNumber = ""
                    Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Register", color = Color.White)
        }
    }
}

@Composable
fun EmergencyScreen(donors: List<Donor>) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredDonors = donors.filter { 
        it.bloodGroup.equals(searchQuery.trim(), ignoreCase = true) 
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Emergency Finder",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red // Red for emergency section
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Enter Required Blood Group") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isNotEmpty() && filteredDonors.isEmpty()) {
            Text("No matching donors found", color = Color.Gray)
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(filteredDonors) { donor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = donor.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "Blood Group: ${donor.bloodGroup}", color = Color.Red, fontWeight = FontWeight.Bold)
                        Text(text = "Phone: ${donor.phoneNumber}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { 
                                Toast.makeText(context, "Notification sent: I Am Coming!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("I Am Coming", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
