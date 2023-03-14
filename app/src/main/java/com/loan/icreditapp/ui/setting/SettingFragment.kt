package com.loan.icreditapp.ui.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.setting.SettingBean
import com.loan.icreditapp.collect.CollectDataMgr
import com.loan.icreditapp.dialog.RateUsDialog
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.home.MainActivity
import com.loan.icreditapp.ui.launcher.WelcomeActivity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class SettingFragment : BaseFragment() {

    private val TAG = "SettingFragment"

    private var rvContent: RecyclerView? = null
    private var mAdater: SettingAdapter? = null

    private var mList: ArrayList<SettingBean> = ArrayList()

    private var rateUsDialog : RateUsDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_setting, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvContent = view.findViewById(R.id.rv_setting_content)
        buildSettingList()
        var manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvContent?.layoutManager = manager
        rvContent?.addItemDecoration(SettingItemDecor())

        mAdater = SettingAdapter(mList)
        mAdater?.setOnClickListener(object : SettingAdapter.OnClickListener {
            override fun OnClick(pos: Int, settingBean: SettingBean) {
                when (settingBean.type) {
                    PageType.MY_LOAN, PageType.MY_PROFILE, PageType.CARD,
                    PageType.BANK_ACCOUNT, PageType.MESSAGE, PageType.HELP,
                    PageType.ABOUT , PageType.CONTACT_US , PageType.OFFLINE_REPAY-> {
                        updateMainPage(settingBean.type)
                        closeSlide()
                    }
                    PageType.LOGOUT -> {
                        if (checkClickFast()) {
                            return
                        }
                        logOut()
                        closeSlide()
                    }
                    PageType.TEST_TO_PROFILE -> {
                        test()
                        closeSlide()
                    }
                    PageType.RATE_US -> {
                        activity?.let {
                            showRateUsDialog()
                        }
                        closeSlide()
                    }
                }
            }

        })
        rvContent?.adapter = mAdater
    }

    private fun showRateUsDialog() {
        if (isRemoving || isDetached){
            return
        }
        if (rateUsDialog != null){
            if (rateUsDialog!!.isShowing){
                rateUsDialog!!.dismiss()
            }
        }
        rateUsDialog =  RateUsDialog(requireContext())
        rateUsDialog?.show()
    }

    private fun buildSettingList() {
        mList.clear()
        mList.add(
            SettingBean(
                R.drawable.ic_my_loan,
                R.string.setting_my_loan,
                PageType.MY_LOAN, true
            )
        )
        mList.add(
            SettingBean(
                R.drawable.ic_my_profile,
                R.string.setting_my_profile,
                PageType.MY_PROFILE, true
            )
        )
        if (Constant.SHOW_BIND_CARD) {
            mList.add(SettingBean(R.drawable.ic_card, R.string.setting_card, PageType.CARD, true))
        }
        mList.add(
            SettingBean(
                R.drawable.ic_account,
                R.string.setting_bank_account,
                PageType.BANK_ACCOUNT, true
            )
        )
//        mList.add(
//            SettingBean(
//                R.drawable.ic_message,
//                R.string.setting_message,
//                PageType.MESSAGE,
//                true
//            )
//        )
        mList.add(SettingBean(R.drawable.ic_card, R.string.setting_offline_repay, PageType.OFFLINE_REPAY, true))
        mList.add(SettingBean(R.drawable.ic_help, R.string.setting_contact_us, PageType.CONTACT_US, true))
        mList.add(SettingBean(R.drawable.ic_about, R.string.setting_about, PageType.ABOUT, true))
        mList.add(SettingBean(R.drawable.ic_out, R.string.setting_logout, PageType.LOGOUT))

        mList.add(SettingBean(R.drawable.ic_about, R.string.setting_rate_us, PageType.RATE_US))
        if (BuildConfig.DEBUG && false) {
            mList.add(
                SettingBean(
                    R.drawable.ic_out,
                    R.string.setting_test1,
                    PageType.TEST_TO_PROFILE
                )
            )
        }
    }

    private fun updateMainPage(@PageType type: Int) {
        if (activity is MainActivity) {
            var main: MainActivity = activity as MainActivity
            main.updatePageByType(type)
        }
    }

    private fun closeSlide(){
        if (activity is MainActivity) {
            var main: MainActivity = activity as MainActivity
            main.closeSlide()
        }
    }

    private fun test(){
        CollectDataMgr.sInstance.collectAuthData(requireContext(),
            "230125150200000481",
            object : CollectDataMgr.Observer {
                override fun success(response: Response<String>?) {

                }

                override fun failure(errorMsg: String?) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "failure = " + errorMsg)
                    }
                }
            })
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        if (rateUsDialog != null){
            if (rateUsDialog!!.isShowing){
                rateUsDialog!!.dismiss()
            }
        }
        super.onDestroy()
    }

    private fun logOut() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        OkGo.post<String>(Api.LOGOUT).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    var responseBean: BaseResponseBean? = null
                    try {
                        responseBean = com.alibaba.fastjson.JSONObject.parseObject(
                            response.body().toString(),
                            BaseResponseBean::class.java
                        )
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e
                        }
                    }
                    if (responseBean == null) {
                        return
                    }
                    if (!responseBean.isRequestSuccess()) {
//                        ToastUtils.showShort("request logout failure.")
                        return
                    }
                    ToastUtils.showShort("logout success")
                    Constant.mToken = null
                    Constant.mAccountId = null
                    Constant.mLaunchOrderInfo = null
                    val header = BuildRequestJsonUtils.clearHeaderToken()
                    OkGo.getInstance().addCommonHeaders(header)
                    var intent: Intent = Intent(activity, WelcomeActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_in_left_my, R.anim.slide_out_right_my)
                    activity?.finish()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "logout error .")
                }
            })
    }

    private fun requestMessageList() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        OkGo.post<String>(Api.REQUEST_MESSAGE_LIST).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val baseResponseBean: BaseResponseBean? =
                        checkResponseSuccess(response, BaseResponseBean::class.java)
                    if (baseResponseBean == null) {
//                        ToastUtils.showShort("logout failure.")
                        return
                    }
                    if (!baseResponseBean.isRequestSuccess()) {
//                        ToastUtils.showShort("request logout failure.")
                        return
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "logout error .")
                }
            })
    }

}
