package com.drp.refah.ui.data.enums

enum class AdapterViewType(val value: Int) {

    VIEW_TYPE_NORMAL(0),
    VIEW_TYPE_EMPTY(1),
    VIEW_LIST_ERROR(3),
    VIEW_TYPE_REST_PAYMENT(2);

    companion object {

        private val map: HashMap<Int, AdapterViewType> = hashMapOf()

        init {
            for (value: AdapterViewType in values()) {
                map[value.value] = value
            }
        }

        fun valueOf(value: Int): AdapterViewType? {
            return map[value]
        }
    }
}