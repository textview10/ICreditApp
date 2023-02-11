package com.loan.icreditapp.ui.card.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.bank.BankAccountResponseBean
import com.loan.icreditapp.bean.bank.BankResponseBean
import com.loan.icreditapp.event.BankListEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.banklist.BankListActivity
import com.loan.icreditapp.ui.card.BindNewCardActivity
import com.loan.icreditapp.ui.profile.AddProfileActivity
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.ui.profile.widget.SelectContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject

class AddBankAccount1Fragment : BaseFragment() {

    private val TAG = "AddBankAccount1Fragment"

    private var selectBankList: SelectContainer? = null
    private var editBankNum: EditTextContainer? = null
    private var flCommit: FrameLayout? = null

    private var mBankData: BankResponseBean.Bank? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_bank_account, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectBankList = view.findViewById(R.id.select_container_add_bank_account_bank_list)
        editBankNum = view.findViewById(R.id.edit_container_add_bank_account_banknum)
        flCommit = view.findViewById(R.id.fl_add_bank_account_commit)

        selectBankList?.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(activity, BankListActivity::class.java)
            startActivity(intent)
        })

        flCommit?.setOnClickListener(OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            if (checkBankAccountAvailable()) {
                uploadBankAccount()
            }
        })
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEvent(event: BankListEvent) {
        mBankData = event.mData
        selectBankList?.setData(event.mData?.bankName)
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    private fun checkBankAccountAvailable() : Boolean{
        if (mBankData == null){
            ToastUtils.showShort("unselect bank")
            return false
        }
        if (editBankNum == null || editBankNum!!.isEmptyText()){
            ToastUtils.showShort("bank account num == null")
            return false
        }
        return true
    }

    private fun uploadBankAccount() {
//        bank account  1408518986
//        Access bank Idise Betty   2284463522
//         Zenith bank 李俊杰  2284462518
//                 Zenith bank 刘正
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            //客户ID
            jsonObject.put("accountId", Constant.mAccountId)
            //银行简码
            jsonObject.put("bankCode", mBankData?.bankCode)
            //银行名称
            jsonObject.put("bankName", mBankData?.bankName)
            //客户填写的银行账号
            jsonObject.put("bankAccountNumber", editBankNum?.getText())
//            if (BuildConfig.DEBUG) {
//                jsonObject.put("bankAccountNumber", "2284462518")
//            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.UPLOAD_BANK_ACCOUNT).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val responseBean: BankAccountResponseBean? =
                        checkResponseSuccess(response, BankAccountResponseBean::class.java)
                    if (responseBean == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " upload bank account ." + response.body())
                        }
                        return
                    }
                    if (responseBean.bankAccountChecked != true) {
                        ToastUtils.showShort("check bank account failure ")
                        return
                    }
                    FirebaseUtils.logEvent("firebase_bank")
                    if (activity is BindNewCardActivity) {
                        var bindNewCardActivity : BindNewCardActivity = activity as BindNewCardActivity
                        bindNewCardActivity.toStep(BindNewCardActivity.SUCCESS)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get bank list = " + response.body())
                }
            })
    }

    override fun onDetach() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDetach()
    }
}