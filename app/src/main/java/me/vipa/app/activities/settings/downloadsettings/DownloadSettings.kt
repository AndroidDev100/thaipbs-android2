package me.vipa.app.activities.settings.downloadsettings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import me.vipa.app.R
import me.vipa.app.activities.settings.downloadsettings.changequality.ui.ChangeDownloadQuality
import me.vipa.app.baseModels.BaseBindingActivity
import me.vipa.app.databinding.ActivityDownloadSettingsBinding
import me.vipa.app.utils.constants.SharedPrefesConstants
import me.vipa.app.utils.helpers.SharedPrefHelper

class DownloadSettings : BaseBindingActivity<ActivityDownloadSettingsBinding>(), View.OnClickListener {
    companion object {
        const val CHANGE_QUALITY_REQUEST_CODE = 10001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()

        binding.selectedQualityText.text = resources.getStringArray(R.array.download_quality)[SharedPrefHelper(this).getInt(SharedPrefesConstants.DOWNLOAD_QUALITY_INDEX, 4)]
        binding.textDownloadQuality.setOnClickListener(this)
        binding.switchTheme.isChecked = SharedPrefHelper(this).getInt(SharedPrefesConstants.DOWNLOAD_OVER_WIFI, 0) == 1

        binding.switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SharedPrefHelper(this@DownloadSettings).setInt(SharedPrefesConstants.DOWNLOAD_OVER_WIFI, 1)
            } else {
                SharedPrefHelper(this@DownloadSettings).setInt(SharedPrefesConstants.DOWNLOAD_OVER_WIFI, 0)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.llSearchIcon.visibility = View.GONE
        binding.toolbar.backLayout.visibility = View.VISIBLE
        binding.toolbar.homeIcon.visibility = View.GONE
        binding.toolbar.titleText.visibility = View.VISIBLE
        binding.toolbar.screenText.text = resources.getString(R.string.download_settings)
        binding.toolbar.backLayout.setOnClickListener { onBackPressed() }
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): ActivityDownloadSettingsBinding {
        return ActivityDownloadSettingsBinding.inflate(inflater)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.text_download_quality -> {
                startActivityForResult(Intent(this, ChangeDownloadQuality::class.java), CHANGE_QUALITY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            CHANGE_QUALITY_REQUEST_CODE -> {
                binding.selectedQualityText.text = resources.getStringArray(R.array.download_quality)[SharedPrefHelper(this).getInt(SharedPrefesConstants.DOWNLOAD_QUALITY_INDEX, 4)]
            }
        }
    }
}
