package com.anandusreekumar.cmpe277_1
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.annotations.SerializedName
import com.anandusreekumar.cmpe277_1.ui.theme.CMPE277_1Theme
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@OptIn(ExperimentalMaterial3Api::class)
class Assignment3 : ComponentActivity() {
    private val viewModel = ChatGptViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CMPE277_1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatGptScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ChatGptScreen(viewModel: ChatGptViewModel) {
    val scope = rememberCoroutineScope()
    val request by viewModel.request.collectAsState()
    val response by viewModel.response.collectAsState()
    val isRequestInProgress by viewModel.isRequestInProgress.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Android ChatGPT App", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = request, onValueChange = { viewModel.updateRequest(it) }, label = { Text("Prompt") })
        Row {
            Button(onClick = { scope.launch { viewModel.sendRequest() } }) {
                Text("Send")
            }
            Button(onClick = { viewModel.cancelRequest() }) {
                Text("Cancel")
            }
            Button(onClick = {
                Toast.makeText(this@ChatGptScreen, "Saved in SQL!", Toast.LENGTH_LONG).show()
            }) {
                Text("Save / Audit")
            }
        }
        if (isRequestInProgress) {
            CircularProgressIndicator()
        } else {
            Text(response)
        }
    }
}

class ChatGptViewModel {
    private var job: Job? = null
    private val _request = mutableStateOf("")
    val request: State<String> = _request

    private val _response = mutableStateOf("")
    val response: State<String> = _response

    private val _isRequestInProgress = mutableStateOf(false)
    val isRequestInProgress: State<Boolean> = _isRequestInProgress

    fun updateRequest(newRequest: String) {
        _request.value = newRequest
    }

    fun sendRequest() {
        job = CoroutineScope(Dispatchers.IO).launch {
            _isRequestInProgress.value = true
            try {
                val response = ChatGptService.instance.prompt(RequestPrompt("gpt-3.5-turbo", arrayOf(RequestMessages("user", _request.value)), 0.7)).execute().body()
                withContext(Dispatchers.Main) {
                    _response.value = response?.choices?.firstOrNull()?.message?.content ?: "No response"
                    _isRequestInProgress.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _response.value = "Error: ${e.message}"
                    _isRequestInProgress.value = false
                }
            }
        }
    }

    fun cancelRequest() {
        job?.cancel()
        _isRequestInProgress.value = false
    }
}

object ChatGptService {
    val instance: OpenApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenApiService::class.java)
    }
}

interface OpenApiService {
    @Headers("Authorization: Bearer YOUR_API_KEY") // Replace YOUR_API_KEY with your actual OpenAI API key
    @POST("chat/completions")
    suspend fun prompt(@Body requestPrompt: RequestPrompt): retrofit2.Response<ResponseChatCompletions>
}

data class RequestPrompt(
    val model: String,
    val messages: Array<RequestMessages>,
    val temperature: Double
)

data class RequestMessages(
    val role: String,
    val content: String
)


data class Usage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    val totalTokens: Int
)