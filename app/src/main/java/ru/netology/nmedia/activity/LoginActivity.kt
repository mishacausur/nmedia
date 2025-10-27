package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityLoginBinding
import ru.netology.nmedia.repository.LoginViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            viewModel.login(
                binding.loginEdit.text?.toString().orEmpty(),
                binding.passwordEdit.text?.toString().orEmpty()
            )
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        viewModel.state.observe(this) { state ->
            binding.progress.visibility = if (state.loading) android.view.View.VISIBLE else android.view.View.GONE
            if (state.error != null) {
                Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
            }
            if (state.success) {
                finish()
            }
        }
    }
}


