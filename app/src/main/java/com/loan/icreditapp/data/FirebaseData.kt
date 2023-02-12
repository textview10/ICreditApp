package com.loan.icreditapp.data

import android.os.Parcel
import android.os.Parcelable

class FirebaseData() : Parcelable {

    var orderId: String? = null

    // 0, 1 申请成功, 2, 申请状态
    var status: Int = 0

    constructor(parcel: Parcel) : this() {
        orderId = parcel.readString()
        status = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FirebaseData> {
        override fun createFromParcel(parcel: Parcel): FirebaseData {
            return FirebaseData(parcel)
        }

        override fun newArray(size: Int): Array<FirebaseData?> {
            return arrayOfNulls(size)
        }
    }
}