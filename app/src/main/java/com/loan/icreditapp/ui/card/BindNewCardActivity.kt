package com.loan.icreditapp.ui.card

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.event.UpdateBindCardResult
import com.loan.icreditapp.event.UpdateGetOrderEvent
import com.loan.icreditapp.ui.card.fragment.AddBankAccount1Fragment
import com.loan.icreditapp.ui.card.fragment.AddBankNum2Fragment
import com.loan.icreditapp.ui.profile.AddProfileActivity
import com.loan.icreditapp.ui.profile.fragment.AddProfile2Fragment
import com.loan.icreditapp.ui.profile.fragment.AddProfile3Fragment
import org.greenrobot.eventbus.EventBus

class BindNewCardActivity : BaseActivity() {

    companion object {
        const val EXTRA_ADD_BANK_CARD = "extra_add_bank_card"
        //添加银行账号
        const val ADD_BANK_ACCOUNT = 111
        //增加银行卡号
        const val ADD_BANK_CARD_NUM = 112

        const val SUCCESS = 113

        const val BIND_BINK_CARD_SUCCESS = 114

        fun launchAddBankAccount(context: Context){
            val intent = Intent(context, BindNewCardActivity::class.java)
            intent.putExtra(EXTRA_ADD_BANK_CARD, ADD_BANK_ACCOUNT)
            context.startActivity(intent)
        }

        fun launchAddBankCard(context: Context){
            val intent = Intent(context, BindNewCardActivity::class.java)
            intent.putExtra(EXTRA_ADD_BANK_CARD, ADD_BANK_CARD_NUM)
            context.startActivity(intent)
        }

    }

    private var ivBack :ImageView? = null
    private var tvTitle :TextView? = null
    private var mType : Int = ADD_BANK_CARD_NUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        mType = intent.getIntExtra(EXTRA_ADD_BANK_CARD, ADD_BANK_CARD_NUM)

        setContentView(R.layout.activity_bind_new_card)
        ivBack = findViewById(R.id.iv_bind_new_card_back)
        tvTitle = findViewById(R.id.tv_bind_new_card_title)

        ivBack?.setOnClickListener(View.OnClickListener {
            finish()
        })
        toStepInternal(if (mType == ADD_BANK_CARD_NUM ) ADD_BANK_CARD_NUM
            else ADD_BANK_ACCOUNT)
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
        } else if (step == SUCCESS) {
            EventBus.getDefault().post(UpdateGetOrderEvent())
            finish()
        } else if (step == BIND_BINK_CARD_SUCCESS){
            EventBus.getDefault().post(UpdateBindCardResult())
            finish()
        }
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_bind_new_card_container
    }
}