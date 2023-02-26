package com.loan.icreditapp.ui.home.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.ThreadUtils.SimpleTask
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.TextInfoResponse
import com.loan.icreditapp.global.ConfigMgr
import com.loan.icreditapp.ui.profile.widget.SelectContainer
import com.loan.icreditapp.util.JumpUtils
import com.lzy.okgo.OkGo
import java.io.File


class ContactUsFragment : BaseFragment() {

    private val TAG = "ContactUsFragment"

    private var selectPhone1: SelectContainer? = null
    private var selectPhone2: SelectContainer? = null
    private var selectWhatApp1: SelectContainer? = null
    private var selectWhatApp2: SelectContainer? = null
    private var selectEmail: SelectContainer? = null

    private var flPhone1: FrameLayout? = null
    private var flPhone2: FrameLayout? = null
    private var flWhatApp1: FrameLayout? = null
    private var flWhatApp2: FrameLayout? = null
    private var flEmail: FrameLayout? = null

    private var phoneNum1: String? = null
    private var phoneNum2: String? = null
    private var whatApp1: String? = null
    private var whatApp2: String? = null
    private var email: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_contact_us, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectPhone1 = view.findViewById(R.id.select_container_contact_us_phone1)
        selectPhone2 = view.findViewById(R.id.select_container_contact_us_phone2)
        selectWhatApp1 = view.findViewById(R.id.select_container_contact_us_whatapp1)
        selectWhatApp2 = view.findViewById(R.id.select_container_contact_us_whatapp2)
        selectEmail = view.findViewById(R.id.select_container_contact_us_email)

        flPhone1 = view.findViewById(R.id.fl_contact_us_phone1)
        flPhone2 = view.findViewById(R.id.fl_contact_us_phone2)
        flWhatApp1 = view.findViewById(R.id.fl_contact_us_whatapp1)
        flWhatApp2 = view.findViewById(R.id.fl_contact_us_whatapp2)
        flEmail = view.findViewById(R.id.fl_contact_us_email)

