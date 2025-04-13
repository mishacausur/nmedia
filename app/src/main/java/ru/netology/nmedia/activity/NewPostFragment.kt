package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.repository.PostViewModel
import ru.netology.nmedia.utils.StringArgs

class NewPostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        arguments?.textArg?.let(binding.edit::setText)
        binding.edit.requestFocus()
        binding.ok.setOnClickListener {
            val content = binding.edit.text.toString()
            if (content.isNotBlank()) {
                viewModel.save(content)
            }
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArgs
    }
}