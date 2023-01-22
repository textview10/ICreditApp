package com.loan.icreditapp.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.ui.profile.fragment.AddProfile1Fragment
import com.loan.icreditapp.ui.profile.fragment.AddProfile2Fragment
import com.loan.icreditapp.ui.profile.fragment.AddProfile3Fragment

class AddProfileActivity : BaseActivity() {

    companion object {
        val TO_STEP_1 = 1111
        val TO_STEP_2 = 1112
        val TO_STEP_3 = 1113
    }

    private var ivBack: ImageView? = null
    private var tvTitle: TextView? = null
    private var ivStep: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.theme_color))
        BarUtils.setStatusBarLightMode(this, false)
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

        toStepInternal(TO_STEP_1)
    }

    fun toStep(step: Int) {
        toStepInternal(step)
    }

    private fun toStepInternal(step: Int) {
        if (step == TO_STEP_1) {
            ivStep?.setImageResource(R.drawable.ic_step_1)
            var profile1Fragment = AddProfile1Fragment()
            toFragment(profile1Fragment)
        } else if (step == TO_STEP_2) {
            ivStep?.setImageResource(R.drawable.ic_step_2)
            var profile2Fragment = AddProfile2Fragment()
            toFragment(profile2Fragment)
        } else if (step == TO_STEP_3) {
            ivStep?.setImageResource(R.drawable.ic_step_3)
            var profile3Fragment = AddProfile3Fragment()
            toFragment(profile3Fragment)
        }
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_add_profile_container
    }
}