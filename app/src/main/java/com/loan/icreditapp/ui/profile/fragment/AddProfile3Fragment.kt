package com.loan.icreditapp.ui.profile.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.profile.GetContact2Bean
import com.loan.icreditapp.bean.profile.GetOther3Bean
import com.loan.icreditapp.bean.profile.ModifyOther3Bean
import com.loan.icreditapp.dialog.selectdata.SelectDataDialog
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.global.ConfigMgr
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.ui.profile.widget.SelectContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

class AddProfile3Fragment : BaseFragment() {
    private val TAG = "AddProfile3Fragment"

    private var selectSpouse: SelectContainer? = null
    private var selectDebt: SelectContainer? = null
    private var selectSalary: SelectContainer? = null
    private var editEmployName: EditTextContainer? = null
    private var editEmployAddress: EditTextContainer? = null
    private var selectWorkStatus: SelectContainer? = null
    private var flCommit: FrameLayout? = null

    private var mMarital: Pair<String, String>? = null
    private var mDebt: Pair<String, String>? = null
    private var mSalary: Pair<String, String>? = null
    private var mWorkStatus: Pair<String, String>? = null

    private var employName :String? = null
    private var employAddress :String? = null

    private var hasUpload: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_profile3, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectSpouse = view.findViewById(R.id.select_container_profile_spouse)
        selectDebt = view.findViewById(R.id.select_container_profile_debt)
        selectSalary = view.findViewById(R.id.select_container_profile_salary)
        editEmployName = view.findViewById(R.id.edit_text_profile_employ_name)
        editEmployAddress = view.findViewById(R.id.edit_text_profile_employ_address)
        selectWorkStatus = view.findViewById(R.id.select_container_profile_work_status)
        flCommit = view.findViewById(R.id.fl_profile_other3_commit)

        if (mShowMode){
            selectSpouse?.setShowMode()
            selectDebt?.setShowMode()
            selectSalary?.setShowMode()
            editEmployName?.setShowMode()
            editEmployAddress?.setShowMode()
            selectWorkStatus?.setShowMode()
            flCommit?.visibility = View.GONE
        }

