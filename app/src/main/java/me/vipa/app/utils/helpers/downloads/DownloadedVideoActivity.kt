package me.vipa.app.utils.helpers.downloads

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import com.brightcove.player.model.Video
import me.vipa.brightcovelibrary.BrightcovePlayerFragment
import com.google.gson.Gson
import com.vipa.app.R
import com.vipa.app.databinding.ActivityDownloadedVideoBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DownloadedVideoActivity : _root_ide_package_.me.vipa.app.baseModels.BaseBindingActivity<ActivityDownloadedVideoBinding>(), _root_ide_package_.me.vipa.brightcovelibrary.BrightcovePlayerFragment.OnPlayerInteractionListener {
    override fun onFragmentInteraction(uri: Uri?) {

    }

    override fun onPlayerError(error: String?) {
    }

    override fun onBookmarkCall(currentPosition: Int) {
    }

    override fun onBookmarkFinish() {
    }

    override fun onPlayerStart() {
    }

    override fun onAdStarted() {
    }


    private var TAG = this.javaClass.simpleName
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        var intentData = intent.getParcelableExtra<Video>("DownloadedVideoId")
        _root_ide_package_.me.vipa.app.utils.cropImage.helpers.Logger.e(TAG, Gson().toJson(intentData))
        var transaction = supportFragmentManager.beginTransaction()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        var playerFragment = _root_ide_package_.me.vipa.brightcovelibrary.BrightcovePlayerFragment()
        val args = Bundle()
        args.putBoolean("isOffline", true)
        args.putParcelable(_root_ide_package_.me.vipa.app.utils.constants.AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, intentData as Parcelable)
        playerFragment.arguments = args
        transaction.add(R.id.playerFragmentFrame, playerFragment).commit()
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): ActivityDownloadedVideoBinding {
        return ActivityDownloadedVideoBinding.inflate(inflater)

    }
}
