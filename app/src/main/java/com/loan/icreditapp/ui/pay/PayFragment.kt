package com.loan.icreditapp.ui.pay

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.dialog.order.OrderInfoBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class PayFragment : BaseFragment() {

    companion object {
        const val TAG = "PayFragment"
    }

    private var orderId: String? = null
    private var amount: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_pay, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestMonify()
    }

    private fun requestMonify() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_RESERVED_ACCOUNT).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (isRemoving || isDetached) {
                        return
                    }
                    Log.e(TAG, " response = " + response.body().toString())
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)

                }
            })
    }

    // @Query("orderId") orderId: String?,
//        //    @Query("amount") amount: String?
    fun setData(orderId: String?, amount: String?) {
        this.orderId = orderId
        this.amount = amount
    }
}