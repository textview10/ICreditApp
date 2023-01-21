package com.loan.icreditapp.ui.card.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.global.ConfigMgr
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import java.text.SimpleDateFormat

class AddBankNum2Fragment : BaseFragment() {

    private var editBankNum: EditTextContainer? = null
    private var flChooseDate: FrameLayout? = null
    private var tvChooseDate: AppCompatTextView? = null
    private var etCvv: AppCompatEditText? = null

    private var flCommit: FrameLayout? = null

    private var expireDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_banknum, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editBankNum = view.findViewById(R.id.edit_container_add_bank_card_banknum)
        flChooseDate = view.findViewById(R.id.fl_add_bank_card_choose_date)
        tvChooseDate = view.findViewById(R.id.tv_add_bank_card_choose_date)
        etCvv = view.findViewById(R.id.et_add_bank_card_cvv)
        flCommit = view.findViewById(R.id.fl_add_bank_num_commit)

        var pair: Pair<ArrayList<String>, ArrayList<ArrayList<String>>> = ConfigMgr.getMouthList()
        mouthList.clear()
        mouthList.addAll(pair.first)
        dateList.clear()
        dateList.addAll(pair.second)

        flChooseDate?.setOnClickListener(OnClickListener {
            showTimePicker { date, v ->
                val sdf = SimpleDateFormat("MM-dd")
                val datef = sdf.format(date)
                expireDate = datef
                tvChooseDate?.text = expireDate
            }
        })
        flCommit?.setOnClickListener(OnClickListener {

        })
    }

    private fun checkBankNum() : Boolean{
        return false
    }

    private val mouthList = ArrayList<String>()
    private val dateList = ArrayList<ArrayList<String>>()

    fun showTimePicker(listener: OnTimeSelectListener?) {
        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
            KeyboardUtils.hideSoftInput(requireActivity())
        }
        val type = booleanArrayOf(
            false, true, true, false, false, false
        )
        //时间选择器
        val pvTime = TimePickerBuilder(context, listener).setSubmitText("ok")
            .setCancelText("cancel")
            .setType(type).build()
        pvTime.show()
    }
}