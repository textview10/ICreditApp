package com.loan.icreditapp.ui.profile.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.profile.ModifyProfile1Bean
import com.loan.icreditapp.bean.profile.Profile1Bean
import com.loan.icreditapp.global.ConfigMgr
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.login.SignUpActivity
import com.loan.icreditapp.ui.profile.AddProfileActivity
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.ui.profile.widget.GenderCheckBox
import com.loan.icreditapp.ui.profile.widget.SelectContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat

class AddProfile1Fragment : BaseFragment() {

    private val TAG: String = "AddProfile1Fragment"

    private var editFirstName: EditTextContainer? = null
    private var editMiddleName: EditTextContainer? = null
    private var editLastName: EditTextContainer? = null
    private var mCheckBox: GenderCheckBox? = null
    private var selectCalendar: SelectContainer? = null
    private var editBvn: EditTextContainer? = null
    private var editEmail: EditTextContainer? = null
    private var selectAddress: SelectContainer? = null
    private var editStreet: EditTextContainer? = null
    private var editStreetNum: EditTextContainer? = null

    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
    private var genderPos: Int? = 0
    private var bvn: String? = null
    private var email: String? = null
    private var state: String? = null
    private var area: String? = null
    private var homeAddress: String? = null
    private var mBirthday: String? = null

    private var flCommit: FrameLayout? = null
//    private var streetNum :String? = null

    private var hasUpload: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_profile1, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editFirstName = view.findViewById(R.id.select_container_profile_first_name)
        editMiddleName = view.findViewById(R.id.select_container_profile_middle_name)
        editLastName = view.findViewById(R.id.select_container_profile_last_name)
        mCheckBox = view.findViewById(R.id.gender_container_profile3_gender)
        selectCalendar = view.findViewById(R.id.select_container_profile_calendar)
        editBvn = view.findViewById(R.id.select_container_profile_bvn)
        editEmail = view.findViewById(R.id.select_container_profile_email)
        selectAddress = view.findViewById(R.id.select_container_profile_address)
        editStreet = view.findViewById(R.id.select_container_profile_street)
        editStreetNum = view.findViewById(R.id.select_container_profile_street_num)
        flCommit = view.findViewById(R.id.fl_profile1_commit)

        editBvn?.setInputNum()

        selectCalendar?.setOnClickListener(View.OnClickListener {
            showTimePicker { date, v ->
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val datef = sdf.format(date)
                mBirthday = datef
                selectCalendar?.setData(mBirthday)
            }
        })
        selectAddress?.setOnClickListener(View.OnClickListener {
            showAreaPicker()
        })

