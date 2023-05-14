package com.loan.icreditapp.ui.profile.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.profile.ContactBean
import com.loan.icreditapp.bean.profile.GetContact2Bean
import com.loan.icreditapp.bean.profile.ModifyContact2Bean
import com.loan.icreditapp.dialog.selectdata.SelectDataDialog
import com.loan.icreditapp.global.ConfigMgr
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.profile.AddProfileActivity
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.ui.profile.widget.SelectContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject


class AddProfile2Fragment : BaseFragment() {
    private val TAG = "AddProfile2Fragment"

    private var editLeagalName1: EditTextContainer? = null
    private var selectMobile1: SelectContainer? = null
    private var selectRelationShip1: SelectContainer? = null

    private var editLeagalName2: EditTextContainer? = null
    private var selectMobile2: SelectContainer? = null
    private var selectRelationShip2: SelectContainer? = null

    private var editLeagalName3: EditTextContainer? = null
    private var selectMobile3: SelectContainer? = null
    private var selectRelationShip3: SelectContainer? = null

    private var editLeagalName4: EditTextContainer? = null
    private var selectMobile4: SelectContainer? = null
    private var selectRelationShip4: SelectContainer? = null

    private var flCommit: FrameLayout? = null

//    private val mContactList: ArrayList<ContactBean> = ArrayList<ContactBean>()
    private var hasUpload: Boolean = false

    //-------------------
    private var leagalName1: String? = null
    private var mobile1: String? = null
    private var relationShip1: Pair<String,String>? = null

    private var leagalName2: String? = null
    private var mobile2: String? = null
    private var relationShip2: Pair<String,String>? = null

    private var leagalName3: String? = null
    private var mobile3: String? = null
    private var relationShip3: Pair<String,String>? = null

    private var leagalName4: String? = null
    private var mobile4: String? = null
    private var relationShip4: Pair<String,String>? = null