        flPhone1?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            if (TextUtils.isEmpty(phoneNum1)) {
                ToastUtils.showShort("phoneNum1 is empty")
                return@OnClickListener
            }
            executeCallPhone(phoneNum1!!)
        })
        flPhone1?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            if (TextUtils.isEmpty(phoneNum2)) {
                ToastUtils.showShort("phoneNum2 is empty")
                return@OnClickListener
            }
            executeCallPhone(phoneNum2!!)
        })
        flPhone2?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            if (TextUtils.isEmpty(phoneNum2)) {
                ToastUtils.showShort("phoneNum2 is empty")
                return@OnClickListener
            }
            executeCallPhone(phoneNum2!!)
        })
        flWhatApp1?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            if (TextUtils.isEmpty(whatApp1)) {
                ToastUtils.showShort("whatApp1 is empty")
                return@OnClickListener
            }
            checkAndToWhatApp(context, whatApp1!!)
        })
        flWhatApp2?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            if (TextUtils.isEmpty(whatApp2)) {
                ToastUtils.showShort("whatApp2 is empty")
                return@OnClickListener
            }
            checkAndToWhatApp(context, whatApp2!!)
        })
        flEmail?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            if (TextUtils.isEmpty(email)) {
                ToastUtils.showShort("email address is empty")
                return@OnClickListener
            }
            if (isRemoving || isDetached) {
                return@OnClickListener
            }
            ThreadUtils.executeByCached(object : SimpleTask<String?>() {
                @Throws(Throwable::class)
                override fun doInBackground(): String? {
                    var logFoldPath = File(LogSaver.getLogFileFolder())
                    if (logFoldPath.listFiles().isNotEmpty()) {
                        val srcFile = logFoldPath.listFiles()[0]
                        val traceFile =
                            File(requireContext().filesDir.absolutePath + "/log/", "trace")
                        FileUtils.createFileByDeleteOldFile(traceFile)
                        val success = ZipUtils.zipFile(srcFile, traceFile)
                        if (success) {
                            return traceFile.absolutePath
                        }
                    }
                    return null
                }

                override fun onSuccess(result: String?) {
                    startFeedBackEmail(result)
                }
            })

        })
        requestUrl()
    }

    private fun startFeedBackEmail(traceFile: String?) {
        try {
            val data = Intent(Intent.ACTION_SEND)
            data.data = Uri.parse(email)
            data.setType("text/plain")
            val addressEmail = arrayOf<String>(email!!)
            val addressCC = arrayOf<String>("hanjierui@126.com")
            data.putExtra(Intent.EXTRA_EMAIL, addressEmail)
//            data.putExtra(Intent.EXTRA_CC, addressCC)
            data.putExtra(Intent.EXTRA_SUBJECT, "Crediting Feedback")
            data.putExtra(Intent.EXTRA_TEXT, "Hi:")
            if (!TextUtils.isEmpty(traceFile)) {
                data.putExtra(
                    Intent.EXTRA_STREAM,
                    getFileUri(requireContext(), File(traceFile!!), getAuthority(requireContext()))
                )
            }
            activity?.startActivity(Intent.createChooser(data, "Crediting Feedback:"))
        } catch (e: Exception) {
            if ((e is ActivityNotFoundException)) {
                ToastUtils.showShort(" not exist email app")
            }
            if (BuildConfig.DEBUG) {
                throw e
            }
        }
    }

    private fun checkAndToWhatApp(context: Context?, mobile: String) {
        if (context == null) {
            return
        }
        var isInstall = JumpUtils.isAppInstall(context, "com.whatsapp")
        if (isInstall) {
            JumpUtils.chatInWhatsApp(context, mobile)
        } else {
            ToastUtils.showShort("not exist whatsapp.")
        }
    }

    private fun executeCallPhone(phoneNum: String) {
        var hasPermissions: Boolean = PermissionUtils.isGranted(Manifest.permission.CALL_PHONE)
        if (hasPermissions) {
            executeCallPhoneInternal(phoneNum)
        } else {
            PermissionUtils.permission(Manifest.permission.CALL_PHONE)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        executeCallPhoneInternal(phoneNum)
                    }

                    override fun onDenied() {
                        ToastUtils.showShort("Please allow permission")
                    }
                }).request()
        }
    }

    @SuppressLint("MissingPermission")
    private fun executeCallPhoneInternal(phoneNum: String) {
        PhoneUtils.call(phoneNum)
    }

    private fun requestUrl() {
        ConfigMgr.getTextInfo(object : ConfigMgr.CallBack3 {
            override fun onGetData(textInfoResponse: TextInfoResponse?) {
                if (textInfoResponse != null) {
                    updateContactUsUI(textInfoResponse)
                }
            }

        })
    }

    private fun updateContactUsUI(textInfo: TextInfoResponse) {
        if (!TextUtils.isEmpty(textInfo.phone)) {
            val split = textInfo.phone!!.split("\n")
            if (split != null && split.size > 0) {
                val phoneNum1 = split[0]
                if (split.size > 1) {
                    val phoneNum2 = split[1]
                    if (!TextUtils.isEmpty(phoneNum1)) {
                        flPhone1?.visibility = View.VISIBLE
                        this.phoneNum1 = phoneNum1
                        selectPhone1?.setData(phoneNum1)
                    }
                    if (!TextUtils.isEmpty(phoneNum2)) {
                        this.phoneNum2 = phoneNum2
                        flPhone2?.visibility = View.VISIBLE
                        selectPhone2?.setData(phoneNum2)
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(textInfo.whatsApp)) {
            flWhatApp1?.visibility = View.VISIBLE
            whatApp1 = textInfo.whatsApp
            selectWhatApp1?.setData(textInfo.whatsApp)
        }
        if (!TextUtils.isEmpty(textInfo.whatsApp1)) {
            flWhatApp2?.visibility = View.VISIBLE
            whatApp2 = textInfo.whatsApp1
            selectWhatApp2?.setData(textInfo.whatsApp1)
        }
        if (!TextUtils.isEmpty(textInfo.email)) {
            this.email = textInfo.email
            flEmail?.visibility = View.VISIBLE
            selectEmail?.setData(textInfo.email)
        }
    }

    override fun onDestroyView() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroyView()
    }

    private fun getFileUri(context: Context, file: File, authority: String): Uri? {
        var useProvider = false
        var canAdd = false
        if (file.exists()) {
            useProvider = true
            canAdd = true
        }
        return if (canAdd) {
            if (useProvider) {
                FileProvider.getUriForFile(getDPContext(context), authority, file)
            } else {
                Uri.fromFile(file)
            }
        } else null
    }

    private fun getDPContext(context: Context): Context {
        var storageContext: Context = context
        if (Build.VERSION.SDK_INT >= 24) {
            if (!context.isDeviceProtectedStorage) {
                val deviceContext = context.createDeviceProtectedStorageContext()
                storageContext = deviceContext
            }
        }
        return storageContext
    }

    private fun getAuthority(context: Context) =
        context.applicationInfo.packageName + ".fileprovider"
}