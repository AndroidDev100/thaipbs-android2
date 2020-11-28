package me.vipa.app.activities.settings.downloadsettings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.vipa.app.R
import com.vipa.app.activities.settings.downloadsettings.changequality.ui.ChangeDownloadQuality
import com.vipa.app.databinding.ActivityDownloadSettingsBinding
import com.vipa.app.utils.constants.SharedPrefesConstants

class DownloadSettings : _root_ide_package_.me.vipa.app.baseModels.BaseBindingActivity<ActivityDownloadSettingsBinding>(), View.OnClickListener {
    companion object {
        const val CHANGE_QUALITY_REQUEST_CODE = 10001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()

        binding.selectedQualityText.text = resources.getStringArray(R.array.download_quality)[_root_ide_package_.me.vipa.app.utils.helpers.SharedPrefHelper(this).getInt(SharedPrefesConstants.DOWNLOAD_QUALITY_INDEX, 4)]
        binding.textDownloadQuality.setOnClickListener(this)
        binding.switchTheme.isChecked = _root_ide_package_.me.vipa.app.utils.helpers.SharedPrefHelper(this).getInt(SharedPrefesConstants.DOWNLOAD_OVER_WIFI, 0) == 1

        binding.switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                _root_ide_package_.me.vipa.app.utils.helpers.SharedPrefHelper(this@DownloadSettings).setInt(SharedPrefesConstants.DOWNLOAD_OVER_WIFI, 1)
            } else {
                _root_ide_package_.me.vipa.app.utils.helpers.SharedPrefHelper(this@DownloadSettings).setInt(SharedPrefesConstants.DOWNLOAD_OVER_WIFI, 0)
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
                binding.selectedQualityText.text = resources.getStringArray(R.array.download_quality)[_root_ide_package_.me.vipa.app.utils.helpers.SharedPrefHelper(this).getInt(SharedPrefesConstants.DOWNLOAD_QUALITY_INDEX, 4)]
            }
        }
    }
}
