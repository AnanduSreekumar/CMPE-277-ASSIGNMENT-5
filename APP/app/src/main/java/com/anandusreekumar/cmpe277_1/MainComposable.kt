package com.anandusreekumar.cmpe277_1

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(counter: MutableState<Int>, shouldShowDialog: MutableState<Boolean>) {
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            result.data?.getStringExtra("resultData")?.toIntOrNull()?.let {
                counter.value += it
            }
        }
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        ActivityStarterButtons(counter = counter, activityLauncher = activityLauncher, shouldShowDialog = shouldShowDialog)
        if (shouldShowDialog.value) {
            ConfirmationDialog(shouldShowDialog = shouldShowDialog)
        }
    }
}

@Composable
fun ActivityStarterButtons(
    counter: MutableState<Int>,
    activityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    shouldShowDialog: MutableState<Boolean>
) {
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = {
            activityLauncher.launch(Intent(context, MainActivityB::class.java))
        }) {
            Text("Launch Activity B")
        }

        TextButton(onClick = {
            activityLauncher.launch(Intent(context, MainActivityC::class.java))
        }) {
            Text("Launch Activity C")
        }

        TextButton(onClick = {
            shouldShowDialog.value = true
        }) {
            Text("Show Dialog")
        }

        Text("Counter: ${counter.value}")
    }
}

@Composable
fun ConfirmationDialog(shouldShowDialog: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = { shouldShowDialog.value = false },
        title = { Text("Attention") },
        text = { Text("You've triggered a dialog.") },
        confirmButton = {
            TextButton(onClick = { shouldShowDialog.value = false }) {
                Text("OK")
            }
        }
    )
}
