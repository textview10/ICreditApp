package com.loan.icreditapp.ui.profile.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.database.Cursor
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
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.profile.ContactBean
import com.loan.icreditapp.bean.profile.GetContact2Bean
import com.loan.icreditapp.bean.profile.ModifyContact2Bean
import com.loan.icreditapp.dialog.contact.SettingContactAdapter
import com.loan.icreditapp.dialog.custom.CustomDialog
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
    private var flCommit: FrameLayout? = null

    private val mContactList: ArrayList<ContactBean> = ArrayList<ContactBean>()

    private var hasUpload: Boolean = false

    //-------------------
    private var leagalName1: String? = null
    private var mobile1: String? = null
    private var relationShip1: Pair<String,String>? = null

    private var leagalName2: String? = null
    private var mobile2: String? = null
    private var relationShip2: Pair<String,String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_profile2, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editLeagalName1 = view.findViewById(R.id.edit_container_profile2_leagal_name)
        selectMobile1 = view.findViewById(R.id.select_container_profile_mobile)
        selectRelationShip1 = view.findViewById(R.id.select_container_profile_relationship)

        editLeagalName2 = view.findViewById(R.id.edit_container_profile2_leagal_name2)
        selectMobile2 = view.findViewById(R.id.select_container_profile_mobile2)
        selectRelationShip2 = view.findViewById(R.id.select_container_profile_relationship2)


        flCommit = view.findViewById(R.id.fl_contact2_commit)

        if (mShowMode){
            editLeagalName1?.setShowMode()
            selectMobile1?.setShowMode()
            selectRelationShip1?.setShowMode()
            editLeagalName2?.setShowMode()
            selectMobile2?.setShowMode()
            selectRelationShip2?.setShowMode()
            flCommit?.visibility = View.GONE
        }

        selectMobile1?.setOnClickListener(View.OnClickListener {
            showContactDialog(object : OnSelectContactListener {
                override fun onData(contactBean: ContactBean?) {
                    if (contactBean != null) {
                        leagalName1 = contactBean.contactName
                        mobile1 = contactBean.number
                        editLeagalName1?.setEditTextAndSelection(contactBean.contactName!!)
                        selectMobile1?.setData(contactBean.number!!)
                    }
                }
            })
        })
        selectMobile2?.setOnClickListener(View.OnClickListener {
            showContactDialog(object : OnSelectContactListener {
                override fun onData(contactBean: ContactBean?) {
                    if (contactBean != null) {
                        leagalName2 = contactBean.contactName
                        mobile2 = contactBean.number
                        editLeagalName2?.setEditTextAndSelection(contactBean.contactName!!)
                        selectMobile2?.setData(contactBean.number!!)
                    }
                }
            })
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

        flCommit?.setOnClickListener(OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            if (checkContactAvailable()){
                uploadContact2()
            }
        })
        initializePermission()
        getContact2()
    }

    private fun initializePermission() {
        val hasReadContactPermission = PermissionUtils.isGranted(Manifest.permission.READ_CONTACTS)
        if (!hasReadContactPermission) {
            PermissionUtils.permission(PermissionConstants.CONTACTS)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        readContact()
                    }

                    override fun onDenied() {}
                }).request()
        } else {
            readContact()
        }
    }

    @SuppressLint("Range")
    private fun readContact() {
        //调用并获取联系人信息
        var cursor: Cursor? = null
        try {
            cursor = requireActivity().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
            )
            if (cursor != null) {
                var set = HashSet<String>()
                mContactList.clear()
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val photoUri =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    val ringtone =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CUSTOM_RINGTONE))
                    //                    Log.e(TAG, " number = " + number + "  displayName = " + displayName);
                    if (!TextUtils.isEmpty(number) && !set.contains(number)) {
                        set.add(number)
                        mContactList.add(ContactBean(id, number, displayName, photoUri, ringtone))
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e(TAG, " exception = $e")
        } finally {
            cursor?.close()
        }
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

    fun showContactDialog(listener: OnSelectContactListener) {
        val customDialog = CustomDialog(requireContext())
        customDialog.setView(R.layout.dialog_contact)
        val rv: RecyclerView = customDialog.findViewById(R.id.rv_dialog_contact)
        val tv: TextView = customDialog.findViewById(R.id.tv_contact_no_data)
        if (mContactList.size == 0) {
            rv.visibility = View.GONE
            tv.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            tv.visibility = View.GONE
        }
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val contactAdapter = SettingContactAdapter(mContactList)
        contactAdapter.setOnItemClickListener(object : SettingContactAdapter.OnItemClickListener {
            override fun onClick(pos: Int) {
                try {
                    var bean: ContactBean? = mContactList.get(pos)
                    listener?.onData(bean)
                } catch (e: Exception) {
                }
                if (customDialog != null) {
                    customDialog.dismiss()
                }
            }
        })
        rv.adapter = contactAdapter
        customDialog.show()
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

            jsonObject.put("contact3", leagalName1)
            jsonObject.put("contact3Mobile", mobile1)
            jsonObject.put("contact3Relationship", relationShip1?.second)

            jsonObject.put("contact4", leagalName2)
            jsonObject.put("contact4Mobile", mobile2)
            jsonObject.put("contact4Relationship", relationShip2?.second)
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
                        ToastUtils.showShort("get contact 2 failure.")
                        return
                    }
                    if (modifyContact2Bean.hasContact == true){
                        ToastUtils.showShort("modify contact success")
                        FirebaseUtils.logEvent("firebase_data2")
                        if (activity is AddProfileActivity) {
                            var addProfileActivity : AddProfileActivity = activity as AddProfileActivity
                            addProfileActivity.toStep(AddProfileActivity.TO_STEP_3)
                        }
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
    }

    interface OnSelectContactListener {
        fun onData(contactBean: ContactBean?)
    }

    var mShowMode :Boolean = false
    fun setShowMode(){
        mShowMode = true
    }
}