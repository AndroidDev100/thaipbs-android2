package me.vipa.app.activities.downloads

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.player.edge.OfflineCallback
import com.brightcove.player.model.Video
import com.brightcove.player.network.DownloadStatus
import com.brightcove.player.offline.MediaDownloadable
import com.mmtv.utils.helpers.downloads.DownloadHelper

import com.vipa.app.R
import com.vipa.app.databinding.ListDownloadItemBinding
import com.vipa.app.utils.helpers.downloads.DownloadedVideoActivity
import com.vipa.app.utils.helpers.downloads.room.DownloadModel
import com.vipa.app.utils.helpers.downloads.room.DownloadedVideo
import java.io.File
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.set
import kotlin.math.log10
import kotlin.math.pow


class DownloadsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>, MediaDownloadable.DownloadEventListener, _root_ide_package_.me.vipa.app.utils.helpers.downloads.VideoListListener {
    private fun deleteVideo(view: View, downloadedVideo: DownloadedVideo, position: Int) {
        val popup = PopupMenu(context, view)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete_download -> {
                    downloadHelper.deleteVideo(downloadedVideo.videoId)
                    downloadedVideos.remove(downloadedVideo)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, downloadedVideos.size)
                    buildIndexMap()
                }
            }
            false
        }

        popup.inflate(R.menu.my_downloads_menu)
        popup.show()
    }

    private fun showPauseCancelPopUpMenu(view: View, video: DownloadedVideo, position: Int) {
        val popup = PopupMenu(context, view)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.cancel_download -> {
                    downloadHelper.cancelVideo(video.videoId)
                    downloadedVideos.remove(video)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, downloadedVideos.size)
                    buildIndexMap()
                }
                R.id.pause_download -> downloadHelper.pauseVideo(video.videoId)
            }
            false
        }
        popup.inflate(R.menu.download_menu)
        popup.show()
    }

    private lateinit var video: Video
    private var viewHolder: _root_ide_package_.me.vipa.app.activities.downloads.DownloadsAdapter.LandscapeItemRowHolder? = null
    private val TAG = this.javaClass.simpleName
    private var context: Activity
    private val downloadedVideos = ArrayList<DownloadedVideo>()
    private val indexMap = HashMap<String, Int>()
    private val downloadHelper: DownloadHelper

    constructor(activity: Activity, downloadsList: DownloadModel) {
        context = activity
        downloadHelper = DownloadHelper(activity, this)
        downloadsList.downloadVideos.forEachIndexed { index, downloadedVideo ->
            val b = downloadedVideo.downloadType == _root_ide_package_.me.vipa.app.utils.MediaTypeConstants.getInstance().series || downloadedVideo.downloadType ==  _root_ide_package_.me.vipa.app.utils.MediaTypeConstants.getInstance().episode
            if (b) {
                if (Integer.parseInt(downloadedVideo.episodeCount) != 0){
                    downloadedVideos.add(downloadedVideo)
                }else{
                    //fetchVideos(downloadedVideo);
/*
                    downloadHelper.findOfflineVideoById(downloadedVideo.videoId, object : OfflineCallback<Video> {
                        override fun onSuccess(video: Video) {
                            Logger.e("Video Found", "true")
                            if (!video.isClearContent) {
                                if (video.licenseExpiryDate!!.time >= System.currentTimeMillis()) {
                                    Logger.e("License", "Expiry" + video.licenseExpiryDate)
                                    downloadedVideos.add(downloadedVideo)
                                    if (index == downloadsList.downloadVideos.size - 1) {
                                        buildIndexMap()
                                    }
                                } else {
                                    downloadHelper.deleteVideo(video)
                                    if (index == downloadsList.downloadVideos.size - 1) {
                                        buildIndexMap()
                                    }
                                }
                            } else {
                                downloadedVideos.add(downloadedVideo)
                                if (index == downloadsList.downloadVideos.size - 1) {
                                    buildIndexMap()
                                }
                            }
                        }

                        override fun onFailure(p0: Throwable?) {
                            downloadHelper.deleteVideo(video)
                            if (index == downloadsList.downloadVideos.size - 1) {
                                buildIndexMap()
                            }
                        }
                    })
*/

                }

            } else {
                downloadHelper.findOfflineVideoById(downloadedVideo.videoId, object : OfflineCallback<Video> {
                    override fun onSuccess(video: Video) {
                        _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e("Video Found", "true")
                        if (!video.isClearContent) {
                            if (video.licenseExpiryDate!!.time >= System.currentTimeMillis()) {
                                _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e("License", "Expiry" + video.licenseExpiryDate)
                                downloadedVideos.add(downloadedVideo)
                                if (index == downloadsList.downloadVideos.size - 1) {
                                    buildIndexMap()
                                }
                            } else {
                                downloadHelper.deleteVideo(video)
                                if (index == downloadsList.downloadVideos.size - 1) {
                                    buildIndexMap()
                                }
                            }
                        } else {
                            downloadedVideos.add(downloadedVideo)
                            if (index == downloadsList.downloadVideos.size - 1) {
                                buildIndexMap()
                            }else{
                                buildIndexMap()
                            }
                        }
                    }

                    override fun onFailure(p0: Throwable?) {
                        downloadHelper.deleteVideo(video)
                        if (index == downloadsList.downloadVideos.size - 1) {
                            buildIndexMap()
                        }
                    }
                })
            }


        }
    }

    private fun fetchVideos(downloadedVideo: DownloadedVideo) {

    }

    private fun buildIndexMap() {
        indexMap.clear()
        if (downloadedVideos != null) {
            var index = 0
            for (video in downloadedVideos) {
                indexMap[video.videoId] = index++
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val listLdsItemBinding: ListDownloadItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.list_download_item, viewGroup, false)
        viewHolder = LandscapeItemRowHolder(listLdsItemBinding)
        return viewHolder as _root_ide_package_.me.vipa.app.activities.downloads.DownloadsAdapter.LandscapeItemRowHolder
    }

    override fun getItemCount(): Int {
        return downloadedVideos.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        setLandscapeData(viewHolder as _root_ide_package_.me.vipa.app.activities.downloads.DownloadsAdapter.LandscapeItemRowHolder, position)
    }

    private fun setLandscapeData(viewHolder: _root_ide_package_.me.vipa.app.activities.downloads.DownloadsAdapter.LandscapeItemRowHolder, position: Int) {
        val currentVideoItem = downloadedVideos[position]
        if (currentVideoItem.downloadType == _root_ide_package_.me.vipa.app.utils.MediaTypeConstants.getInstance().series) {
            try {
                val imgFile = File(Environment.getExternalStorageDirectory().absolutePath + "/Android/data/" + context.packageName + "/files/Download/${currentVideoItem.videoId}.jpg")
                val bm = BitmapFactory.decodeFile(imgFile.absolutePath)
                viewHolder.itemBinding.itemImage.setImageBitmap(bm)
                viewHolder.itemBinding.tvTitle.text = currentVideoItem.seriesName
            } catch (ex: Exception) {
                _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e(TAG, ex.message)
            }
        }
        downloadHelper.getDownloadStatus(currentVideoItem.videoId)
        downloadHelper.findOfflineVideoById(currentVideoItem.videoId, object : OfflineCallback<Video> {
            override fun onSuccess(p0: Video?) {
                viewHolder.itemBinding.root.tag = position
                viewHolder.itemBinding.videoItem = p0
                if (currentVideoItem.downloadType != _root_ide_package_.me.vipa.app.utils.MediaTypeConstants.getInstance().series) {
                    viewHolder.itemBinding.tvTitle.text = p0?.name
                }
                viewHolder.itemBinding.downloadedVideo = currentVideoItem
            }

            override fun onFailure(p0: Throwable?) {
            }
        })
        if (currentVideoItem.downloadType == _root_ide_package_.me.vipa.app.utils.MediaTypeConstants.getInstance().series) {
            viewHolder.itemBinding.flDeleteWatchlist.visibility = View.GONE
        }
        updateDownloadStatus(viewHolder, currentVideoItem, position)
        viewHolder.itemBinding.videoDownloaded.setOnClickListener { v -> deleteVideo(v!!, currentVideoItem, position) }
        viewHolder.itemBinding.videoDownloading.setOnClickListener { v ->
            if (currentVideoItem.downloadStatus==DownloadStatus.STATUS_PAUSED){
                downloadHelper.resumeDownload(currentVideoItem.videoId)
            }else{
                showPauseCancelPopUpMenu(v!!, currentVideoItem, position)
            }

        }
        viewHolder.itemBinding.pauseDownload.setOnClickListener {
            downloadHelper.resumeDownload(currentVideoItem.videoId)
        }
        viewHolder.itemBinding.root.setOnClickListener {
            if (currentVideoItem.downloadType != _root_ide_package_.me.vipa.app.utils.MediaTypeConstants.getInstance().series) {
                val downloadedVideoIntent = Intent(context, DownloadedVideoActivity::class.java)
                val status=downloadHelper.getCatalog().getVideoDownloadStatus(currentVideoItem.videoId)

                downloadHelper.findOfflineVideoById(currentVideoItem.videoId, object : OfflineCallback<Video> {
                    override fun onSuccess(p0: Video?) {
                        if (status.code== DownloadStatus.STATUS_COMPLETE){
                            _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e("isParcelable", (p0 is Parcelable).toString())
                            downloadedVideoIntent.putExtra("DownloadedVideoId", p0 as Parcelable)
                            context.startActivity(downloadedVideoIntent)
                        }

                    }

                    override fun onFailure(p0: Throwable?) {
                    }
                })
            } else {
                val downloadedEpisodesIntent = Intent(context, _root_ide_package_.me.vipa.app.activities.downloads.DownloadedEpisodesActivity::class.java)
                downloadedEpisodesIntent.putExtra("SeriesId", currentVideoItem.seriesId)
                downloadedEpisodesIntent.putExtra("SeriesName", currentVideoItem.seriesName)
                downloadedEpisodesIntent.putExtra("SeasonNumber", currentVideoItem.seasonNumber)
                context.startActivity(downloadedEpisodesIntent)
            }
        }
    }

    private fun updateDownloadStatus(viewHolder: _root_ide_package_.me.vipa.app.activities.downloads.DownloadsAdapter.LandscapeItemRowHolder, currentVideoItem: DownloadedVideo, position: Int) {
        downloadHelper.getCatalog().getVideoDownloadStatus(currentVideoItem.videoId,
                object : OfflineCallback<DownloadStatus> {
                    override fun onSuccess(p0: DownloadStatus?) {
                        if (currentVideoItem.downloadType == _root_ide_package_.me.vipa.app.utils.MediaTypeConstants.getInstance().series) {
                            if (currentVideoItem.episodeCount.toInt() == 1)
                                viewHolder.itemBinding.descriptionTxt.text = context.getString(R.string.season) + currentVideoItem.seasonNumber + " | " + currentVideoItem.episodeCount + context.getString(R.string.episode)
                            else
                                viewHolder.itemBinding.descriptionTxt.text = context.getString(R.string.season) + currentVideoItem.seasonNumber + " | " + currentVideoItem.episodeCount + context.getString(R.string.episodes)
                        } else {
                            when (p0?.code) {
                                DownloadStatus.STATUS_COMPLETE -> {
                                    viewHolder.itemBinding.downloadStatus = _root_ide_package_.me.vipa.app.enums.DownloadStatus.DOWNLOADED
                                    viewHolder.itemBinding.descriptionTxt.text = getFileSize(p0.actualSize)
                                }
                                DownloadStatus.STATUS_DOWNLOADING -> {
                                    viewHolder.itemBinding.downloadStatus = _root_ide_package_.me.vipa.app.enums.DownloadStatus.DOWNLOADING
                                    viewHolder.itemBinding.videoDownloading.progress = p0.progress.toFloat()
                                    viewHolder.itemBinding.descriptionTxt.text = "Downloading"
                                }
                                DownloadStatus.STATUS_PAUSED -> {
                                    viewHolder.itemBinding.downloadStatus = _root_ide_package_.me.vipa.app.enums.DownloadStatus.PAUSE
                                    viewHolder.itemBinding.descriptionTxt.text = "Paused"
                                }
                            }
                        }
                    }

                    override fun onFailure(p0: Throwable?) {
                    }
                })
    }

    fun getFileSize(size: Long): String {
        if (size <= 0)
            return "0"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

    internal fun notifyVideoChanged(videoId: String, status: DownloadStatus) {
        if (indexMap.containsKey(videoId)) {
            val index = indexMap[videoId]!!
            for (video in downloadedVideos) {
                if (videoId == video.videoId) {
                    downloadedVideos[index] = video
                    notifyItemChanged(index, status)
                }
            }
        }
    }

    override fun onDownloadStarted(p0: Video, p1: Long, p2: MutableMap<String, Serializable>) {
    }

    override fun onDownloadPaused(p0: Video, p1: DownloadStatus) {
        _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e(TAG, "onDownloadPaused" + p0.id)
        notifyVideoChanged(p0.id, p1)
    }

    override fun onDownloadFailed(p0: Video, p1: DownloadStatus) {
    }

    override fun onDownloadRequested(p0: Video) {
    }

    override fun onDownloadCanceled(p0: Video) {
        _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e("DownloadCancelled", "True")
    }

    override fun onDownloadDeleted(p0: Video) {

    }

    override fun onDownloadCompleted(p0: Video, p1: DownloadStatus) {
        notifyVideoChanged(p0.id, p1)
    }

    override fun onDownloadProgress(p0: Video, p1: DownloadStatus) {
        _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e(TAG, "onDownloadProgress" + p1.progress)
        notifyVideoChanged(p0.id, p1)
    }

    override fun downloadVideo(video: Video) {

    }

    override fun pauseVideoDownload(video: Video) {
        _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e(TAG, "pauseVideoDownload")
    }

    override fun resumeVideoDownload(video: Video) {
        _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e(TAG, "resumeVideoDownload")
    }

    override fun deleteVideo(video: Video) {
    }

    override fun alreadyDownloaded(video: Video) {
    }

    override fun downloadedVideos(p0: MutableList<out Video>?) {
    }

    override fun videoFound(video: Video?) {

    }

    override fun downloadStatus(videoId: String, downloadStatus: DownloadStatus?) {
        _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e(TAG, "downloadStatus" + downloadStatus?.progress)
    }

    inner class LandscapeItemRowHolder(internal val itemBinding: ListDownloadItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}