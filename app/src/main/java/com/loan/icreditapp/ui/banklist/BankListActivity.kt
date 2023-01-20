package com.loan.icreditapp.ui.banklist

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.GsonUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.bank.BankResponseBean
import com.loan.icreditapp.event.BankListEvent
import com.loan.icreditapp.ui.banklist.WaveSideBar.OnSelectIndexItemListener
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class BankListActivity : BaseActivity() {
    private val TAG = "BankListActivity"

    private var rvBankList: RecyclerView? = null
    private var ivBack: ImageView? = null
    private val mBankList = ArrayList<BankResponseBean.Bank>()
    private var mAdapter: BankListAdapter? = null
    private var sideBar: WaveSideBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this, false)
        setContentView(R.layout.activity_bank_list)
        initializeView()
        getBankList()
    }

    private fun initializeView() {
        rvBankList = findViewById(R.id.rv_bank_list)
        ivBack = findViewById(R.id.iv_bank_list_back)
        sideBar = findViewById(R.id.sidebar_bank_list)
        ivBack?.setOnClickListener(View.OnClickListener { finish() })

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvBankList?.layoutManager = manager
        mAdapter = BankListAdapter(mBankList)
        mAdapter?.setOnItemClickListener(object : BankListAdapter.OnItemClickListener {
            override fun onItemClick(bankBean: BankResponseBean.Bank, pos: Int) {
                EventBus.getDefault().post(BankListEvent(bankBean))
                finish()
            }
        })
        rvBankList?.setAdapter(mAdapter)
        sideBar?.setOnSelectIndexItemListener(OnSelectIndexItemListener {
            Log.e(
                TAG,
                " test index ..."
            )
        })
    }

    private fun getBankList() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_BANK_LIST).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val responseBean: BankResponseBean? =
                        checkResponseSuccess(response, BankResponseBean::class.java)
                    if (responseBean == null || responseBean.cardlist == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get bank list ." + response.body())
                        }
                        return
                    }

                    mBankList.clear()
                    mBankList.addAll(responseBean.cardlist!!)

                    mAdapter?.notifyDataSetChanged()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get bank list = " + response.body())
                }
            })
    }

}