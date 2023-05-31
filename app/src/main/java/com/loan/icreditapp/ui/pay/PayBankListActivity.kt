package com.loan.icreditapp.ui.pay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.bean.bank.CardResponseBean
import com.loan.icreditapp.event.ChooseBankListEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.card.BindNewCardActivity
import com.loan.icreditapp.ui.card.CardListAdapter2
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

class PayBankListActivity : BaseActivity() {

    companion object {
        const val TO_PAYBANK_LIST_RESULT = 117

        fun launchActivityForResult(context: Activity){
            val intent = Intent(context, PayBankListActivity::class.java)
            context.startActivityForResult(intent, TO_PAYBANK_LIST_RESULT)
        }
    }

    private val TAG = "PayBankListActivity"
    private var ivBack : ImageView? = null
    private var rvBankList : RecyclerView? = null

    private val mBankList = ArrayList<CardResponseBean.Bank>()
    private var mAdapter: CardListAdapter2? = null

    private var flLoading: FrameLayout? = null
    private var flEmpty: FrameLayout? = null
    private var flCommit: FrameLayout? = null
    private var llAddCard: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_banklist)
        ivBack = findViewById(R.id.iv_pay_banklist_back)
        ivBack?.setOnClickListener(object :OnClickListener{
            override fun onClick(v: View?) {
                 finish()
            }

        })
        rvBankList = findViewById(R.id.rv_pay_bank_list)
        flLoading = findViewById(R.id.fl_bank_card_loading)
        flEmpty = findViewById(R.id.fl_bank_card_empty)
        flCommit= findViewById(R.id.fl_pay_banklist_commit)
        llAddCard = findViewById(R.id.ll_pay_banklist_add_card)

        rvBankList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = CardListAdapter2(mBankList)
        mAdapter?.setOnItemClickListener(object :CardListAdapter2.OnItemClickListener {
            override fun onItemClick(bank: CardResponseBean.Bank, pos: Int) {
                mAdapter?.curPos = pos
                mAdapter?.notifyDataSetChanged()
            }

        })
        rvBankList?.adapter = mAdapter
        if (Constant.bankList.isEmpty()){
            getBankList()
        } else {
            mBankList.clear()
            mBankList.addAll(Constant.bankList)
        }

        flCommit?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                var event = ChooseBankListEvent()
                event.bankNum = mAdapter?.getBankNum()
                EventBus.getDefault().post(event)
                finish()
            }

        })
        llAddCard?.setOnClickListener(View.OnClickListener {
            BindNewCardActivity.launchAddBankCardForResult(this@PayBankListActivity)
        })
    }

    private fun getBankList() {
        flEmpty?.visibility = View.GONE
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_CARD_LIST).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed){
                        return
                    }
                    flLoading?.visibility = View.GONE
                    val bankBean: CardResponseBean? =
                        checkResponseSuccess(response, CardResponseBean::class.java)
                    if (bankBean == null || bankBean.cardlist == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get bank list ." + response.body())
                        }
                        flEmpty?.visibility = View.VISIBLE
                        return
                    }
                    if (bankBean.cardlist!!.isEmpty()){
                        flEmpty?.visibility = View.VISIBLE
                    } else {
                        flEmpty?.visibility = View.GONE
                        mBankList.clear()
                        mBankList.addAll(bankBean.cardlist!!)
                        mAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed){
                        return
                    }
                    flLoading?.visibility = View.GONE
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " get bank list error =  ." + response.body())
                    }
                }
            })
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BindNewCardActivity.BIND_BINK_CARD_FROM_PAY_BANK){
            setResult(TO_PAYBANK_LIST_RESULT)
            finish()
        }
    }
}