package com.kunal.calendar.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.kunal.calendar.databinding.ActivitySplashBinding
import com.kunal.calendar.ui.base.BaseActivity
import com.kunal.calendar.utils.AppConstants.Remote.USER_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        lifecycleScope.launchWhenCreated {
            val isFirstLaunch = datastoreManager.isFirstLaunch.first()
            if (isFirstLaunch == true) {
                val userId = generateUserId()
                Timber.d("$USER_ID:$userId")
                launch(Dispatchers.IO) {
                    datastoreManager.saveUserId(userId)
                    datastoreManager.setIsFirstLaunch(false)
                }
            }
            delay(2500L)
            val createIntent = MainActivity.createIntent(this@SplashActivity)
            startActivity(createIntent)
            finish()
        }
    }

    private fun generateUserId(): String = (0..1000).random().toString()
}