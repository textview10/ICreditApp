package com.loan.icreditapp.ui.setting

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.setting.SettingBean

class SettingAdapter : RecyclerView.Adapter<SettingHolder> {

    private var mList : List<SettingBean>? = null
    private var mListener : OnClickListener?  = null

    private var mCurPos : Int = 0

    constructor(list : List<SettingBean>?){
        this.mList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
        return SettingHolder(view)
    }


    override fun onBindViewHolder(holder: SettingHolder, position: Int) {
        var settingBean = mList!!.get(position)
        holder.ivLeftIcon?.setImageResource(settingBean.leftIconRes)
        var str = holder.itemView.resources.getString(settingBean.title)
        if (!TextUtils.isEmpty(settingBean.desc)){
            str += " "
            str += settingBean.desc
            holder.tvTitle?.setTextSize(12f)
        }
        holder.tvTitle?.text = str
        if (isSelectPos(position, settingBean)) {
            holder.llContainer?.setBackgroundResource(R.drawable.setting_bg)
        } else {
            holder.llContainer?.setBackgroundColor(Color.WHITE)
        }

        holder.flContainer?.setOnClickListener(View.OnClickListener {
            var oldPos = mCurPos
            if (settingBean.canSelect){
                mCurPos = position
            }
            if (mCurPos != oldPos){
                notifyItemChanged(mCurPos)
                notifyItemChanged(oldPos)
            }
            mListener?.OnClick(position, settingBean)
        })
    }

    private fun isSelectPos(pos : Int , settingBean : SettingBean) : Boolean{
        if (!settingBean.canSelect){
            return false
        }
        return pos == mCurPos
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    fun setOnClickListener(listener: OnClickListener){
        mListener = listener
    }

    interface OnClickListener {
        fun OnClick(pos : Int, settingBean: SettingBean)
    }
}