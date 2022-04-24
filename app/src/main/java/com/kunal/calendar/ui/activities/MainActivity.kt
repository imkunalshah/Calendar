package com.kunal.calendar.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationBarView
import com.kunal.calendar.R
import com.kunal.calendar.databinding.ActivityMainBinding
import com.kunal.calendar.ui.base.BaseActivity
import com.kunal.calendar.ui.fragments.CalendarFragment
import com.kunal.calendar.ui.fragments.EventsFragment
import com.kunal.calendar.ui.fragments.QuitAppDialogFragment

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : BaseActivity(), NavigationBarView.OnItemSelectedListener,
    FragmentManager.OnBackStackChangedListener {

    private lateinit var binding: ActivityMainBinding

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        initializeViews()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainer,
                CalendarFragment.newInstance()
            ).commit()
            binding.title.text = resources.getString(R.string.app_name)
        }
    }

    private fun initializeViews() {
        supportFragmentManager.addOnBackStackChangedListener(this)
        binding.bottomNavBar.setOnItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val selectedFragment = getVisibleFragment()
        when (item.itemId) {
            R.id.calendar -> {
                if (selectedFragment is EventsFragment) {
                    val calendarFragment = CalendarFragment.newInstance()
                    binding.title.text = resources.getString(R.string.app_name)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, calendarFragment)
                        .addToBackStack(CalendarFragment.TAG).commit()
                }
                return true
            }
            R.id.events -> {
                if (selectedFragment is CalendarFragment) {
                    val eventsFragment = EventsFragment.newInstance()
                    binding.title.text = resources.getString(R.string.events)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, eventsFragment)
                        .addToBackStack(CalendarFragment.TAG).commit()
                }
                return true
            }
        }
        return false
    }

    override fun onBackStackChanged() {
        val selectedFragment = getVisibleFragment()
        if (selectedFragment is CalendarFragment) {
            binding.bottomNavBar.menu.getItem(0)?.isChecked = true
            binding.title.text = resources.getString(R.string.app_name)
        }
        if (selectedFragment is EventsFragment) {
            binding.bottomNavBar.menu.getItem(1)?.isChecked = true
            binding.title.text = resources.getString(R.string.events)
        }
    }

    private fun getVisibleFragment(): Fragment? {
        val fragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment != null && fragment.isVisible) return fragment
        }
        return null
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (getVisibleFragment() is CalendarFragment) {
                val quitAppDialogFragment = QuitAppDialogFragment.newInstance().apply {
                    isCancelable = false
                }.also {
                    it.onQuitClicked = {
                        finish()
                    }
                }
                quitAppDialogFragment.show(supportFragmentManager, QuitAppDialogFragment.TAG)
            } else {
                supportFragmentManager.popBackStack()
            }
        } else {
            val quitAppDialogFragment = QuitAppDialogFragment.newInstance().apply {
                isCancelable = false
            }.also {
                it.onQuitClicked = {
                    finish()
                }
            }
            quitAppDialogFragment.show(supportFragmentManager, QuitAppDialogFragment.TAG)
        }
    }

}