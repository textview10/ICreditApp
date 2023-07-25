package com.loan.icreditapp.util

class CardNumUtils {
    companion object {

        fun getCardNumHide(cardNumber: String?): String? {
            val length: Int? = cardNumber?.length
            var cardNumber: String = cardNumber!!
            length?.let {
                if (length > 4) {
                    try {
                        val temp = cardNumber!!.substring(cardNumber.length - 4, cardNumber.length)
                        var prex = StringBuffer()
                        for (i in 0 until (length - 4)) {
                            prex.append("*")
                        }
                        val result = prex.append(temp).toString()
                        val tempResult = StringBuffer()
                        for (i in 0 until result.length) {
                            if (i % 4 == 3) {
                                tempResult.append(result[i]).append(" ")
                            } else {
                                tempResult.append(result[i])
                            }
                        }
                        cardNumber = tempResult.toString()
                    } catch (_: java.lang.Exception) {

                    }
                }
            }
            return cardNumber
        }

        fun getCardNumHide2(cardNumber: String?): String? {
            val length: Int? = cardNumber?.length
            var cardNumber: String = cardNumber!!
            length?.let {
                if (length > 4) {
                    try {
                        val result = cardNumber.toString()
                        val tempResult = StringBuffer()
                        for (i in 0 until result.length) {
//                            if (i >= 8 && i <= 11) {
                            if (i <= 11) {
                                tempResult.append("*")
                            } else {
                                tempResult.append(result[i])
                            }
                        }
                        cardNumber = tempResult.toString()
                    } catch (_: java.lang.Exception) {

                    }
                }
            }
            return cardNumber
        }
    }
}