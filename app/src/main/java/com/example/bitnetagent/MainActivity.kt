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
import io.aatricks.llmedge.LLMEdge
import io.aatricks.llmedge.model.ModelSpec

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPrompt: EditText
    private lateinit var buttonSend: Button
    private lateinit var textViewResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPrompt = findViewById(R.id.editTextPrompt)
        buttonSend = findViewById(R.id.buttonSend)
        textViewResponse = findViewById(R.id.textViewResponse)

        val edge = LLMEdge.create(applicationContext, lifecycleScope)

        buttonSend.setOnClickListener {
            val prompt = editTextPrompt.text.toString()
            if (prompt.isBlank()) return@setOnClickListener

            editTextPrompt.text.clear()
            textViewResponse.text = "Thinking with Microsoft BitNet..."

            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        edge.text.generate(
                            prompt = prompt,
                            model = ModelSpec.huggingFace(
                                repoId = "microsoft/bitnet-b1.58-2B-4T-gguf",
                                filename = "ggml-model-i2_s.gguf"
                            )
                        )
                    }
                    textViewResponse.text = response
                } catch (e: Exception) {
                    textViewResponse.text = "Error: ${e.message}"
                }
            }
        }
    }
}
