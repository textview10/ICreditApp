package com.loan.icreditapp.ui.pay

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
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
import com.loan.icreditapp.ui.pay.presenter.*
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.util.CardNumUtils
import com.lzy.okgo.model.Response
import net.entity.bean.FlutterWaveResult
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class PayFragment : BaseFragment() {

    companion object {
        const val TAG = "PayFragment"
    }

    private var orderId: String? = null
    private var amount: String? = null
    private var curPresenter: BasePresenter? = null

    private var flNor: FrameLayout? = null
    private var flPayStack: RelativeLayout? = null
    private var flFlutterware: FrameLayout? = null
    private var flRedocly: FrameLayout? = null
    private var flLoading: FrameLayout? = null
    private var tvCopy: AppCompatTextView? = null

    private var tvMonifyTitle: AppCompatTextView? = null

    private var norPresenter: NorLoanPresenter? = null
    private var payStackPresenter: PayStackPresenter? = null
    private var flutterwarePresenter: FlutterwarePresenter? = null
    private var redoclyPresenter: RedoclyPresenter? = null
    private var monifyPresenter: MonifyPresenter? = null

    private var llMonifyResult: LinearLayout? = null
    private var selectBankName: EditTextContainer? = null
    private var selectBankCode: EditTextContainer? = null
    private var selectAccountName: EditTextContainer? = null
    private var selectAccountNumber: EditTextContainer? = null
    private var tvOfflineTitle: AppCompatTextView? = null
    private var tvNorCardNum: AppCompatTextView? = null
    private var llSelectBank: LinearLayout? = null

    private var llAccountResult: LinearLayout? = null
    private var selectBankName2: EditTextContainer? = null
    private var selectBankCode2: EditTextContainer? = null
    private var selectAccountName2: EditTextContainer? = null
    private var selectAccountNumber2: EditTextContainer? = null
    private var tvCopy2: AppCompatTextView? = null

    private var mBankList: ArrayList<CardResponseBean.Bank> = ArrayList()

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
        initView(view)
        initData()

        curPresenter = monifyPresenter
        llMonifyResult?.visibility = GONE
        startLoading()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        updateBankListInternal()
    }

    private fun initView(view: View) {
        tvOfflineTitle = view.findViewById(R.id.tv_pay_offline_title)
        tvNorCardNum = view.findViewById(R.id.tv_pay_offline_card_num)
        flNor = view.findViewById(R.id.fl_pay_nor)
        flPayStack = view.findViewById(R.id.fl_pay_paystack)
        flFlutterware = view.findViewById(R.id.fl_pay_flutterware)
        flRedocly = view.findViewById(R.id.fl_pay_redocly)
        flLoading = view.findViewById(R.id.fl_pay_loading)
        tvCopy = view.findViewById(R.id.tv_pay_copy_account_num)

        llMonifyResult = view.findViewById(R.id.ll_play_monify_result)
        selectBankName = view.findViewById(R.id.select_container_pay_bank_name)
        selectBankCode = view.findViewById(R.id.select_container_pay_bank_code)
        selectAccountName = view.findViewById(R.id.select_container_pay_account_name)
        selectAccountNumber = view.findViewById(R.id.select_container_pay_account_number)
        llSelectBank = view.findViewById(R.id.ll_pay_offline_select_bank)

        llAccountResult = view.findViewById(R.id.ll_account_result)
        selectBankName2 = view.findViewById(R.id.select_container_pay_bank_name_2)
        selectBankCode2 = view.findViewById(R.id.select_container_pay_bank_code_2)
        selectAccountName2 = view.findViewById(R.id.select_container_pay_account_name_2)
        selectAccountNumber2 = view.findViewById(R.id.select_container_pay_account_number_2)
        tvCopy2 = view.findViewById(R.id.tv_pay_copy_account_num_2)

        tvMonifyTitle = view.findViewById(R.id.tv_monify_title_1)

        selectBankName?.setShowMode()
        selectBankCode?.setShowMode()
        selectAccountName?.setShowMode()
        selectAccountNumber?.setShowMode()

//        flFlutterware?.visibility = GONE

        if (Constant.SHOW_BIND_CARD) {
            tvOfflineTitle?.visibility = View.VISIBLE
            flNor?.visibility = View.VISIBLE
            llSelectBank?.visibility = View.VISIBLE
        }

        flNor?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            curPresenter = norPresenter
            if (TextUtils.isEmpty(norPresenter?.getCurBankNum())) {
                ToastUtils.showShort("no bind bank card.")
                return@OnClickListener
            }
            startLoading()
        })

        flPayStack?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            curPresenter = payStackPresenter
            startLoading()
        })
        flFlutterware?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            curPresenter = flutterwarePresenter
            startLoading()
        })
        flRedocly?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            curPresenter = redoclyPresenter
            startLoading()
        })
        tvCopy?.setOnClickListener(OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            if (monifyPresenter == null) {
                return@OnClickListener
            }
            var text = monifyPresenter!!.getCLipBoardText()
            if (!TextUtils.isEmpty(text)) {
                ClipboardUtils.copyText(text)
                ToastUtils.showShort("Copy " + text + " to clipboard success")
            }
        })

        llSelectBank?.setOnClickListener(OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            if (context == null || activity == null){
                return@OnClickListener
            }
            PayBankListActivity.launchActivityForResult(requireActivity())
        })
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
                    tvNorCardNum?.text = CardNumUtils.getCardNumHide2(bank.cardNumber)
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
        if (flutterwarePresenter == null) {
            flutterwarePresenter = FlutterwarePresenter(this)
            flutterwarePresenter?.setData(orderId, amount)
            flutterwarePresenter?.setObserver(MyObserver())
        }
        if (redoclyPresenter == null) {
            redoclyPresenter = RedoclyPresenter(this)
            redoclyPresenter?.setData(orderId, amount)
            redoclyPresenter?.setObserver(MyObserver())
        }
        if (monifyPresenter == null) {
            monifyPresenter = MonifyPresenter(this)
            monifyPresenter?.setData(orderId, amount)
            monifyPresenter?.setObserver(MyObserver())
        }
        updateBankListInternal()
    }

    private fun startLoading() {
        if (curPresenter == null) {
            return
        }
        curPresenter?.requestUrl(orderId, amount)
        flLoading?.visibility = VISIBLE
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
//        Log.e("Test", "on update bind card.")
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
            sb.append("Flutterwave result error")
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
        flutterwarePresenter?.setFlutterwareBean(bean!!)
        flutterwarePresenter?.updateResult()
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
                    if (!TextUtils.isEmpty(body) && !Constant.isAabBuild()) {
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
            llMonifyResult?.visibility = VISIBLE
            if (TextUtils.equals(bean.reserved, "0")) {
                tvMonifyTitle?.text = "Offline Tranfer:"
            } else {
                tvMonifyTitle?.text = "Virtual Account:"
            }
            if (!TextUtils.isEmpty(bean.bankName)) {
                selectBankName?.setEditTextAndSelection(bean.bankName!!)
            }
            if (!TextUtils.isEmpty(bean.bankCode)) {
                selectBankCode?.setEditTextAndSelection(bean.bankCode!!)
            }
            if (!TextUtils.isEmpty(bean.accountName)) {
                selectAccountName?.setEditTextAndSelection(bean.accountName!!)
            }
            if (!TextUtils.isEmpty(bean.accountNumber)) {
                selectAccountNumber?.setEditTextAndSelection(bean.accountNumber!!)
            }
        }
    }

    private fun showOfflineTransfer(){
        ConfigMgr.getTextInfo(object :ConfigMgr.CallBack3 {
            override fun onGetData(textInfoResponse: TextInfoResponse?) {
                if (textInfoResponse == null){
                    return
                }
                if (!TextUtils.isEmpty(textInfoResponse.accountName)){
                    selectAccountName?.setEditTextAndSelection(textInfoResponse.accountName!!)
                }
                if (!TextUtils.isEmpty(textInfoResponse.accountNumber)){
                    selectAccountNumber?.setEditTextAndSelection(textInfoResponse.accountNumber!!)
                }
                if (!TextUtils.isEmpty(textInfoResponse.bank)){
                    selectBankName?.setEditTextAndSelection(textInfoResponse.bank!!)
                }
            }

        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEvent(event: ChooseBankListEvent) {
        if (!TextUtils.isEmpty(event.bankNum)) {
            norPresenter?.setCurBankNum(event.bankNum)
            tvNorCardNum?.text = CardNumUtils.getCardNumHide2(event.bankNum)
        }
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }
}