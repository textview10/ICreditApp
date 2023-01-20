package com.loan.icreditapp.ui.home.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.dialog.order.OrderInfoBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.loan.LoanApplyFragment
import com.loan.icreditapp.ui.loan.LoanDeclinedFragment
import com.loan.icreditapp.ui.loan.LoanProcessingFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class MyLoanFragment : BaseFragment() {

    private val TAG = "MyLoanFragment"

    private val TYPE_DELAY = 111

    private var pbLoading: ProgressBar? = null

    private val mHandler = Handler(
        Looper.getMainLooper()
    ) { message ->
        when (message.what) {
            TYPE_DELAY -> if (Constant.mLaunchOrderInfo != null) {
                pbLoading?.setVisibility(View.GONE)
                updatePageByStatus(Constant.mLaunchOrderInfo!!)
            } else {
                getOrderInfo()
            }
        }
        false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_myloan, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pbLoading = view.findViewById<ProgressBar>(R.id.pb_loan_loading)
        pbLoading?.setVisibility(View.VISIBLE)
        mHandler.sendEmptyMessageDelayed(TYPE_DELAY, 500)
    }

    private fun getOrderInfo() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //        Log.e(TAG, "111 id = " + Constant.mAccountId);
        OkGo.post<String>(Api.GET_ORDER_INFO).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (pbLoading != null) {
                        pbLoading!!.visibility = View.GONE
                    }
                    if (activity!!.isFinishing || activity!!.isDestroyed) {
                        return
                    }
                    val orderInfo: OrderInfoBean? =
                        checkResponseSuccess(response, OrderInfoBean::class.java)
                    if (orderInfo == null) {
                        Log.e(TAG, " order info error ." + response.body())
                        return
                    }
                    updatePageByStatus(orderInfo)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (pbLoading != null) {
                        pbLoading!!.visibility = View.GONE
                    }
                    if (activity!!.isFinishing || activity!!.isDestroyed) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "order info failure = " + response.body())
                    }
                    ToastUtils.showShort("order info failure")
                }
            })
    }

    private fun updatePageByStatus(orderInfoBean: OrderInfoBean) {
        //可以借款
        if (orderInfoBean.canApply == true || TextUtils.equals(orderInfoBean.orderId, "0")) {
//        if (true){
            val loanApplyFragment = LoanApplyFragment()
            toFragment(loanApplyFragment)
            return
        }
        val status: String? = orderInfoBean.status
        // 1，审核中
        // 2 active 3 paid 4, overdue
        // 5 拒绝
        //"1":approving "2":active "3":paid "4":overdue "5":"declined" "6":"repaying"
        when (status) {
            "1" -> {
                val processingFragment = LoanProcessingFragment()
                processingFragment.setOrderInfo(orderInfoBean)
                toFragment(processingFragment)
            }
            "2", "3", "4" -> {
                val repayDueFragment = LoanApplyFragment()
                repayDueFragment.setOrderInfo(orderInfoBean)
                toFragment(repayDueFragment)
            }
            "5" -> {
                val loanApplyFragment = LoanDeclinedFragment()
                toFragment(loanApplyFragment)
            }
        }
    }

    fun toFragment(fragment: BaseFragment?) {
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(R.id.fl_loan_container, fragment!!)
        transaction.commitAllowingStateLoss()
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}