package com.drp.shared_ui.widget.xml

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.drp.utils.currencyFormatter

class AmountTextVew : AppCompatTextView {

    val amount: Long
        get() = if (this.text != null && this.length() > 0) {
            java.lang.Long.parseLong(text!!.toString().filter { it.isDigit() })
        } else {
            0
        }

    constructor(context: Context) : super(context) {
        if (this.text.toString().any { it.isDigit() })
            this.text = currencyFormatter(
                java.lang.Long.parseLong(
                    this.text.toString().filter { it.isDigit() })
            )

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        if (this.text.toString().filter { it.isDigit() }.isNotEmpty())
            this.text = currencyFormatter(
                java.lang.Long.parseLong(
                    this.text.toString().filter { it.isDigit() })
            )

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (this.text.toString().filter { it.isDigit() }.isNotEmpty())
            this.text = currencyFormatter(
                java.lang.Long.parseLong(
                    this.text.toString().filter { it.isDigit() })
            )
    }
}