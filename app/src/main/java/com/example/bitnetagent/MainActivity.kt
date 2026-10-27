package com.example.bitnetagent

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.aatricks.llmedge.LLMEdge
import io.github.aatricks.llmedge.model.ModelSpec
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPrompt: EditText
    private lateinit var buttonSend: Button
    private lateinit var textViewResponse: TextView

    private lateinit var edge: LLMEdge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPrompt = findViewById(R.id.editTextPrompt)
        buttonSend = findViewById(R.id.buttonSend)
        textViewResponse = findViewById(R.id.textViewResponse)

        edge = LLMEdge.create(applicationContext, lifecycleScope)

        buttonSend.setOnClickListener {
            val prompt = editTextPrompt.text.toString()
            if (prompt.isBlank()) return@setOnClickListener

            editTextPrompt.text.clear()
            textViewResponse.text = "Thinking..."

            lifecycleScope.launch {
                try {
                    val response = edge.text.generate(
                        prompt = prompt,
                        model = ModelSpec.huggingFace(
                            repoId = "unsloth/Qwen2.5-0.5B-Instruct-GGUF",
                            filename = "qwen2.5-0.5b-instruct-q4_k_m.gguf"
                        )
                    )
                    textViewResponse.text = response
                } catch (e: Exception) {
                    textViewResponse.text = "Error: ${e.message}"
                }
            }
        }
    }
}
