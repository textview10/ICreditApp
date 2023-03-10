package com.loan.icreditapp.ui.banklist

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.bean.bank.BankResponseBean
import com.loan.icreditapp.event.BankListEvent
import com.loan.icreditapp.global.ConfigMgr
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
    private var flLoading: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.theme_color))
        BarUtils.setStatusBarLightMode(this, false)
        setContentView(R.layout.activity_bank_list)
        initializeView()
        if (ConfigMgr.mBankList.size == 0){
            getBankList()
        } else {
            mBankList.clear()
            mBankList.addAll(ConfigMgr.mBankList)
            updateList()
        }
    }

    private fun initializeView() {
        rvBankList = findViewById(R.id.rv_bank_list)
        ivBack = findViewById(R.id.iv_bank_list_back)
        sideBar = findViewById(R.id.sidebar_bank_list)
        flLoading = findViewById(R.id.fl_banklist_loading)
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
                " test index .. = " + it
            )
        })
    }

    private fun getBankList() {
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_BANK_LIST).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed){
                        return
                    }
                    flLoading?.visibility = View.GONE
                    val responseBean: BankResponseBean? =
                        checkResponseSuccess(response, BankResponseBean::class.java)
                    if (responseBean == null || responseBean.banklist == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get bank list ." + response.body())
                        }
                        return
                    }
                    Collections.sort<BankResponseBean.Bank>(responseBean.banklist!!,
                        object : Comparator<BankResponseBean.Bank> {
                           override fun compare(
                                bank1: BankResponseBean.Bank,
                                bank2: BankResponseBean.Bank
                            ): Int {
                                if (TextUtils.isEmpty(bank1.bankName)) {
                                    return -1
                                }
                                if (TextUtils.isEmpty(bank2.bankName)) {
                                    return 1
                                }
                                val c1 : Char = bank1.bankName!![0]
                                val c2 : Char = bank2.bankName!![0]
                                return c1 - c2
                            }
                        })

                    ConfigMgr.mBankList.clear()
                    ConfigMgr.mBankList.addAll(responseBean.banklist!!)
                    mBankList.clear()
                    mBankList.addAll(responseBean.banklist!!)
                    updateList()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(TAG, "get bank list = " + response.body())
                }
            })
    }

    fun updateList(){
        mAdapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}