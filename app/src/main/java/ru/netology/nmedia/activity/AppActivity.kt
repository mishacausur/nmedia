package ru.netology.nmedia.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import android.Manifest
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val authViewModel by viewModels<AuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(R.menu.auth_menu, menu)
                    authViewModel.data.observe(this@AppActivity) {
                        val isAuth = authViewModel.isAuth

                        menu.setGroupVisible(R.id.authorized, isAuth)
                        menu.setGroupVisible(R.id.unauthorized, !isAuth)
                    }
                }

                override fun onMenuItemSelected(
                    menuItem: MenuItem
                ): Boolean = when (menuItem.itemId) {
                    R.id.signin -> {
                        AppAuth.getInstance().setAuth(5, "x-token")
                        true
                    }
                    R.id.signup -> {
                        AppAuth.getInstance().setAuth(5, "x-token")
                        true
                    }
                    R.id.logout -> {
                        AppAuth.getInstance().unauth()
                        true
                    }
                    else -> false
                }

            }
        )
        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                }
            )
        }
    }

    fun requestNotificationPermission()  {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }
}