package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.loan.ProductResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.loan.adapter.LoanApplyAdapter
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoanApplyFragment : BaseLoanFragment()  {

    private val TAG = "LoanApplyFragment"

    private var tvAmount:AppCompatTextView? = null
    private var tvTerm:AppCompatTextView? = null
    private var rvTitle: RecyclerView? = null

    private var mAdapter: LoanApplyAdapter? = null
    private var mList: ArrayList<ProductResponseBean.Product> = ArrayList()

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
        tvAmount = view.findViewById(R.id.tv_loan_apply_amount)
        tvTerm = view.findViewById(R.id.tv_loan_apply_term)
        rvTitle = view.findViewById(R.id.rv_loan_apply_title)

        mAdapter = LoanApplyAdapter(mList)
        rvTitle?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvTitle?.adapter = mAdapter

        getProducts()
    }

    private fun getProducts() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_PRODUCT_LIST).tag(TAG)
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
        mList.clear()
        mList.addAll(products)
        mAdapter?.notifyDataSetChanged()
    }

//    private fun convertData(products: List<ProductResponseBean.Product>): ArrayList<Pair<String, ArrayList<ProductResponseBean.Product>>>? {
//        val mMap: HashMap<String, ArrayList<ProductResponseBean.Product>> = HashMap()
//        for (i in 0 until products.size) {
//            val product: ProductResponseBean.Product = products.get(i)
//            var itemList: ArrayList<ProductResponseBean.Product>
//            if (mMap.containsKey(product.amount)) {
//                itemList = mMap[product.getAmount()]!!
//            } else {
//                itemList = ArrayList<ProductResponseBean.Product>()
//                mMap[product.getAmount()] = itemList
//            }
//            itemList.add(product)
//        }
//        val list: ArrayList<Pair<String, ArrayList<ProductResponseBean.Product>>> =
//            ArrayList<Pair<String, ArrayList<ProductResponseBean.Product>>>()
//        val iterator: Iterator<Map.Entry<String, ArrayList<ProductResponseBean.Product>>> =
//            mMap.entries.iterator()
//        while (iterator.hasNext()) {
//            val (key, value) = iterator.next()
//            val pair: Pair<String, ArrayList<ProductResponseBean.Product>> =
//                Pair<String, ArrayList<ProductResponseBean.Product>>(key, value)
//            list.add(pair)
//        }
//        return list
//    }

}