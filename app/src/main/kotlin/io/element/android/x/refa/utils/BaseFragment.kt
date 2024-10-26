package com.drp.shared_ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.drp.refah.ui.data.enums.DialogName

import java.util.Locale


open class BaseFragment<VB : ViewBinding>(private val inflate: FragmentInflate<VB>) : Fragment() {
    //    lateinit var user: UserEntity
    private var _binding: VB? = null
    val binding get() = _binding!!
    lateinit var activityResultLauncher:
            ActivityResultLauncher<Intent>
    var requestCode = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (requestCode != -1)
                    activityResult(requestCode, result)
            }
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        ChangeLanguage.wrap(requireContext(), Locale("fa"))
        dataObserver()
        binding.root.setOnTouchListener { _, _ ->
            requireActivity().onUserInteraction()
            true
        }
    }

    fun showNextDialog(name: DialogName) {
        when (name) {
            DialogName.NETWORK_ERROR -> {
                FailNetworkDialog().show(requireActivity().supportFragmentManager, "network_dialog")

            }
        }
    }

    open fun dataObserver() {
        //nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    protected open fun activityResult(requestCode: Int, result: ActivityResult) {
        // getting activity results inside fragments
    }
}

typealias FragmentInflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T