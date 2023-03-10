package com.loan.icreditapp.ui.loan

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.bean.ApplyLoadResponse
import com.loan.icreditapp.bean.loan.CheckLoanResponseBean
import com.loan.icreditapp.bean.loan.ProductResponseBean
import com.loan.icreditapp.bean.loan.TrialResponseBean
import com.loan.icreditapp.collect.CollectDataMgr
import com.loan.icreditapp.data.FirebaseData
import com.loan.icreditapp.dialog.RequestPermissionDialog
import com.loan.icreditapp.dialog.producttrial.ProductTrialDialog
import com.loan.icreditapp.event.BankListEvent
import com.loan.icreditapp.event.UpdateGetOrderEvent
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.card.BindNewCardActivity
import com.loan.icreditapp.ui.loan.adapter.LoanApplyAdapter
import com.loan.icreditapp.ui.profile.AddProfileActivity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject

/**
 * ?????????????????????
 */
class LoanApplyFragment : BaseLoanFragment() {

    private val TAG = "LoanApplyFragment"

    private var spinnerAmount: Spinner? = null
    private var spinnerTerm: Spinner? = null
    private var tvTitle: AppCompatTextView? = null
    private var rvContent: RecyclerView? = null
    private var flCommit: FrameLayout? = null
    private var flLoading: FrameLayout? = null

    private var mAmountList: ArrayList<Pair<String, ArrayList<ProductResponseBean.Product>>> =
        ArrayList()

    private var mTrialList: ArrayList<TrialResponseBean.Trial> = ArrayList<TrialResponseBean.Trial>()
    private var mAdapter: LoanApplyAdapter? = null

    private var mTrialBean: TrialResponseBean? = null
    private var mProduct: ProductResponseBean.Product? = null
    private var scrollView: NestedScrollView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_loan_apply, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerAmount = view.findViewById(R.id.spinner_loan_apply_amount)
        spinnerTerm = view.findViewById(R.id.spinner_loan_apply_term)
        tvTitle = view.findViewById(R.id.tv_loan_apply_title)
        rvContent = view.findViewById(R.id.rv_loan_apply_container)
        flCommit = view.findViewById(R.id.fl_loan_apply_commit)
        flLoading = view.findViewById(R.id.fl_apply_load_loading)
        scrollView = view.findViewById(R.id.sv_load_apply)

        var manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvContent?.layoutManager = manager
        mAdapter = LoanApplyAdapter(mTrialList)
        rvContent?.adapter = mAdapter

        if (mAmountList.size == 0) {
            getProducts()
        } else {
            updateSpinner()
        }

        flCommit?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            requestPermission()
        })
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun getProducts() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_PRODUCTS).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (isRemoving || isDetached) {
                        return
                    }
                    val productBean: ProductResponseBean? =
                        checkResponseSuccess(response, ProductResponseBean::class.java)
                    if (productBean == null || productBean.products?.isEmpty() == true) {
                        return
                    }
                    if (productBean.products != null) {
                        updateProductList(productBean.products!!)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " product list error ." + response.body())
                    }
                    ToastUtils.showShort("product list error")
                }
            })
    }

    private fun updateProductList(products: List<ProductResponseBean.Product>) {
        mAmountList.clear()
        for (i in 0 until products.size) {
            val product = products[i]
            var list: ArrayList<ProductResponseBean.Product>? = null
            for (j in 0 until mAmountList.size) {
                var item: Pair<String, ArrayList<ProductResponseBean.Product>> = mAmountList[j]
                if (item != null && (TextUtils.equals(item.first, product.amount))) {
                    list = item.second
                    break
                }
            }
            if (list == null) {
                list = ArrayList<ProductResponseBean.Product>()
                mAmountList.add(Pair(product.amount, list))
            }
            list.add(product)
        }
        updateSpinner()
    }

    private fun updateSpinner() {
        if (context == null){
            return
        }
        val mItem1s = arrayOfNulls<String>(mAmountList.size)
        for (i in 0 until mAmountList.size) {
            mItem1s[i] = "??? " + mAmountList[i].first + " "
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mItem1s)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.setNotifyOnChange(true)
        spinnerAmount?.adapter = adapter
        spinnerAmount?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                selectPos(i)
//                Log.e(TAG, "select pos 1 = " + i)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

    }

    private fun selectPos(pos: Int) {
        var pair: Pair<String, ArrayList<ProductResponseBean.Product>> = mAmountList[pos]
        var products: ArrayList<ProductResponseBean.Product> = pair.second

        val mItem2s = arrayOfNulls<String>(products.size)
        for (i in 0 until products.size) {
            mItem2s[i] = products[i].period + " days  "
        }
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mItem2s)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setNotifyOnChange(true)
        spinnerTerm?.adapter = adapter2
        spinnerTerm?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                var product = products[i]
                tvTitle?.text = product.prodName
                mProduct = product
                Log.e(TAG, "select pos 2 = " + i)
                if (!TextUtils.isEmpty(product.prodId) && !TextUtils.isEmpty(product.amount)){
                    requestLoanTrial(product.prodId!!, product.amount!!)
                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    private fun requestLoanTrial(proId : String , loanAmount : String) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("prodId", proId)
            jsonObject.put("loanAmount", loanAmount)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.PRODUCT_TRIAL).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    val trialBean: TrialResponseBean? =
                        checkResponseSuccess(response, TrialResponseBean::class.java)
                    if (trialBean == null || trialBean.trial?.size == 0) {
                        return
                    }
                    mTrialBean = trialBean
                    mTrialList.clear()
                    mTrialList.addAll(trialBean.trial!!)
                    mAdapter?.notifyDataSetChanged()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " product list error ." + response.body())
                    }
                    ToastUtils.showShort("product list error")
                }
            })
    }

    private fun requestPermission() {
        val hasPermission = PermissionUtils.isGranted(
//            PermissionConstants.LOCATION,
            PermissionConstants.SMS,
            PermissionConstants.CONTACTS,
//            PermissionConstants.STORAGE,
        )
//        val hasPermissionCallLog = PermissionUtils.isGranted(Manifest.permission.READ_CALL_LOG)
        val hasPermissionReadPhoneState =
            PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)

        //        if (false && hasPermission) {
        if (hasPermission && hasPermissionReadPhoneState) {
            executeGetOrderId()
        } else {
            requestPermissionInternal()
        }
    }

    private fun requestPermissionInternal() {
        val dialog = RequestPermissionDialog(requireContext())
        dialog.setOnItemClickListener(object : RequestPermissionDialog.OnItemClickListener() {
            override fun onClickAgree() {
                PermissionUtils.permission(
//                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_SMS,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        executeGetOrderId()
                    }

                    override fun onDenied() {
                        ToastUtils.showShort("please allow permission for apply order.")
                    }
                }).request()
            }
        })
        dialog.show()
    }

    private fun executeGetOrderId(){
        getOrderId()
    }

    private fun getOrderId(){
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_ORDER_ID).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    flLoading?.visibility = View.GONE
                    val checkLoanBean: CheckLoanResponseBean? =
                        checkResponseSuccess(response, CheckLoanResponseBean::class.java)
                    if (checkLoanBean == null) {
                        return
                    }
                    if (checkLoanBean.hasProfile != true || checkLoanBean.hasContact != true
//                    if (checkLoanBean.hasProfile != true || checkLoanBean.hasContact != true
                        || checkLoanBean.hasOther != true || checkLoanBean.bvnChecked != true
                    ) {
                        var intent: Intent = Intent(context, AddProfileActivity::class.java)
                        context?.startActivity(intent)
                        return
                    }
                    // TODO
                    if (checkLoanBean.accountChecked != true){
                        BindNewCardActivity.launchAddBankAccount(context!!)
                        return
                    }
                    if (TextUtils.isEmpty(checkLoanBean.orderId)) {
                        ToastUtils.showShort("need loan apply orderId")
                        return
                    }
                    FirebaseUtils.logEvent("fireb_apply")
