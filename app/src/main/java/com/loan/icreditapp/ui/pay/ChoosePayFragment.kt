package com.loan.icreditapp.ui.pay

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.TextInfoResponse
import com.loan.icreditapp.bean.bank.CardResponseBean
import com.loan.icreditapp.bean.pay.MonifyResponseBean
import com.loan.icreditapp.event.ChooseBankListEvent
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.global.ConfigMgr
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.adapter.ChoosePayAdapter
import com.loan.icreditapp.ui.pay.adapter.ChoosePayData
import com.loan.icreditapp.ui.pay.presenter.*
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.util.CardNumUtils
import com.lzy.okgo.model.Response
import net.entity.bean.FlutterWaveResult
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChoosePayFragment : BaseFragment() {
    companion object {
        const val TAG = "ChoosePayFragment"
    }

    private var rvPay : RecyclerView? = null
    private var flLoading : FrameLayout? = null
    private var mAdapter : ChoosePayAdapter? = null

    private var orderId: String? = null
    private var amount: String? = null
    private var curPresenter: BasePresenter? = null
    private var mBankList: ArrayList<CardResponseBean.Bank> = ArrayList()
    private var mChooseDataList: ArrayList<ChoosePayData> = ArrayList()

    private var norPresenter: NorLoanPresenter? = null
    private var payStackPresenter: PayStackPresenter? = null
    private var flutterwavePresenter: FlutterwarePresenter? = null
    //    private var redoclyPresenter: RedoclyPresenter? = null
//    private var monifyPresenter: MonifyPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_pay, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPay = view.findViewById(R.id.rv_choose_pay)
        flLoading = view.findViewById(R.id.fl_pay_loading)

        initView()
        initData()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        startLoading()
        updateBankListInternal()
    }

    private fun initView() {
        rvPay?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        mChooseDataList.clear()
        mChooseDataList.addAll(ChoosePayData.buildList(null))

        mAdapter = ChoosePayAdapter(mChooseDataList)
        mAdapter?.setOnItemClickListener(object :ChoosePayAdapter.OnItemClickListener {

            override fun onItemClick(bank: ChoosePayData, pos: Int) {
                when(pos) {
                    (0) -> {
                        curPresenter = norPresenter
                        if (TextUtils.isEmpty(norPresenter?.getCurBankNum())) {
                            PayBankListActivity.launchActivityForResult(requireActivity())
                            return
                        }
                        startLoading()
                    }
                    (1) -> {

                        curPresenter = payStackPresenter
                        startLoading()
                    }
                    (2) -> {
                        curPresenter = flutterwavePresenter
                        startLoading()
                    }
                    (3) -> {

                    }
                }
            }

        })
        rvPay?.adapter = mAdapter
    }

    private fun updateBankListInternal(){
        ConfigMgr.getBankList(object : ConfigMgr.CallBack4 {
            override fun onGetData(bankList: ArrayList<CardResponseBean.Bank>) {
                if (isDestroy()){
                    return
                }
                mBankList.clear()
                mBankList.addAll(bankList)
                if (mBankList.isNotEmpty()) {
                    var bank: CardResponseBean.Bank = mBankList[0]
                    norPresenter?.setCurBankNum(bank.cardNumber)

                    mChooseDataList.clear()
                    val cardNum = CardNumUtils.getCardNumHide2(bank.cardNumber)
                    mChooseDataList.addAll(ChoosePayData.buildList(cardNum))
                }
            }

        })
    }

    private fun initData() {
        if (norPresenter == null) {
            norPresenter = NorLoanPresenter(this)
            norPresenter?.setObserver(MyObserver())
            norPresenter?.setData(orderId, amount)
        }
        if (payStackPresenter == null) {
            payStackPresenter = PayStackPresenter(this)
            payStackPresenter?.setData(orderId, amount)
            payStackPresenter?.setObserver(MyObserver())
        }
        if (flutterwavePresenter == null) {
            flutterwavePresenter = FlutterwarePresenter(this)
            flutterwavePresenter?.setData(orderId, amount)
            flutterwavePresenter?.setObserver(MyObserver())
        }
//        if (redoclyPresenter == null) {
//            redoclyPresenter = RedoclyPresenter(this)
//            redoclyPresenter?.setData(orderId, amount)
//            redoclyPresenter?.setObserver(MyObserver())
//        }
//        if (monifyPresenter == null) {
//            monifyPresenter = MonifyPresenter(this)
//            monifyPresenter?.setData(orderId, amount)
//            monifyPresenter?.setObserver(MyObserver())
//        }
        updateBankListInternal()
    }

    private fun startLoading() {
        if (curPresenter == null) {
            return
        }
        curPresenter?.requestUrl(orderId, amount)
        flLoading?.visibility = View.VISIBLE
    }

    fun setData(orderId: String?, amount: String?) {
        this.orderId = orderId
        this.amount = amount
    }

    fun onWebViewEnd() {
        flLoading?.visibility = View.VISIBLE
        curPresenter?.updateResult()
    }

    fun onUpdateBindCard(){
        Log.e("Test", "on update bind card.")
        Constant.bankList.clear()
        updateBankListInternal()
    }


    fun onFlutterWaveResult(isSuccess: Boolean, bean: FlutterWaveResult?) {
        var statusOk = false
        if (bean != null) {
            statusOk = TextUtils.equals(bean.status, "success")
        }
        if (!isSuccess || !statusOk) {
            var sb = StringBuffer()
            sb.append("Flutterware result error")
            if (bean != null) {
                if (!isSuccess && bean.data != null) {
                    sb.append(bean!!.data!!.vbvrespmessage)
                } else {
                    sb.append(GsonUtils.toJson(bean))
                }
            }
            flLoading?.visibility = View.GONE
            ToastUtils.showShort(sb.toString())
            return
        }
        flLoading?.visibility = View.VISIBLE
        flutterwavePresenter?.setFlutterwareBean(bean!!)
        flutterwavePresenter?.updateResult()
    }

    private fun toWebViewInternal(url: String) {
        flLoading?.visibility = View.GONE
        if (activity is PayActivity) {
            var payActivity = activity as PayActivity
            payActivity.toWebView(url)
        }
    }

    private inner class MyObserver : BasePresenter.Observer {

        override fun toWebView(url: String) {
            toWebViewInternal(url)
        }

        override fun repaySuccess() {
            flLoading?.visibility = View.GONE
            activity?.finish()
            ToastUtils.showShort("repay success")
            EventBus.getDefault().post(UpdateLoanEvent())
        }

        override fun repayFailure(response: Response<String>, needTip: Boolean, desc: String?) {
            flLoading?.visibility = View.GONE
            if (needTip) {
                var responseStr = StringBuffer()
                if (!TextUtils.isEmpty(desc)) {
                    responseStr.append(desc)
                }
                if (response != null) {
                    val body = response.body()
                    if (!TextUtils.isEmpty(body) && !Constant.IS_AAB_BUILD) {
                        responseStr.append(body.toString())
                    }
                    LogSaver.logToFile(
                        "repay failure = " + responseStr.toString() + " body = "
                                + body
                    )
                }
                if (!TextUtils.isEmpty(responseStr.toString())) {
                    ToastUtils.showShort(responseStr.toString())
                }
            }
        }

        override fun showMonifyPage(bean: MonifyResponseBean) {
            flLoading?.visibility = View.GONE
//            llMonifyResult?.visibility = View.VISIBLE
//            if (TextUtils.equals(bean.reserved, "0")) {
//                tvMonifyTitle?.setText("Offline Tranfer:")
//            } else {
//                tvMonifyTitle?.setText("Virtual Account:")
//            }
//            if (!TextUtils.isEmpty(bean.bankName)) {
//                selectBankName?.setEditTextAndSelection(bean.bankName!!)
//            }
//            if (!TextUtils.isEmpty(bean.bankCode)) {
//                selectBankCode?.setEditTextAndSelection(bean.bankCode!!)
//            }
//            if (!TextUtils.isEmpty(bean.accountName)) {
//                selectAccountName?.setEditTextAndSelection(bean.accountName!!)
//            }
//            if (!TextUtils.isEmpty(bean.accountNumber)) {
//                selectAccountNumber?.setEditTextAndSelection(bean.accountNumber!!)
//            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEvent(event: ChooseBankListEvent) {
        if (!TextUtils.isEmpty(event.bankNum)) {
            norPresenter?.setCurBankNum(event.bankNum)
//            tvNorCardNum?.text = CardNumUtils.getCardNumHide2(event.bankNum)
        }
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

}