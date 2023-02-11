package com.loan.icreditapp.ui.pay

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.ui.pay.presenter.*
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus

class PayFragment : BaseFragment() {

    companion object {
        const val TAG = "PayFragment"
    }

    private var orderId: String? = null
    private var amount: String? = null
    private var curPresenter: BasePresenter? = null

    private var flNor : FrameLayout? = null
    private var flPayStack : RelativeLayout? = null
    private var flFlutterware : FrameLayout? = null
    private var flRedocly : FrameLayout? = null
    private var flMonify : FrameLayout? = null
    private var flLoading : FrameLayout? = null

    private var norPresenter : NorLoanPresenter? = null
    private var payStackPresenter : PayStackPresenter? = null
    private var flutterwarePresenter : FlutterwarePresenter? = null
    private var redoclyPresenter : RedoclyPresenter? = null
    private var monifyPresenter : MonifyPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_pay, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initData()
    }

    private fun initView(view: View) {
        flNor = view.findViewById(R.id.fl_pay_nor)
        flPayStack = view.findViewById(R.id.fl_pay_paystack)
        flFlutterware = view.findViewById(R.id.fl_pay_flutterware)
        flRedocly = view.findViewById(R.id.fl_pay_redocly)
        flMonify= view.findViewById(R.id.fl_pay_monify)
        flLoading= view.findViewById(R.id.fl_pay_loading)

        flNor?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            curPresenter = norPresenter
            startLoading()
        })

        flPayStack?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            curPresenter = payStackPresenter
            startLoading()
        })
        flFlutterware?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            curPresenter = flutterwarePresenter
            startLoading()
        })
        flRedocly?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            curPresenter = redoclyPresenter
            startLoading()
        })
        flMonify?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            curPresenter = monifyPresenter
            startLoading()
        })
    }

    private fun initData() {
        if (norPresenter == null){
            norPresenter = NorLoanPresenter(this)
            norPresenter?.setObserver(MyObserver())
        }
        if (payStackPresenter == null){
            payStackPresenter = PayStackPresenter(this)
            payStackPresenter?.setObserver(MyObserver())
        }
        if (flutterwarePresenter == null){
            flutterwarePresenter = FlutterwarePresenter(this)
            flutterwarePresenter?.setObserver(MyObserver())
        }
        if (redoclyPresenter == null){
            redoclyPresenter = RedoclyPresenter(this)
            redoclyPresenter?.setObserver(MyObserver())
        }
        if (monifyPresenter == null){
            monifyPresenter = MonifyPresenter(this)
            monifyPresenter?.setObserver(MyObserver())
        }
    }

    private fun startLoading(){
        if (curPresenter == null){
            return
        }
        curPresenter?.requestUrl(orderId, amount)
        flLoading?.visibility = View.VISIBLE
    }

    fun setData(orderId: String?, amount: String?) {
        this.orderId = orderId
        this.amount = amount
    }

    fun onWebViewEnd() {
        flLoading?.visibility = View.VISIBLE
        curPresenter?.updateResult()
    }

    private fun toWebViewInternal(url : String){
        flLoading?.visibility = View.GONE
        if (activity is PayActivity) {
            var payActivity = activity as PayActivity
            payActivity.toWebView(url)
        }
    }

   private inner class MyObserver : BasePresenter.Observer {

        override fun toWebView(url: String) {
            toWebViewInternal(url)
        }

       override fun repaySuccess() {
           flLoading?.visibility = View.GONE
           activity?.finish()
           ToastUtils.showShort("repay success")
           EventBus.getDefault().post(UpdateLoanEvent())
       }

       override fun repayFailure(response: Response<String>, needTip: Boolean, desc : String?) {
           flLoading?.visibility = View.GONE
           if (needTip){
               var responseStr  = StringBuffer()
               if (!TextUtils.isEmpty(desc)){
                   responseStr.append(desc)
               }
               if (response != null) {
                   var body = response.body()
                   if (body != null) {
                       responseStr.append(body.toString())
                   }
               }
               ToastUtils.showShort(responseStr.toString())
           }
       }
   }

}