//                    if (checkLoanBean.hasProfile) {
//
//                    }
                    flLoading?.visibility = View.VISIBLE
                    CollectDataMgr.sInstance.collectAuthData(requireContext(),
                        checkLoanBean.orderId!!,
                        object : CollectDataMgr.Observer {
                            override fun success(response: Response<String>?) {
                                flLoading?.visibility = View.GONE
                                showTrialDialog(checkLoanBean.orderId!!)
                            }

                            override fun failure(errorMsg: String?) {
                                flLoading?.visibility = View.GONE
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "failure = " + errorMsg)
                                }
                                ToastUtils.showShort("upload auth information failure." + errorMsg)
                            }
                        })
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (isRemoving || isDetached){
                        return
                    }
                    flLoading?.visibility = View.GONE
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " product list error ." + response.body())
                    }
                    ToastUtils.showShort("product list error")
                }
            })
    }

    private fun showTrialDialog(orderId : String){
        if (!isAdded || isDetached || isRemoving){
            return
        }
        if (activity is BaseActivity){
           if ( (activity as BaseActivity).isDestroyed){
               return
           }
        }
        var trialDialog =  ProductTrialDialog(requireContext(), mTrialBean!!)
        trialDialog.setOnDialogClickListener(object : ProductTrialDialog.OnDialogClickListener() {
            override fun onClickAgree() {
                applyLoad(orderId, trialDialog)
            }
        })
        trialDialog.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEvent(event: UpdateGetOrderEvent) {
        executeGetOrderId()
    }

    private fun applyLoad(orderId : String, trialDialog: ProductTrialDialog) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)  //??????ID
            jsonObject.put("prodId", mProduct?.prodId)   //??????ID
            jsonObject.put("prodName", mProduct?.prodName) //????????????
            jsonObject.put("amount", mProduct?.amount)   //????????????
            jsonObject.put("period", mProduct?.period)   //??????????????????	91???
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.LOAD_APPLY).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    val applyLoadResponse: ApplyLoadResponse? =
                        checkResponseSuccess(response, ApplyLoadResponse::class.java)
                    if (applyLoadResponse == null ) {
                        return
                    }
                    if (!TextUtils.equals(applyLoadResponse.status, "1")){
                        ToastUtils.showShort("apply loan failure.")
                        return
                    }
                    if (trialDialog != null ){
                        trialDialog?.dismiss()
                    }
                    FirebaseUtils.logEvent("fireb_apply_confirm")

                    var data = FirebaseData()
                    data.orderId = orderId
                    data.status = 1
                    SPUtils.getInstance().put(Constant.KEY_FIREBASE_DATA, GsonUtils.toJson(data))
                    ToastUtils.showShort("apply load success")
                    EventBus.getDefault().post(UpdateLoanEvent())
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " apply load error ." + response.body())
                    }
                    ToastUtils.showShort("apply load error")
                }
            })
    }

    private fun checkLoanApplyState(){

    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        CollectDataMgr.sInstance.onDestroy()
        super.onDestroy()
    }
}