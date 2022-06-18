package com.abdulaziz.movieplus.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.abdulaziz.movieplus.R
import com.abdulaziz.movieplus.databinding.ActivityMainBinding
import com.abdulaziz.movieplus.ui.base.BaseFragment
import com.abdulaziz.movieplus.ui.profile.ProfileFragment
import com.abdulaziz.movieplus.ui.saved.SavedFragment

private const val BASE_TAG = "base"
private const val SAVE_TAG = "save"
private const val PROFILE_TAG = "profile"

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpSmoothBar()
    }

    private fun setUpSmoothBar() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BaseFragment(), "base").commit()

        binding.bottomBar.setOnItemSelectedListener {
            val fragment: Fragment?
            var tag = ""
            when (it) {
                    0 -> {
                        fragment = BaseFragment()
                        tag = BASE_TAG
                    }
                    1 -> {
                        fragment = SavedFragment()
                        tag = SAVE_TAG
                    }
                    2 -> {
                        fragment = ProfileFragment()
                        tag = PROFILE_TAG
                    }
                    else -> {
                        fragment =  BaseFragment()
                        tag = BASE_TAG
                    }
                }
            val baseFragment = supportFragmentManager.findFragmentByTag(BASE_TAG)
            if (baseFragment != null && baseFragment.isVisible) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .addToBackStack(tag).commit()
            } else {
                if (it == 0) onBackPressed()
                else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment, tag).commit()
                }
            }
        }
    }

    private fun getVisibleFragmentTag(): String {
        val baseFragment = supportFragmentManager.findFragmentByTag(BASE_TAG)
        val savedFragment = supportFragmentManager.findFragmentByTag(SAVE_TAG)
        val profileFragment = supportFragmentManager.findFragmentByTag(PROFILE_TAG)

        if (baseFragment!=null && baseFragment.isVisible) return BASE_TAG
        else if (savedFragment!=null && savedFragment.isVisible) return SAVE_TAG
        else if (profileFragment!=null && profileFragment.isVisible) return PROFILE_TAG
        else return ""
    }

    override fun hideBottomBar() {
        binding.bottomBar.visibility = View.GONE
    }

    override fun showBottomBar() {
        binding.bottomBar.visibility = View.VISIBLE
    }

    override fun backPressed() {
        onBackPressed()
    }

    override fun onBackPressed() {
        val fragmentTag = getVisibleFragmentTag()
        when (fragmentTag) {
            BASE_TAG -> {
                finish()
            }
            SAVE_TAG -> {
                super.onBackPressed()
                binding.bottomBar.itemActiveIndex = 0
            }
            PROFILE_TAG -> {
                super.onBackPressed()
                binding.bottomBar.itemActiveIndex = 0
            }
            else -> super.onBackPressed()
        }
    }


}