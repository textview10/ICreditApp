package com.loan.icreditapp.dialog.selectdata

import android.app.Dialog
import android.content.Context
import android.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.loan.icreditapp.R

class SelectDataDialog : Dialog {

    private var rvSelectData: RecyclerView? = null

    private var mAdapter: SelectDataAdapter? = null

    private val mLists = ArrayList<Pair<String, String>>()

    private var mObserver: Observer? = null

    constructor(context: Context) : super(context, R.style.DialogTheme) {
        val lp = window!!.attributes
        lp.width = (ScreenUtils.getAppScreenWidth() * 3 / 4) //设置宽度
        window!!.attributes = lp
        setContentView(R.layout.dialog_select_data)
        rvSelectData = findViewById(R.id.rv_select_data)
        val layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        rvSelectData?.setLayoutManager(layoutManager)
        mAdapter = SelectDataAdapter(mLists)
        rvSelectData?.setAdapter(mAdapter)
        mAdapter!!.setOnItemClickListener(object : SelectDataAdapter.OnItemClickListener {
            override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                dismiss()
                if (mObserver != null) {
                    mObserver!!.onItemClick(content, pos)
                }
            }
        })
    }

    fun setList(lists: ArrayList<Pair<String, String>>?, observer: Observer?) {
        mObserver = observer
        mLists.clear()
        mLists.addAll(lists!!)
        mAdapter?.notifyDataSetChanged()
    }

    interface Observer {
        fun onItemClick(content: Pair<String, String>?, pos: Int)
    }

}