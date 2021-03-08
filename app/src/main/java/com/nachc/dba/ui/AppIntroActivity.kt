package com.nachc.dba.ui

import android.Manifest
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroFragment
import com.nachc.dba.R

class AppIntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // don't allow the user to skip the intro as we request required permissions here
        isSkipButtonEnabled = false

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_first))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_second))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_third))

        askForPermissions(
            permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            slideNumber = 2,
            required = false)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        finish()
    }
}