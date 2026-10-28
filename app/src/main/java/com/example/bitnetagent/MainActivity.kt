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
import io.aatricks.llmedge.EngineConfig
import io.aatricks.llmedge.LLMEdge
import io.aatricks.llmedge.model.ModelPresets

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPrompt: EditText
    private lateinit var buttonSend: Button
    private lateinit var textViewResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- CRASH HANDLER ADDED HERE ---
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runOnUiThread {
                textViewResponse.text = "CRASH DETECTED:\n\n${throwable.message}\n\n${throwable.stackTraceToString().take(1000)}"
            }
            throwable.printStackTrace()
        }
        // --------------------------------

        editTextPrompt = findViewById(R.id.editTextPrompt)
        buttonSend = findViewById(R.id.buttonSend)
        textViewResponse = findViewById(R.id.textViewResponse)

        val edge = LLMEdge.create(
            context = applicationContext,
            scope = lifecycleScope,
            engineConfig = EngineConfig(
                extraArgs = listOf("--override-kv", "bitnet-b1.58.hidden_activation=str:relu2")
            )
        )

        buttonSend.setOnClickListener {
            val prompt = editTextPrompt.text.toString()
            if (prompt.isBlank()) return@setOnClickListener

            editTextPrompt.text.clear()
            textViewResponse.text = "Downloading/Loading Model... (Please wait)"

            lifecycleScope.launch {
                try {
                    textViewResponse.text = "Thinking..."
                    val response = withContext(Dispatchers.IO) {
                        edge.text.generate(
                            prompt = prompt,
                            model = ModelPresets.bitnet  // Use the built-in BitNet preset
                        )
                    }
                    textViewResponse.text = response
                } catch (e: Exception) {
                    textViewResponse.text = "CAUGHT ERROR:\n\n${e.message}\n\n${e.stackTraceToString().take(1000)}"
                }
            }
        }
    }
}
