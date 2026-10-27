package com.example.bitnetagent

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.github.aatricks.llmedge.LLMEdge
import io.github.aatricks.llmedge.model.ChatSession
import io.github.aatricks.llmedge.model.ModelPresets

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPrompt: EditText
    private lateinit var buttonSend: Button
    private lateinit var textViewResponse: TextView

    private var session: ChatSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPrompt = findViewById(R.id.editTextPrompt)
        buttonSend = findViewById(R.id.buttonSend)
        textViewResponse = findViewById(R.id.textViewResponse)

        // Initialize LLMEdge and load BitNet model
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val engine = LLMEdge.newInstance(
                    context = applicationContext,
                    modelPreset = ModelPresets.bitnetB158_2B4T()
                )
                session = engine.createChatSession()
                withContext(Dispatchers.Main) {
                    textViewResponse.text = "Model loaded. Ready to chat."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textViewResponse.text = "Error loading model: ${e.message}"
                }
            }
        }

        buttonSend.setOnClickListener {
            val prompt = editTextPrompt.text.toString()
            if (prompt.isBlank()) return@setOnClickListener

            editTextPrompt.text.clear()
            textViewResponse.text = "Thinking..."

            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        session?.chat(prompt) ?: "Session not ready"
                    }
                    textViewResponse.text = response
                } catch (e: Exception) {
                    textViewResponse.text = "Error: ${e.message}"
                }
            }
        }
    }
}