        flCommit?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            val check = checkProfileParams()
            if (check) {
                uploadProfile1()
            }
        })
        getProfile1()
    }

    private fun uploadProfile1() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("firstName", editFirstName?.text)
            jsonObject.put("middleName", editMiddleName?.text)
            jsonObject.put("lastName", editLastName?.text)
            jsonObject.put("bvn", editBvn?.text)
            jsonObject.put("birthday", mBirthday)
            jsonObject.put("gender", genderPos)
            jsonObject.put("home_state", state)
            jsonObject.put("home_area", area)
            jsonObject.put("home_address", editStreet?.text)
            jsonObject.put("email", editEmail?.text)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, " = " + jsonObject.toString())
        OkGo.post<String>(Api.UPLOAD_PROFILE_1).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val profile1Bean: ModifyProfile1Bean? =
                        checkResponseSuccess(response, ModifyProfile1Bean::class.java)
                    if (profile1Bean == null) {
                        ToastUtils.showShort("modify profile 1 failure.")
                        return
                    }
                    if (profile1Bean.bvnChecked == true) {
                        if (activity is AddProfileActivity) {
                            var addProfileActivity : AddProfileActivity = activity as AddProfileActivity
                            addProfileActivity.toStep(AddProfileActivity.TO_STEP_2)
                        }
                        ToastUtils.showShort("modify profile1 success")
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get profile 1 error.")
                    ToastUtils.showShort("get profile 1 error.")
                }
            })
    }

    private fun getProfile1() {
        if (hasUpload) {
            return
        }
        hasUpload = true
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_PROFILE_1).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val profile1Bean: Profile1Bean? =
                        checkResponseSuccess(response, Profile1Bean::class.java)
                    if (profile1Bean == null) {
                        ToastUtils.showShort("get profile 1 failure.")
                        return
                    }
                    updateData(profile1Bean)
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get profile 1 error.")
                    ToastUtils.showShort("get profile 1 error.")
                }
            })
    }

    private fun updateData(profile1Bean: Profile1Bean) {
        if (TextUtils.isEmpty(firstName)) {
            firstName = profile1Bean.firstName
        }
        if (TextUtils.isEmpty(middleName)) {
            middleName = profile1Bean.middleName
        }
        if (TextUtils.isEmpty(lastName)) {
            lastName = profile1Bean.lastName
        }
        if (genderPos != 1 && genderPos != 2) {
            if (TextUtils.equals(profile1Bean.gender, "1")) {
                genderPos = 1
            } else if (TextUtils.equals(profile1Bean.gender, "2")) {
                genderPos = 2
            }
        }
        if (TextUtils.isEmpty(bvn)) {
            bvn = profile1Bean.bvn
        }
        if (TextUtils.isEmpty(email)) {
            email = profile1Bean.email
        }
        if (TextUtils.isEmpty(state)) {
            state = profile1Bean.homeState
        }
        if (TextUtils.isEmpty(area)) {
            area = profile1Bean.homeArea
        }
        if (TextUtils.isEmpty(homeAddress)) {
            homeAddress = profile1Bean.homeAddress
        }
        if (TextUtils.isEmpty(mBirthday)) {
            mBirthday = profile1Bean.birthday
        }
    }

    private fun bindData() {
        if (!TextUtils.isEmpty(firstName)) {
            editFirstName?.setEditTextAndSelection(firstName)
        }
        if (!TextUtils.isEmpty(middleName)) {
            editMiddleName?.setEditTextAndSelection(middleName)
        }
        if (!TextUtils.isEmpty(lastName)) {
            editLastName?.setEditTextAndSelection(lastName)
        }
        if (genderPos != 0) {
            mCheckBox?.setPos(genderPos!!)
        }
//        private var selectCalendar : SelectContainer? = null
        if (!TextUtils.isEmpty(bvn)) {
            editBvn?.setEditTextAndSelection(bvn)
        }
        if (!TextUtils.isEmpty(email)) {
            editEmail?.setEditTextAndSelection(email)
        }
        if (!TextUtils.isEmpty(area) && !TextUtils.isEmpty(state)) {
            selectAddress?.data = (state + ":" + area)
        }
        if (!TextUtils.isEmpty(homeAddress)) {
            editStreet?.setEditTextAndSelection(homeAddress)
        }
        if (!TextUtils.isEmpty(mBirthday)) {
            selectCalendar?.data = mBirthday
        }
//        if (!TextUtils.isEmpty(profile1Bean.homeAddress)) {
//            selectStreetNum?.setEditTextAndSelection(profile1Bean.homeAddress)
//        }
    }

    private fun checkProfileParams(): Boolean {
        if (editFirstName == null || TextUtils.isEmpty(editFirstName!!.getText())) {
            ToastUtils.showShort("fist name is null")
            return false
        }
//        if (editMiddleName == null || TextUtils.isEmpty(editMiddleName!!.getText())) {
//            ToastUtils.showShort("middle name is null")
//            return false
//        }
        if (mBirthday == null) {
            ToastUtils.showShort("birthday is null")
            return false
        }
        if (editLastName == null || TextUtils.isEmpty(editLastName!!.getText())) {
            ToastUtils.showShort("last name is null")
            return false
        }
        if (TextUtils.isEmpty(state) || TextUtils.isEmpty(area)) {
            ToastUtils.showShort("Residential address is null")
            return false
        }
        if (editBvn == null || TextUtils.isEmpty(editBvn!!.getText())) {
            ToastUtils.showShort("bvn is null")
            return false
        }
        if (editEmail == null || TextUtils.isEmpty(editEmail!!.getText())) {
            ToastUtils.showShort("email is null")
            return false
        }
        if (editStreet == null || TextUtils.isEmpty(editStreet!!.getText())) {
            ToastUtils.showShort("street is null")
            return false
        }
        return true
    }

    private fun showTimePicker(listener: OnTimeSelectListener?) {
        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
            KeyboardUtils.hideSoftInput(requireActivity())
        }
        //时间选择器
        val pvTime = TimePickerBuilder(context, listener).setSubmitText("ok")
            .setCancelText("cancel").build()
        // TODO
//        pvTime.setDate()
        pvTime.show()
    }

    fun showAreaPicker() {
        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
            KeyboardUtils.hideSoftInput(requireActivity())
        }
        initAreaPickerData()
        if (provinceList.size == 0 || stateList.size == 0) {
            ConfigMgr.getAllConfig()
            ToastUtils.showShort("area data error , please wait a monment and try again")
            return
        }
        val pvOptions = OptionsPickerBuilder(context) { options1, options2, options3, v ->
            if (options1 != -1) {
                state = provinceList.get(options1)
            }
            if (options1 != -1 && options2 != -1) {
                area = stateList.get(options1).get(options2)
            }
            if (selectAddress != null) {
                selectAddress?.setData(state + "-" + area)
            }
            Log.i(TAG, " select province = $state select state = $area")
        }
        val view: OptionsPickerView<*> = pvOptions.setSubmitText("ok") //确定按钮文字
            .setCancelText("cancel") //取消按钮文字
            .setTitleText("city picker") //标题
            .setSubCalSize(18) //确定和取消文字大小
            .setTitleSize(20) //标题文字大小
            .setTitleColor(Color.BLACK) //标题文字颜色
            .setSubmitColor(Color.BLUE) //确定按钮文字颜色
            .setCancelColor(Color.BLUE) //取消按钮文字颜色
            .setContentTextSize(18) //滚轮文字大小
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setCyclic(false, false, false) //循环与否
            .isRestoreItem(true) //切换时是否还原，设置默认选中第一项。
            .build<Any>()
        view.setPicker(provinceList as List<Nothing>?, stateList as List<Nothing>?)
        view.setSelectOptions(0, 0, 0)
        view.show()
    }

    private val provinceList = ArrayList<String>()
    private val stateList = ArrayList<ArrayList<String>>()
    private fun initAreaPickerData() {
        provinceList.clear()
        stateList.clear()
        val areaData: HashMap<String, java.util.ArrayList<String>> = ConfigMgr.mAreaMap
        val iterator: Iterator<Map.Entry<String, ArrayList<String>>> = areaData.entries.iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            provinceList.add(key)
            val stateItemList = java.util.ArrayList<String>()
            stateItemList.addAll(value)
            stateList.add(stateItemList)
        }
    }
}