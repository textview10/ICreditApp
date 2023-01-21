package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.loan.ProductResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

/**
 * 申请贷款的界面
 */
class LoanApplyFragment : BaseLoanFragment() {

    private val TAG = "LoanApplyFragment"

    private var spinnerAmount: Spinner? = null
    private var spinnerTerm: Spinner? = null
    private var tvTitle: AppCompatTextView? = null

    private var mAmountList: ArrayList<Pair<String, ArrayList<ProductResponseBean.Product>>> = ArrayList()

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

        if (mAmountList.size == 0){
            getProducts()
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
        val mItem1s = arrayOfNulls<String>(mAmountList.size)
        for (i in 0 until mAmountList.size) {
            mItem1s[i] = mAmountList[i].first
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
                Log.e(TAG, "select pos 1 = " + i)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

    }

    private fun selectPos(pos : Int){
        var pair : Pair<String, ArrayList<ProductResponseBean.Product>> = mAmountList[pos]
        var products : ArrayList<ProductResponseBean.Product> = pair.second

        val mItem2s = arrayOfNulls<String>(products.size)
        for (i in 0 until products.size) {
            mItem2s[i] = products[i].period + "  "
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
                Log.e(TAG, "select pos 2 = " + i)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }
}