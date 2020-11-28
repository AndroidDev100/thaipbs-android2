package me.vipa.app.activities.downloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.brightcove.player.model.Video
import com.brightcove.player.network.DownloadStatus
import com.brightcove.player.offline.MediaDownloadable
import com.google.gson.Gson
import com.mmtv.utils.helpers.downloads.DownloadHelper
import me.vipa.app.R
import me.vipa.app.databinding.ActivityMyDownloadsBinding
import me.vipa.app.utils.helpers.downloads.room.DownloadModel
import kotlinx.android.synthetic.main.activity_my_downloads.*
import me.vipa.app.baseModels.BaseBindingActivity
import me.vipa.app.utils.cropImage.helpers.Logger
import me.vipa.app.utils.helpers.downloads.VideoListListener
import java.io.Serializable

class MyDownloads : BaseBindingActivity<ActivityMyDownloadsBinding>(), MediaDownloadable.DownloadEventListener, VideoListListener {
    override fun inflateBindingLayout(inflater: LayoutInflater): ActivityMyDownloadsBinding {
        return ActivityMyDownloadsBinding.inflate(inflater)
    }

    override fun downloadStatus(videoId: String, downloadStatus: DownloadStatus?) {
        Logger.e(TAG, downloadStatus?.code.toString())
        downloadHelper.updateVideoStatus(downloadStatus!!.code, videoId)
        downloadsAdapter?.notifyVideoChanged(videoId, downloadStatus)

    }

    private lateinit var downloadHelper: DownloadHelper
    private var downloadsAdapter: DownloadsAdapter? = null
    val TAG = this.javaClass.simpleName
    override fun alreadyDownloaded(video: Video) {
    }

    override fun downloadedVideos(p0: MutableList<out Video>?) {
    }

    override fun videoFound(video: Video?) {
    }

    override fun onDownloadRequested(video: Video) {
        Logger.e(TAG, String.format(
                "Starting to process '%s' video download request", video.name))
    }

    override fun onDownloadStarted(video: Video, l: Long, map: Map<String, Serializable>) {
        Logger.e(TAG, "onDownloadStarted" + video.name)
        downloadHelper.updateVideoStatus(DownloadStatus.STATUS_DOWNLOADING, video.id)
    }

    override fun onDownloadProgress(video: Video, downloadStatus: DownloadStatus) {
        Logger.e(TAG, "onDownloadProgress" + downloadStatus.progress)
        downloadsAdapter?.notifyVideoChanged(video.id, downloadStatus)
    }

    override fun onDownloadPaused(video: Video, downloadStatus: DownloadStatus) {
        Logger.e(TAG, "onDownloadPaused")
    }

    override fun onDownloadCompleted(video: Video, downloadStatus: DownloadStatus) {
        downloadHelper.updateVideoStatus(DownloadStatus.STATUS_COMPLETE, video.id)
        downloadsAdapter?.notifyVideoChanged(video.id, downloadStatus)
    }

    override fun onDownloadCanceled(video: Video) {
        Logger.e(TAG, "onDownloadCanceled")

    }

    override fun onDownloadDeleted(video: Video) {
        Logger.e(TAG, "onDownloadDeleted")
    }

    override fun onDownloadFailed(video: Video, downloadStatus: DownloadStatus) {
        Logger.e(TAG, "onDownloadFailed")

    }

    override fun downloadVideo(video: Video) {

    }

    override fun pauseVideoDownload(video: Video) {
        Logger.e(TAG, "pauseVideoDownload")
    }

    override fun resumeVideoDownload(video: Video) {
    }

    override fun deleteVideo(video: Video) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var counter = 0
        downloadHelper = DownloadHelper(this, this)
        downloadHelper.getAllVideosFromDatabase().observe(this, Observer {
            downloadsAdapter = DownloadsAdapter(this, it)
            downloaded_recycler_view.layoutManager = LinearLayoutManager(this)
            downloaded_recycler_view.setHasFixedSize(true)
            downloaded_recycler_view.itemAnimator = DefaultItemAnimator()
            downloaded_recycler_view.adapter = downloadsAdapter
        })
        setupToolbar()
    }


    private fun setupToolbar() {
        binding.toolbar.llSearchIcon.visibility = View.GONE
        binding.toolbar.backLayout.visibility = View.VISIBLE
        binding.toolbar.homeIcon.visibility = View.GONE
        binding.toolbar.titleText.visibility = View.VISIBLE
        binding.toolbar.screenText.text = resources.getString(R.string.my_downloads)
        binding.toolbar.backLayout.setOnClickListener { onBackPressed() }
    }

    private fun setData(it: DownloadModel?) {
        Logger.e(TAG, "Videos: => " + Gson().toJson(it))
    }
}