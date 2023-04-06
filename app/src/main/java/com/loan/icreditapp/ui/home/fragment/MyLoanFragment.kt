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
import androidx.annotation.StringRes
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.dialog.order.OrderInfoBean
import com.loan.icreditapp.event.ToApplyLoanEvent
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.home.MainActivity
import com.loan.icreditapp.ui.loan.*
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject

class MyLoanFragment : BaseFragment() {

    private val TAG = "MyLoanFragment"

    private val TYPE_DELAY = 111

    private var pbLoading: ProgressBar? = null
    private var refreshLayout: SmartRefreshLayout? = null

    private var needRefresh : Boolean = false

    private val mHandler = Handler(
        Looper.getMainLooper()
    ) { message ->
        when (message.what) {
            TYPE_DELAY -> {
                if (Constant.mLaunchOrderInfo != null) {
                    pbLoading?.setVisibility(View.GONE)
                    updatePageByStatus(Constant.mLaunchOrderInfo!!)
                } else {
                    getOrderInfo()
                }
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
        refreshLayout = view.findViewById(R.id.refresh_Layout_my_loan)
        pbLoading?.visibility = View.VISIBLE
        if (needRefresh){
            refreshLayout?.autoRefresh()
            needRefresh = false
        } else {
            mHandler.sendEmptyMessageDelayed(TYPE_DELAY, 500)
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        refreshLayout?.setEnableLoadMore(false)
        refreshLayout?.setEnableRefresh(true)
        refreshLayout?.setOnRefreshListener(OnRefreshListener {
            getOrderInfo()
        })
    }

    private fun getOrderInfo() {
        pbLoading?.visibility = View.VISIBLE
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
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (isDetached || isRemoving || !isAdded){
                        return
                    }
                    pbLoading?.visibility = View.GONE
                    refreshLayout?.finishRefresh()
                    val orderInfo: OrderInfoBean? =
                        checkResponseSuccess(response, OrderInfoBean::class.java)
                    if (orderInfo == null) {
                        Log.e(TAG, " order info error ." + response.body())
                        return
                    }
                    Constant.mLaunchOrderInfo = orderInfo
                    updatePageByStatus(orderInfo)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (isDetached || isRemoving || !isAdded){
                        return
                    }
                    pbLoading?.visibility = View.GONE
                    refreshLayout?.finishRefresh()
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
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
        if (orderInfoBean.canApply == true || TextUtils.isEmpty(orderInfoBean.orderId) ||
            TextUtils.equals(orderInfoBean.orderId, "0")
        ) {
            toLoanApplyFragment()
//            val loanPaidFragment = LoanPaidFragment()
//            loanPaidFragment.setOrderInfo(orderInfoBean)
//            toFragment(loanPaidFragment)
//            setTitleInternal(R.string.my_loan_title_paid)
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
                setTitleInternal(R.string.my_loan_title_processing)
            }
            "2" -> {
                val loanActiveFragment = LoanActiveFragment()
                loanActiveFragment.setOrderInfo(orderInfoBean)
                toFragment(loanActiveFragment)
                setTitleInternal(R.string.my_loan_title_active)
            }
            "3" -> {
                val loanPaidFragment = LoanPaidFragment()
                loanPaidFragment.setOrderInfo(orderInfoBean)
                toFragment(loanPaidFragment)
                setTitleInternal(R.string.my_loan_title_paid)
            }
            "4" -> {
                val loanOverDueFragment = LoanOverDueFragment()
                loanOverDueFragment.setOrderInfo(orderInfoBean)
                toFragment(loanOverDueFragment)
                setTitleInternal(R.string.my_loan_title_overdue)
            }
            "5" -> {
                val loanDeclinedFragment = LoanDeclinedFragment()
                toFragment(loanDeclinedFragment)
                setTitleInternal(R.string.my_loan_title_declined)
            }
            "6" -> {
                val loanRepayingFragment = LoanRepayingFragment()
                toFragment(loanRepayingFragment)
                setTitleInternal(R.string.my_loan_title_repaying)
            }
        }
    }

    fun setTitleInternal(@StringRes titleRes: Int) {
        if (activity is MainActivity) {
            var main: MainActivity = activity as MainActivity
            main.setTitle(titleRes)
        }
    }

    private fun toLoanApplyFragment() {
        val loanApplyFragment = LoanApplyFragment()
        setTitleInternal(R.string.setting_my_loan)
        toFragment(loanApplyFragment)
    }

    fun toFragment(fragment: BaseFragment?) {
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(R.id.fl_loan_container, fragment!!)
        transaction.commitAllowingStateLoss()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEvent(event: UpdateLoanEvent) {
        if (refreshLayout != null){
            if ( !refreshLayout!!.isRefreshing){
                refreshLayout!!.autoRefresh(200)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEvent(event: ToApplyLoanEvent) {
        toLoanApplyFragment()
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        mHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    fun setNeedRefresh() {
        needRefresh = true
    }
}