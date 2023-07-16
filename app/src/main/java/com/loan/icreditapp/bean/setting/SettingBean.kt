package com.loan.icreditapp.bean.setting

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.loan.icreditapp.ui.setting.PageType

class SettingBean {
    @DrawableRes
    var leftIconRes: Int
    @StringRes
    var title: Int
    @PageType
    var type: Int

    var canSelect: Boolean = false

    var desc: String? = null

    var switchType: Boolean = false

    constructor(@DrawableRes icon: Int, @StringRes title: Int, @PageType type :Int) {
        this.leftIconRes = icon
        this.title = title
        this.type = type
    }

    constructor(@DrawableRes icon: Int, @StringRes title: Int, @PageType type :Int, canSelect : Boolean) {
        this.leftIconRes = icon
        this.title = title
        this.type = type
        this.canSelect = canSelect
    }

    constructor(@DrawableRes icon: Int, @StringRes title: Int, @PageType type :Int, desc : String) {
        this.leftIconRes = icon
        this.title = title
        this.type = type
        this.desc = desc
    }
}