package com.loan.icreditapp.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.bank.CardResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.banklist.BankListActivity
import com.loan.icreditapp.ui.card.BindNewCardActivity
import com.loan.icreditapp.ui.card.CardListAdapter
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class BankCardFragment : BaseFragment() {

    private val TAG = "BankCardFragment"

    private var rvBankList: RecyclerView? = null
    private var llAddCard: LinearLayout? = null
    private var flLoading: FrameLayout? = null
    private var flEmpty: FrameLayout? = null
    private var refreshLayout: SmartRefreshLayout? = null

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
        llAddCard = view.findViewById(R.id.ll_card_bank_add_card)
        flLoading = view.findViewById(R.id.fl_bank_card_loading)
        flEmpty = view.findViewById(R.id.fl_bank_card_empty)
        refreshLayout = view.findViewById(R.id.refresh_bank_card)

        mAdapter = CardListAdapter(mBankList)
        rvBankList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvBankList?.adapter = mAdapter
        llAddCard?.setOnClickListener(OnClickListener {
            if (context == null){
                return@OnClickListener
            }
            BindNewCardActivity.launchAddBankCard(requireContext())

        })

        refreshLayout?.setEnableLoadMore(false)
        refreshLayout?.setEnableRefresh(true)
        refreshLayout?.setOnRefreshListener(OnRefreshListener {
            getBankList()
        })
        refreshLayout?.autoRefresh(300)
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
                    if (isRemoving || isDetached){
                        return
                    }
                    refreshLayout?.finishRefresh()
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
                    if (isRemoving || isDetached){
                        return
                    }
                    flLoading?.visibility = View.GONE
                    refreshLayout?.finishRefresh()
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
}