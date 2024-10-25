package com.drp.shared_ui.navigation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

inline fun <reified T : Fragment> FragmentActivity.instanceFragment(
    fragment: T,
    tag: String,
    bundle: Bundle = Bundle(),
    layoutName: Int,
    add: Boolean = false
) {
    if (add) {
        supportFragmentManager.findFragmentByTag(tag)?.let {
            supportFragmentManager.beginTransaction().remove(it).commitAllowingStateLoss()
        }
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .add(layoutName, fragment, tag)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
        return
    }
    val myFragment = supportFragmentManager.findFragmentByTag(tag) ?: fragment
    myFragment.arguments = bundle
    supportFragmentManager.beginTransaction()
        .replace(layoutName, myFragment, tag)
        .addToBackStack(null)
        .commitAllowingStateLoss()

}

inline fun <reified T : Fragment> FragmentActivity.newFragment(
    fragment: T,
    tag: String,
    bundle: Bundle = Bundle(),
    layoutName: Int
) {
    fragment.arguments = bundle
    supportFragmentManager.beginTransaction()
        .replace(layoutName, fragment, tag)
        .addToBackStack(null)
        .commitAllowingStateLoss()

}