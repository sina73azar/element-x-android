package com.drp.shared_ui.model.receipt

enum class ReceiptType(val value: Int) {

    NORMAL(0),
    AMOUNT(1),
    TRACE(2),
    PIN_CODE(3),
    OWNER_CHEQUE(4),
    RECEIVER_CHEQUE(5),
    CARD(6),
    IBAN(7);

    companion object {

        private val map: HashMap<Int, ReceiptType> = hashMapOf()

        init {
            for (value: ReceiptType in values()) {
                map[value.value] = value
            }
        }

        fun valueOf(value: Int): ReceiptType {
            return map[value]!!
        }
    }
}