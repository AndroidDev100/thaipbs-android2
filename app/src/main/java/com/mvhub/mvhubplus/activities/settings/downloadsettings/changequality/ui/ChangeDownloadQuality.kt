package com.mvhub.mvhubplus.activities.settings.downloadsettings.changequality.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mvhub.mvhubplus.R
import com.mvhub.mvhubplus.activities.settings.downloadsettings.DownloadSettings.Companion.CHANGE_QUALITY_REQUEST_CODE
import com.mvhub.mvhubplus.databinding.ActivityChangeDownloadQualityBinding


class ChangeDownloadQuality : com.mvhub.mvhubplus.baseModels.BaseBindingActivity<ActivityChangeDownloadQualityBinding>() {
    private var changeDownloadQualityAdapter: com.mvhub.mvhubplus.activities.settings.downloadsettings.changequality.adapter.ChangeDownloadQualityAdapter? = null
    override fun inflateBindingLayout(inflater: LayoutInflater): ActivityChangeDownloadQualityBinding {
        return ActivityChangeDownloadQualityBinding.inflate(inflater)
    }

    private var preference: com.mvhub.mvhubplus.utils.helpers.ksPreferenceKeys.KsPreferenceKeys? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preference = com.mvhub.mvhubplus.utils.helpers.ksPreferenceKeys.KsPreferenceKeys.getInstance()
        setupToolbar()
        uiInitialization()

    }

    private fun setupToolbar() {
        binding.toolbar.llSearchIcon.visibility = View.GONE
        binding.toolbar.backLayout.visibility = View.VISIBLE
        binding.toolbar.homeIcon.visibility = View.GONE
        binding.toolbar.titleText.visibility = View.VISIBLE
        binding.toolbar.screenText.text = resources.getString(R.string.download_quality)
        binding.toolbar.backLayout.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        setResult(CHANGE_QUALITY_REQUEST_CODE)
        finish()
    }

    private val downloadQualityList: ArrayList<String> = ArrayList()
    private fun setAdapter() {
        downloadQualityList.addAll(resources.getStringArray(R.array.download_quality))
        com.mvhub.mvhubplus.utils.cropImage.helpers.Logger.e("ChangeDownloadQuality", Gson().toJson(downloadQualityList));
        changeDownloadQualityAdapter = com.mvhub.mvhubplus.activities.settings.downloadsettings.changequality.adapter.ChangeDownloadQualityAdapter(this@ChangeDownloadQuality, downloadQualityList)
        binding.recyclerview.adapter = changeDownloadQualityAdapter
    }

    private fun uiInitialization() {
        binding.recyclerview.hasFixedSize()
        binding.recyclerview.isNestedScrollingEnabled = false
        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(binding.recyclerview.context,
                (binding.recyclerview.layoutManager as LinearLayoutManager).orientation)
        binding.recyclerview.addItemDecoration(dividerItemDecoration)
        setAdapter()
    }
}