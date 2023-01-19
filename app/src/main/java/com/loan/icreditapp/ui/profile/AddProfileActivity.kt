package com.loan.icreditapp.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity

class AddProfileActivity : BaseActivity() {

    private var ivBack :ImageView? = null
    private var tvTitle :TextView? = null
    private var ivStep :ImageView? = null
    private var tvStepTitle :TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_profile)
        initializeView()
    }

    private fun initializeView() {
        ivBack = findViewById(R.id.iv_add_profile_back)
        tvTitle = findViewById(R.id.tv_add_profile_title)

        ivBack?.setOnClickListener(View.OnClickListener {
            finish()
        })

        ivStep = findViewById(R.id.iv_add_profile_step)
        tvStepTitle = findViewById(R.id.tv_add_profile_step_title)


    }

    private fun toStep(step :Int) {
        if (step == 0){
            ivStep?.setImageResource(R.drawable.ic_step_1)
            tvStepTitle?.setText(resources.getText(R.string.add_profile_title_1))
        } else if (step == 1){
            ivStep?.setImageResource(R.drawable.ic_step_2)
            tvStepTitle?.setText(resources.getText(R.string.add_profile_title_2))
        } else if (step == 2){
            ivStep?.setImageResource(R.drawable.ic_step_3)
            tvStepTitle?.setText(resources.getText(R.string.add_profile_title_3))
        }
    }
}