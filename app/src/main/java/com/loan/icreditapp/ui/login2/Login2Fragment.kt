package com.loan.icreditapp.ui.login2

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.presenter.PhoneNumPresenter

class Login2Fragment : BaseFragment() {

    private var etSignIn : AppCompatEditText? = null
    private var tvCommit : AppCompatTextView? = null
    private var mSpinner : Spinner? = null
    private var ivClear : AppCompatImageView? = null
    private var ivCheckState : AppCompatImageView? = null

    private var mPresenter: PhoneNumPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login2, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSignIn = view.findViewById<AppCompatEditText>(R.id.et_signin_phone_num)
        tvCommit = view.findViewById<AppCompatTextView>(R.id.tv_login2_commit)
        mSpinner = view.findViewById<Spinner>(R.id.spinner_signin_input)
        ivClear = view.findViewById<AppCompatImageView>(R.id.iv_signin_phonenum_clear)
        ivCheckState = view.findViewById<AppCompatImageView>(R.id.iv_login2_agree_state)

        initView()
    }

    private fun initView(){
        mPresenter = PhoneNumPresenter(context)
        mPresenter?.initSpinner(mSpinner!!)

        etSignIn?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val text: String = editable.toString()
                if (!TextUtils.isEmpty(text)) {
                    ivClear?.visibility = View.VISIBLE
                } else {
                    ivClear?.visibility = View.GONE
                }
            }
        })
    }
}