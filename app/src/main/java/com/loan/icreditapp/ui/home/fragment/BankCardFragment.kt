package com.loan.icreditapp.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.bank.CardResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.card.CardListAdapter
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class BankCardFragment : BaseFragment() {

    private val TAG = "BankCardFragment"

    private var rvBankList: RecyclerView? = null
    private var mAdapter: CardListAdapter? = null
    private val mBankList = ArrayList<CardResponseBean.Bank>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bank_card, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvBankList = view.findViewById(R.id.rv_card_bank_list)
        mAdapter = CardListAdapter(mBankList)

        getBankList()
    }

    private fun getBankList() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_BANK_LIST).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val bankBean: CardResponseBean? =
                        checkResponseSuccess(response, CardResponseBean::class.java)
                    if (bankBean == null || bankBean.cardlist == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get bank list ." + response.body())
                        }
                        return
                    }
                    mBankList.clear()
                    mBankList.addAll(bankBean.cardlist!!)
                    mAdapter?.notifyDataSetChanged()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get bank list = " + response.body())
                }
            })
    }
}