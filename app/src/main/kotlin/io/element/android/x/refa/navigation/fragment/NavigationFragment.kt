package com.drp.shared_ui.navigation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.navigationToDestinationFragment(
    navigationPath: String,
    bundle: Bundle? = Bundle(),
    add: Boolean = false,
    layoutName: Int
) {
    try {
        val fragment =
            Class.forName(navigationPath).getDeclaredConstructor().newInstance() as Fragment
        if (add) {
            supportFragmentManager.findFragmentByTag(navigationPath)?.let {
                supportFragmentManager.beginTransaction().remove(it).commitAllowingStateLoss()
            }
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .add(layoutName, fragment, navigationPath)
                .addToBackStack(navigationPath)
                .commitAllowingStateLoss()
            return
        }
        val myFragment = supportFragmentManager.findFragmentByTag(navigationPath) ?: fragment
        myFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(layoutName, myFragment, navigationPath)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}