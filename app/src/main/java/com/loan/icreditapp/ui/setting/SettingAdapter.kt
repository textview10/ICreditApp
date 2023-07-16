package com.loan.icreditapp.ui.setting

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blankj.utilcode.util.SPUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.setting.SettingBean
import com.loan.icreditapp.global.Constant

class SettingAdapter : RecyclerView.Adapter<ViewHolder> {

    private val TYPE_SWITCH = 111
    private val TYPE_NORMAL = 112

    private var mList : List<SettingBean>? = null
    private var mListener : OnClickListener?  = null

    private var mCurPos : Int = 0

    constructor(list : List<SettingBean>?){
        this.mList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_SWITCH) {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting_switch, parent, false)
            return SettingSwitchHolder(view)
        } else {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
            return SettingHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is SettingHolder) {
            bindNorSwitch(holder, position)
        } else if (holder is SettingSwitchHolder){
            bindSettingSwitch(holder, position)
        }
    }

    private fun bindNorSwitch(holder : SettingHolder, position: Int) {
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

    private fun bindSettingSwitch(holder : SettingSwitchHolder, position: Int) {
        var settingBean = mList!!.get(position)
        var str = holder.itemView.resources.getString(settingBean.title)
        if (!TextUtils.isEmpty(settingBean.desc)){
            str += " "
            str += settingBean.desc
            holder.tvTitle?.setTextSize(12f)
        }
        holder.tvTitle?.text = str
        val afterLoginExecute = SPUtils.getInstance().getBoolean(Constant.TEST_KEY_NOT_AUTO_LOGIN_EXECUTE, true)
        holder.switch?.isChecked = afterLoginExecute
        holder.switch?.setOnCheckedChangeListener( object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                SPUtils.getInstance().put(Constant.TEST_KEY_NOT_AUTO_LOGIN_EXECUTE, isChecked)
            }

        })
    }

    private fun isSelectPos(pos : Int , settingBean : SettingBean) : Boolean{
        if (!settingBean.canSelect){
            return false
        }
        return pos == mCurPos
    }

    fun setCurPos(pos : Int) {
        mCurPos = pos
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    override fun getItemViewType(position : Int) : Int{
        if (mList != null) {
            val settingBean = mList!![position]
            if (settingBean.switchType) {
                return TYPE_SWITCH
            }
        }
        return TYPE_NORMAL
    }
    fun setOnClickListener(listener: OnClickListener){
        mListener = listener
    }

    interface OnClickListener {
        fun OnClick(pos : Int, settingBean: SettingBean)
    }
}
