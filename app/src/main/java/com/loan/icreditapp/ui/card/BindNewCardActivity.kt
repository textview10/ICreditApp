package com.loan.icreditapp.ui.card

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.ui.card.fragment.AddBankAccount1Fragment
import com.loan.icreditapp.ui.card.fragment.AddBankNum2Fragment
import com.loan.icreditapp.ui.profile.AddProfileActivity
import com.loan.icreditapp.ui.profile.fragment.AddProfile2Fragment
import com.loan.icreditapp.ui.profile.fragment.AddProfile3Fragment

class BindNewCardActivity : BaseActivity() {

    companion object {
        //添加银行账号
        const val ADD_BANK_ACCOUNT = 111
        //增加银行卡号
        const val ADD_BANK_CARD_NUM = 112
        //输入手机号
        const val INPUT_PHONE_NUM = 113
        //输入银行卡IPN
        const val INPUT_CARD_IPN = 114
        //输入手机验证码
        const val INPUT_VERIFICATION_CODE = 115

    }

    private var ivBack :ImageView? = null
    private var tvTitle :TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_bind_new_card)
        ivBack = findViewById(R.id.iv_bind_new_card_back)
        tvTitle = findViewById(R.id.tv_bind_new_card_title)

        toStepInternal(ADD_BANK_ACCOUNT)
    }

    fun toStep(step: Int){
        toStepInternal(step)
    }

    private fun toStepInternal(step: Int) {
        if (step == ADD_BANK_ACCOUNT) {
            var addBankFragment = AddBankAccount1Fragment()
            toFragment(addBankFragment)
            tvTitle?.text = resources.getString(R.string.bind_new_card_title1)
        } else if (step == ADD_BANK_CARD_NUM) {
            var addBankNum2Fragment = AddBankNum2Fragment()
            toFragment(addBankNum2Fragment)
            tvTitle?.text = resources.getString(R.string.bind_new_card_title2)
        } else if (step == AddProfileActivity.TO_STEP_3) {
            var profile3Fragment = AddProfile3Fragment()
            toFragment(profile3Fragment)
            tvTitle?.text = resources.getString(R.string.bind_new_card_title3)
        }
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_bind_new_card_container
    }
}