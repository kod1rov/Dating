package com.devcraft.ratingapp.ui.anim

import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.navigation.ui.R

class Anim {
    fun animForNav(): NavOptions {
        return navOptions {
            anim {
                enter = R.anim.nav_default_enter_anim
                popEnter = R.anim.nav_default_pop_enter_anim
                popExit = R.anim.nav_default_pop_enter_anim
                exit = R.anim.nav_default_exit_anim
            }
        }
    }
}