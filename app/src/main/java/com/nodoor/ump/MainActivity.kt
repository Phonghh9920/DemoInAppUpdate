package com.nodoor.ump

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.ump.FormError
import com.nodoor.iau_module.ConsentCallBack
import com.nodoor.iau_module.NoDoorAdConsent
import com.nodoor.iau_module.UpdateManager
import com.nodoor.iau_module.UpdateInstanceCallback
import com.nodoor.ump.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), ConsentCallBack {

    private val isShowDialogUpdate: Boolean = true
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonCheckGgFlex.setOnClickListener {
            updateWithType(AppUpdateType.FLEXIBLE, isShowDialogUpdate)
        }
        binding.buttonCheckUpdateGgImmediate.setOnClickListener {
            updateWithType(AppUpdateType.IMMEDIATE, isShowDialogUpdate)
        }
        binding.buttonLoadConsent.setOnClickListener {
            NoDoorAdConsent.loadAndShowConsent(false, this)
            binding.buttonShowConsent.isEnabled = false
            binding.progressLoading.visibility = View.VISIBLE
        }
        binding.buttonShowConsent.setOnClickListener {
            NoDoorAdConsent.showDialogConsent(this)
//            startActivity(Intent(this, LanguageActivity::class.java))
        }
        binding.buttonLoadAndShowConsent.setOnClickListener {
            binding.progressLoading.visibility = View.VISIBLE
            NoDoorAdConsent.loadAndShowConsent(true, this)
        }
        binding.buttonRestartConsent.setOnClickListener {
            NoDoorAdConsent.resetConsentDialog()
        }
    }

    private fun updateWithType(type: Int, isShowDialogUpdate: Boolean) {
        if (isShowDialogUpdate) UpdateManager(this, 100, object : UpdateInstanceCallback {
            override fun updateAvailableListener(updateAvailability: AppUpdateInfo): Int {
                when (updateAvailability.updateAvailability()) {
                    UpdateAvailability.UPDATE_AVAILABLE -> {
                        // Need to show Dialog check option then return type
                        return type
                    }

                }
                return AppUpdateType.FLEXIBLE
            }

        }).checkUpdateAvailable()
    }

    override fun getCurrentActivity(): Activity {
        return this@MainActivity
    }

    override fun isDebug(): Boolean {
        return true
    }

    override fun isUnderAgeAd(): Boolean {
        return false
    }

    override fun onNotUsingAdConsent() {
        Log.v("ITGAdConsent", "onNotUsingAdConsent")
        binding.progressLoading.visibility = View.GONE
        binding.buttonShowConsent.isEnabled = false
    }


    override fun onConsentSuccess(canPersonalized: Boolean) {
        if (canPersonalized) {
            Log.v("ITGAdConsent", "onConsentSuccess true")
        } else {
            Log.v("ITGAdConsent", "onConsentSuccess false")
        }
        binding.progressLoading.visibility = View.GONE
        binding.buttonShowConsent.isEnabled = false

    }

    override fun onConsentError(formError: FormError) {
        binding.progressLoading.visibility = View.GONE
        Log.v("ITGAdConsent", "formError  ${formError.message}")
    }

    override fun onConsentStatus(consentStatus: Int) {
        Log.v("ITGAdConsent", "consentStatus  ${consentStatus}")

        binding.buttonShowConsent.isEnabled = true
        binding.progressLoading.visibility = View.GONE
    }

    override fun onRequestShowDialog() {
        Log.v("ITGAdConsent", "onRequestShowDialog  ")
    }
}