    private val TYPE_1 = 1111
    private val TYPE_2 = 1112
    private val TYPE_3 = 1113
    private val TYPE_4 = 1114
    private var startType : Int = TYPE_1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_profile2, container, false)
        return view
    }

    private var mActivityResultLauncher : ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityResultLauncher =  registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val contactUri: Uri? = it.data?.data
            val projection: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            context?.contentResolver?.query(contactUri!!, projection, null, null, null).use { cursor ->
                if (cursor!!.moveToFirst()) {
                    val NUMBER_INDEX = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val DISPLAY_NAME = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val number = cursor.getString(NUMBER_INDEX)
                    val name = cursor.getString(DISPLAY_NAME)
                    Log.e("Test", "number = " + number +" name = " + name)
                    when(startType){
                        (TYPE_1) -> {
                            leagalName1 = name
                            mobile1 = number
                            editLeagalName1?.setEditTextAndSelection(name)
                            selectMobile1?.setData(number)
                        }
                        (TYPE_2) -> {
                            leagalName2 = name
                            mobile2 = number
                            editLeagalName2?.setEditTextAndSelection(name)
                            selectMobile2?.setData(number)
                        }
                        (TYPE_3) -> {
                            leagalName3 = name
                            mobile3 = number
                            editLeagalName3?.setEditTextAndSelection(name)
                            selectMobile3?.setData(number)
                        }
                        (TYPE_4) -> {
                            leagalName4 = name
                            mobile4 = number
                            editLeagalName4?.setEditTextAndSelection(name)
                            selectMobile4?.setData(number)
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editLeagalName1 = view.findViewById(R.id.edit_container_profile2_leagal_name)
        selectMobile1 = view.findViewById(R.id.select_container_profile_mobile)
        selectRelationShip1 = view.findViewById(R.id.select_container_profile_relationship)

        editLeagalName2 = view.findViewById(R.id.edit_container_profile2_leagal_name2)
        selectMobile2 = view.findViewById(R.id.select_container_profile_mobile2)
        selectRelationShip2 = view.findViewById(R.id.select_container_profile_relationship2)

        editLeagalName3 = view.findViewById(R.id.edit_container_profile2_leagal_name3)
        selectMobile3 = view.findViewById(R.id.select_container_profile_mobile3)
        selectRelationShip3 = view.findViewById(R.id.select_container_profile_relationship3)

        editLeagalName4 = view.findViewById(R.id.edit_container_profile2_leagal_name4)
        selectMobile4 = view.findViewById(R.id.select_container_profile_mobile4)
        selectRelationShip4 = view.findViewById(R.id.select_container_profile_relationship4)

        flCommit = view.findViewById(R.id.fl_contact2_commit)

        if (mShowMode){
            editLeagalName1?.setShowMode()
            selectMobile1?.setShowMode()
            selectRelationShip1?.setShowMode()

            editLeagalName2?.setShowMode()
            selectMobile2?.setShowMode()
            selectRelationShip2?.setShowMode()

            editLeagalName3?.setShowMode()
            selectMobile3?.setShowMode()
            selectRelationShip3?.setShowMode()

            editLeagalName4?.setShowMode()
            selectMobile4?.setShowMode()
            selectRelationShip4?.setShowMode()

            flCommit?.visibility = View.GONE
        }

        selectMobile1?.setOnClickListener(View.OnClickListener {
            startContactIntent()
            startType = TYPE_1
        })
        selectMobile2?.setOnClickListener(View.OnClickListener {
            startContactIntent()
            startType = TYPE_2
        })
        selectMobile3?.setOnClickListener(View.OnClickListener {
            startContactIntent()
            startType = TYPE_3
        })
        selectMobile4?.setOnClickListener(View.OnClickListener {
            startContactIntent()
            startType = TYPE_4
        })

        selectRelationShip1?.setOnClickListener(OnClickListener {
            showListDialog(ConfigMgr.mRelationShipList, object : SelectDataDialog.Observer {
                override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                    relationShip1 = content
                    selectRelationShip1?.setData(content?.first)
                }
            })

        })
        selectRelationShip2?.setOnClickListener(OnClickListener {
            showListDialog(ConfigMgr.mRelationShipList, object : SelectDataDialog.Observer {
                override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                    relationShip2 = content
                    selectRelationShip2?.setData(content?.first)
                }
            })
        })
        selectRelationShip3?.setOnClickListener(OnClickListener {
            showListDialog(ConfigMgr.mRelationShipList, object : SelectDataDialog.Observer {
                override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                    relationShip3 = content
                    selectRelationShip3?.setData(content?.first)
                }
            })
        })
        selectRelationShip4?.setOnClickListener(OnClickListener {
            showListDialog(ConfigMgr.mRelationShipList, object : SelectDataDialog.Observer {
                override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                    relationShip4 = content
                    selectRelationShip4?.setData(content?.first)
                }
            })
        })

        flCommit?.setOnClickListener(OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            if (checkContactAvailable()){
                uploadContact2()
            }
        })
        getContact2()
    }

    private fun startContactIntent(){
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        mActivityResultLauncher?.launch(intent)
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

    private fun checkContactAvailable() : Boolean {
        if (TextUtils.isEmpty(leagalName1)){
            ToastUtils.showShort("Please select contact1 name")
            return false
        }
        if (TextUtils.isEmpty(mobile1)){
            ToastUtils.showShort("Please select contact1 mobile")
            return false
        }
        if (relationShip1 == null){
            ToastUtils.showShort("Please select contact1 relationship")
            return false
        }
        if (TextUtils.isEmpty(leagalName2)){
            ToastUtils.showShort("Please select contact2 name")
            return false
        }
        if (TextUtils.isEmpty(mobile2)){
            ToastUtils.showShort("Please select contact2 mobile")
            return false
        }
        if (relationShip2 == null){
            ToastUtils.showShort("Please select contact2 relationship")
            return false
        }
        if (TextUtils.isEmpty(leagalName3)){
            ToastUtils.showShort("Please select contact3 name")
            return false
        }
        if (TextUtils.isEmpty(mobile3)){
            ToastUtils.showShort("Please select contact3 mobile")
            return false
        }
        if (relationShip3 == null){
            ToastUtils.showShort("Please select contact3 relationship")
            return false
        }
        if (TextUtils.isEmpty(leagalName4)){
            ToastUtils.showShort("Please select contact4 name")
            return false
        }
        if (TextUtils.isEmpty(mobile4)){
            ToastUtils.showShort("Please select contact4 mobile")
            return false
        }
        if (relationShip4 == null){
            ToastUtils.showShort("Please select contact4 relationship")
            return false
        }
        return true
    }

    private fun uploadContact2(){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("contact1", leagalName1)
            jsonObject.put("contact1Mobile", mobile1)
            jsonObject.put("contact1Relationship", relationShip1?.second)

            jsonObject.put("contact2", leagalName2)
            jsonObject.put("contact2Mobile", mobile2)
            jsonObject.put("contact2Relationship", relationShip2?.second)

            jsonObject.put("contact3", leagalName3)
            jsonObject.put("contact3Mobile", mobile3)
            jsonObject.put("contact3Relationship", relationShip3?.second)

            jsonObject.put("contact4", leagalName4)
            jsonObject.put("contact4Mobile", mobile4)
            jsonObject.put("contact4Relationship", relationShip4?.second)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.UPLOAD_CONTACT_2).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val modifyContact2Bean: ModifyContact2Bean? =
                        checkResponseSuccess(response, ModifyContact2Bean::class.java)
                    if (modifyContact2Bean == null) {
                        return
                    }
                    if (modifyContact2Bean.hasContact == true){
                        ToastUtils.showShort("modify contact success")
                        FirebaseUtils.logEvent("fireb_data2")
                        if (activity is AddProfileActivity) {
                            var addProfileActivity : AddProfileActivity = activity as AddProfileActivity
                            addProfileActivity.toStep(AddProfileActivity.TO_STEP_3)
                        }
                    } else {
                        ToastUtils.showShort("get contact 2 failure.")
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get contact 2 error.")
                    ToastUtils.showShort("get contact 2 error.")
                }
            })
    }

    private fun getContact2() {
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
        OkGo.post<String>(Api.GET_CONTACT_2).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val getContact2Bean: GetContact2Bean? =
                        checkResponseSuccess(response, GetContact2Bean::class.java)
                    if (getContact2Bean == null) {
                        ToastUtils.showShort("get contact 2 failure.")
                        return
                    }
                    updateData(getContact2Bean)
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get contact 2 error.")
                    ToastUtils.showShort("get contact 2 error.")
                }
            })
    }

    private fun updateData(contact2Bean: GetContact2Bean) {
        if (leagalName1 == null ) {
            leagalName1 = contact2Bean.contact1
        }
        if (mobile1 == null) {
            mobile1 = contact2Bean.contact1Mobile
        }
        if (relationShip1 == null){
            if (!TextUtils.isEmpty(contact2Bean.contact1RelationshipLabel) &&
                    contact2Bean.contact1Relationship != 0) {
                relationShip1 = Pair(contact2Bean.contact1RelationshipLabel,
                    contact2Bean.contact1Relationship.toString())
            }
        }

        if (leagalName2 == null ) {
            leagalName2 = contact2Bean.contact2
        }
        if (mobile2 == null) {
            mobile2 = contact2Bean.contact2Mobile
        }
        if (relationShip2 == null){
            if (!TextUtils.isEmpty(contact2Bean.contact2RelationshipLabel) &&
                contact2Bean.contact2Relationship != 0) {
                relationShip2 = Pair(contact2Bean.contact2RelationshipLabel,
                    contact2Bean.contact2Relationship.toString())
            }
        }

        if (leagalName3 == null ) {
            leagalName3 = contact2Bean.contact3
        }
        if (mobile3 == null) {
            mobile3 = contact2Bean.contact3Mobile
        }
        if (relationShip3 == null){
            if (!TextUtils.isEmpty(contact2Bean.contact3RelationshipLabel) &&
                contact2Bean.contact3Relationship != 0) {
                relationShip3 = Pair(contact2Bean.contact3RelationshipLabel,
                    contact2Bean.contact3Relationship.toString())
            }
        }
        if (leagalName4 == null ) {
            leagalName4 = contact2Bean.contact4
        }
        if (mobile4 == null) {
            mobile4 = contact2Bean.contact4Mobile
        }
        if (relationShip4 == null){
            if (!TextUtils.isEmpty(contact2Bean.contact4RelationshipLabel) &&
                contact2Bean.contact4Relationship != 0) {
                relationShip4 = Pair(contact2Bean.contact4RelationshipLabel,
                    contact2Bean.contact4Relationship.toString())
            }
        }
    }

    private fun bindData() {
        if (!TextUtils.isEmpty(leagalName1)){
            editLeagalName1?.setEditTextAndSelection(leagalName1!!)
        }
        if (!TextUtils.isEmpty(mobile1)){
            selectMobile1?.setData(mobile1!!)
        }
        if (!TextUtils.isEmpty(relationShip1?.first)){
            selectRelationShip1?.setData(relationShip1?.first!!)
        }

        if (!TextUtils.isEmpty(leagalName2)){
            editLeagalName2?.setEditTextAndSelection(leagalName2!!)
        }
        if (!TextUtils.isEmpty(mobile2)){
            selectMobile2?.setData(mobile2!!)
        }
        if (!TextUtils.isEmpty(relationShip2?.first)){
            selectRelationShip2?.setData(relationShip2?.first!!)
        }

        if (!TextUtils.isEmpty(leagalName3)){
            editLeagalName3?.setEditTextAndSelection(leagalName3!!)
        }
        if (!TextUtils.isEmpty(mobile3)){
            selectMobile3?.setData(mobile3!!)
        }
        if (!TextUtils.isEmpty(relationShip3?.first)){
            selectRelationShip3?.setData(relationShip3?.first!!)
        }

        if (!TextUtils.isEmpty(leagalName4)){
            editLeagalName4?.setEditTextAndSelection(leagalName4!!)
        }
        if (!TextUtils.isEmpty(mobile4)){
            selectMobile4?.setData(mobile4!!)
        }
        if (!TextUtils.isEmpty(relationShip4?.first)){
            selectRelationShip4?.setData(relationShip4?.first!!)
        }
    }

    var mShowMode :Boolean = false
    fun setShowMode(){
        mShowMode = true
    }
}