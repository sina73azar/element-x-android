package com.drp.card_facilities.presentation.card_to_card

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.drp.card_facilities.R
import com.drp.card_facilities.databinding.ActivityCardToCardSuperappBinding
import com.drp.card_facilities.presentation.card_to_card.refahi.inquiry.RefahiCardInquiryFragment
import com.drp.shared_ui.BaseActivity
import com.drp.shared_ui.navigation.fragment.instanceFragment
import com.drp.utils.logger
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class SuperAppCardToCardActivity :
    BaseActivity<ActivityCardToCardSuperappBinding>(ActivityCardToCardSuperappBinding::inflate) {


    var keyId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("HUB_LOGGER").d("onCreate keyId is: $keyId")
        showHubFragmentInquiry()
        binding.toolbar.tvToolbarTitle.text = getString(R.string.inquiry_card_title)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.ivHome.setOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                logger(" ShahkarLoginActivity")
                finish()
            }
        })
    }

    private fun showHubFragmentInquiry() {
        this.instanceFragment(
            RefahiCardInquiryFragment(),
            RefahiCardInquiryFragment.TAG,
            bundle = this.intent.extras ?: Bundle(),
            layoutName = binding.fragmentContainer.id
        )
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val receivedKeyId = intent?.data?.getQueryParameter("keyId")
        Timber.tag("HUB_LOGGER").d("onNewIntent cardToCardActivity keyId : $receivedKeyId")
        if (!receivedKeyId.isNullOrEmpty() && receivedKeyId != "null") {
            this.keyId = receivedKeyId
        }
    }

}