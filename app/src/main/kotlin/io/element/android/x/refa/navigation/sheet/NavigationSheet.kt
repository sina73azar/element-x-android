package com.drp.shared_ui.navigation.sheet

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



fun FragmentActivity.navigationToDestinationSheet(
    navigationPath: String,
    bundle: Bundle? = Bundle(),
    isCancelable: Boolean? = true
) {
    try {
        val clazz =
            Class.forName(navigationPath)
        if (BottomSheetDialogFragment::class.java.isAssignableFrom(clazz)) {
            // Create an instance of the BottomSheetDialogFragment
            val bottomSheetDialogFragment =
                clazz.getDeclaredConstructor().newInstance() as BottomSheetDialogFragment
            bottomSheetDialogFragment.arguments = bundle
            bottomSheetDialogFragment.isCancelable = isCancelable!!
            // Show the BottomSheetDialogFragment
            bottomSheetDialogFragment.show(supportFragmentManager, navigationPath)
        }
    }catch (e: ClassNotFoundException) {
        e.printStackTrace()
    } catch (e: InstantiationException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
    catch (e: ClassCastException) {
        e.printStackTrace()
    }
}