        selectSpouse?.setOnClickListener(View.OnClickListener {
            showListDialog(ConfigMgr.mMaritalList, object : SelectDataDialog.Observer {
                override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                    mMarital = content
                    selectSpouse?.setData(content?.first!!)
                }
            })
        })
        selectDebt?.setOnClickListener(View.OnClickListener {
            showListDialog(ConfigMgr.mDebtList, object : SelectDataDialog.Observer {
                override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                    mDebt = content
                    selectDebt?.setData(content?.first!!)
                }
            })
        })
        selectSalary?.setOnClickListener(View.OnClickListener {
            showListDialog(ConfigMgr.mSalaryList, object : SelectDataDialog.Observer {
                override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                    mSalary = content
                    selectSalary?.setData(content?.first!!)
                }
            })
        })
        selectWorkStatus?.setOnClickListener(View.OnClickListener {
            showListDialog(ConfigMgr.mWorkList, object : SelectDataDialog.Observer {
                override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                    mWorkStatus = content
                    selectWorkStatus?.setData(content?.first!!)
                }
            })
        })

        flCommit?.setOnClickListener {
            if (checkClickFast()){
                return@setOnClickListener
            }
            if (checkOtherAvailable()){
                uploadOther3()
            }
        }
        getOther3()
    }

    private fun showListDialog(
        list: ArrayList<Pair<String, String>>,
        observer: SelectDataDialog.Observer
    ) {
        val dialog = SelectDataDialog(requireContext())
        val tempList = ArrayList<Pair<String, String>>()
        tempList.addAll(list)
        dialog.setList(tempList, observer)
        dialog.show()
    }

    private fun checkOtherAvailable() : Boolean{
        if (mMarital == null){
            ToastUtils.showShort("Please select spouse")
            return false
        }
        if (mWorkStatus == null){
            ToastUtils.showShort("Please select work status")
            return false
        }
        if (mSalary == null){
            ToastUtils.showShort("Please select salary")
            return false
        }
        if (mDebt == null){
            ToastUtils.showShort("Please select debt")
            return false
        }
        if (TextUtils.isEmpty(editEmployName?.getText())){
            ToastUtils.showShort("Please fill employ name")
            return false
        }
        if (TextUtils.isEmpty(editEmployAddress?.getText())){
            ToastUtils.showShort("Please fill employ address")
            return false
        }
        return true
    }

    private fun uploadOther3(){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            //用户唯一标识
            jsonObject.put("accountId", Constant.mAccountId)
            //个人婚姻状况
            jsonObject.put("marital", mMarital?.second)
            //个人工作情况
            jsonObject.put("work", mWorkStatus?.second)
            //薪水区间
            jsonObject.put("salary", mSalary?.second)
            //是否有外债
            jsonObject.put("hasOutstandingLoan", mDebt?.second)
            //工作单位名称
            jsonObject.put("companyName", editEmployName?.getText())
            //工作地址-手填详细地址
            jsonObject.put("companyAddress", editEmployAddress?.getText())
            jsonObject.put("education", "1")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.UPLOAD_OTHER_3).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val modifyOther3: ModifyOther3Bean? =
                        checkResponseSuccess(response, ModifyOther3Bean::class.java)
                    if (modifyOther3 == null) {
//                        ToastUtils.showShort("upload other 3 failure.")
                        return
                    }
                    if (modifyOther3.hasContact != true){
                        ToastUtils.showShort("upload other 3 failure.")
                        return
                    }
                    FirebaseUtils.logEvent("fireb_data3")
                    ToastUtils.showShort("modify other3 success")
                    EventBus.getDefault().post(UpdateLoanEvent())
                    activity?.finish()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "upload other 3 error." + response.body().toString())
                    }
                    ToastUtils.showShort("upload other 3 error.")
                }
            })
    }

    private fun getOther3() {
        if (hasUpload && !Constant.mNeedRefreshProfile) {
            bindData()
            return
        }
        hasUpload = true
        Constant.mNeedRefreshProfile = false
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_OTHER_3).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val getOther3Bean: GetOther3Bean? =
                        checkResponseSuccess(response, GetOther3Bean::class.java)
                    if (getOther3Bean == null) {
                        ToastUtils.showShort("get other 3 failure.")
                        return
                    }
                    updateData(getOther3Bean)
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get other 3 error.")
                    ToastUtils.showShort("get other 3 error.")
                }
            })
    }

    private fun updateData(getOther3Bean: GetOther3Bean) {
        if (getOther3Bean.hasOutstandingLoan != -1) {
            for (i in 0 until ConfigMgr.mDebtList.size) {
                var debtPair: Pair<String, String> = ConfigMgr.mDebtList[i]
                if (debtPair.second == getOther3Bean.hasOutstandingLoan.toString()){
                    mDebt = debtPair
                }
            }
        }
        if (!TextUtils.isEmpty(getOther3Bean.marital)) {
            for (i in 0 until ConfigMgr.mMaritalList.size) {
                var maritalPair: Pair<String, String> = ConfigMgr.mMaritalList[i]
                if (maritalPair.second == getOther3Bean.salary){
                    mMarital = maritalPair
                }
            }
        }
        if (!TextUtils.isEmpty(getOther3Bean.salary)) {
            for (i in 0 until ConfigMgr.mSalaryList.size) {
                var salaryPair: Pair<String, String> = ConfigMgr.mSalaryList[i]
                if (salaryPair.second == getOther3Bean.salary){
                    mSalary = salaryPair
                }
            }
        }
        if (!TextUtils.isEmpty(getOther3Bean.work)) {
            for (i in 0 until ConfigMgr.mWorkList.size) {
                var salaryPair: Pair<String, String> = ConfigMgr.mWorkList[i]
                if (salaryPair.second == getOther3Bean.work){
                    mWorkStatus = salaryPair
                }
            }
        }
        if (TextUtils.isEmpty(employName) && !TextUtils.isEmpty(getOther3Bean.companyName)) {
            employName = getOther3Bean.companyName
        }
        if (TextUtils.isEmpty(employAddress) && !TextUtils.isEmpty(getOther3Bean.companyAddress)) {
            employAddress = getOther3Bean.companyAddress
        }

    }

    private fun bindData() {
        selectDebt?.setData(mDebt?.first)
        selectSalary?.setData(mSalary?.first)
        selectWorkStatus?.setData(mWorkStatus?.first)
        selectSpouse?.setData(mMarital?.first)

        if (!TextUtils.isEmpty(employName)) {
            editEmployName?.setEditTextAndSelection(employName!!)
        }
        if (!TextUtils.isEmpty(employAddress)) {
            editEmployAddress?.setEditTextAndSelection(employAddress!!)
        }
    }

    var mShowMode :Boolean = false
    fun setShowMode(){
        mShowMode = true
    }
}