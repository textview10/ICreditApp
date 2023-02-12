package net.entity.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class FlutterWaveResult : Serializable {
    var status: String? = null
    var message: String? = null
    var data: DataBean? = null

    class DataBean {
        var id = 0
        var txRef: String? = null
        var orderRef: String? = null
        var flwRef: String? = null
        var redirectUrl: String? = null
        var device_fingerprint: String? = null
        var settlement_token: Any? = null
        var cycle: String? = null
        var amount = 0
        var charged_amount = 0
        var appfee = 0.0
        var merchantfee = 0
        var merchantbearsfee = 0
        var chargeResponseCode: String? = null
        var raveRef: String? = null
        var chargeResponseMessage: String? = null
        var authModelUsed: String? = null
        var currency: String? = null

        @SerializedName("IP")
        var iP: String? = null
        var narration: String? = null
        var status: String? = null
        var modalauditid: String? = null
        var vbvrespmessage: String? = null
        var authurl: String? = null
        var vbvrespcode: String? = null
        var acctvalrespmsg: Any? = null
        var acctvalrespcode: String? = null
        var paymentType: String? = null
        var paymentPlan: Any? = null
        var paymentPage: Any? = null
        var paymentId: String? = null
        var fraud_status: String? = null
        var charge_type: String? = null
        var is_live = 0
        var createdAt: String? = null
        var updatedAt: String? = null
        var deletedAt: Any? = null
        var customerId = 0

        @SerializedName("AccountId")
        var accountId = 0

        @SerializedName("customer.id")
        private var `_$CustomerId243` // FIXME check this code
                = 0

        @SerializedName("customer.phone")
        private var `_$CustomerPhone131` // FIXME check this code
                : Any? = null

        @SerializedName("customer.fullName")
        private var `_$CustomerFullName200` // FIXME check this code
                : String? = null

        @SerializedName("customer.customertoken")
        private var `_$CustomerCustomertoken267` // FIXME check this code
                : Any? = null

        @SerializedName("customer.email")
        private var `_$CustomerEmail308` // FIXME check this code
                : String? = null

        @SerializedName("customer.createdAt")
        private var `_$CustomerCreatedAt306` // FIXME check this code
                : String? = null

        @SerializedName("customer.updatedAt")
        private var `_$CustomerUpdatedAt242` // FIXME check this code
                : String? = null

        @SerializedName("customer.deletedAt")
        private var `_$CustomerDeletedAt274` // FIXME check this code
                : Any? = null

        @SerializedName("customer.AccountId")
        private var `_$CustomerAccountId242` // FIXME check this code
                = 0
        var meta: List<*>? = null
        var flwMeta: FlwMetaBean? = null

        fun `get_$CustomerId243`(): Int {
            return `_$CustomerId243`
        }

        fun `set_$CustomerId243`(`_$CustomerId243`: Int) {
            this.`_$CustomerId243` = `_$CustomerId243`
        }

        fun `get_$CustomerPhone131`(): Any? {
            return `_$CustomerPhone131`
        }

        fun `set_$CustomerPhone131`(`_$CustomerPhone131`: Any?) {
            this.`_$CustomerPhone131` = `_$CustomerPhone131`
        }

        fun `get_$CustomerFullName200`(): String? {
            return `_$CustomerFullName200`
        }

        fun `set_$CustomerFullName200`(`_$CustomerFullName200`: String?) {
            this.`_$CustomerFullName200` = `_$CustomerFullName200`
        }

        fun `get_$CustomerCustomertoken267`(): Any? {
            return `_$CustomerCustomertoken267`
        }

        fun `set_$CustomerCustomertoken267`(`_$CustomerCustomertoken267`: Any?) {
            this.`_$CustomerCustomertoken267` = `_$CustomerCustomertoken267`
        }

        fun `get_$CustomerEmail308`(): String? {
            return `_$CustomerEmail308`
        }

        fun `set_$CustomerEmail308`(`_$CustomerEmail308`: String?) {
            this.`_$CustomerEmail308` = `_$CustomerEmail308`
        }

        fun `get_$CustomerCreatedAt306`(): String? {
            return `_$CustomerCreatedAt306`
        }

        fun `set_$CustomerCreatedAt306`(`_$CustomerCreatedAt306`: String?) {
            this.`_$CustomerCreatedAt306` = `_$CustomerCreatedAt306`
        }

        fun `get_$CustomerUpdatedAt242`(): String? {
            return `_$CustomerUpdatedAt242`
        }

        fun `set_$CustomerUpdatedAt242`(`_$CustomerUpdatedAt242`: String?) {
            this.`_$CustomerUpdatedAt242` = `_$CustomerUpdatedAt242`
        }

        fun `get_$CustomerDeletedAt274`(): Any? {
            return `_$CustomerDeletedAt274`
        }

        fun `set_$CustomerDeletedAt274`(`_$CustomerDeletedAt274`: Any?) {
            this.`_$CustomerDeletedAt274` = `_$CustomerDeletedAt274`
        }

        fun `get_$CustomerAccountId242`(): Int {
            return `_$CustomerAccountId242`
        }

        fun `set_$CustomerAccountId242`(`_$CustomerAccountId242`: Int) {
            this.`_$CustomerAccountId242` = `_$CustomerAccountId242`
        }

        class FlwMetaBean
    }
}
