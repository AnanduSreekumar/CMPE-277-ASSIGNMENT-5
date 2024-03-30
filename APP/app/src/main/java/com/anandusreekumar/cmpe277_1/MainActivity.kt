package com.anandusreekumar.cmpe277_1

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.anandusreekumar.cmpe277_1.ui.theme.CMPE277_1Theme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }

    @Composable
    fun AppContent() {
        CMPE277_1Theme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AssignmentsList()
            }
        }
    }

    @Composable
    fun AssignmentsList() {
        val context = LocalContext.current
        val assignments = listOf("Assignment - 1", "Assignment - 2", "Assignment - 3")
        val activities = listOf(Assignment1::class.java, Assignment2::class.java, Assignment3::class.java, Assignment5::class.java)

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            assignments.forEachIndexed { index, title ->
                AssignmentButton(title = title) {
                    val intent = Intent(context, activities[index]).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    @Composable
    fun AssignmentButton(title: String, action: () -> Unit) {
        Button(
            onClick = { action() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text(text = title)
        }
    }
}
