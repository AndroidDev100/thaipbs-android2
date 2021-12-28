package me.vipa.app.activities.notification.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.moengage.inbox.core.model.InboxMessage
import kotlinx.android.synthetic.main.purchase.*
import kotlinx.android.synthetic.main.toolbar.view.*
import me.vipa.app.R
import me.vipa.app.activities.notification.adapter.NotificationListAdapter
import me.vipa.app.activities.notification.viewmodel.NotificationViewModel
import me.vipa.app.baseModels.BaseBindingActivity
import me.vipa.app.databinding.ActivityNotificationBinding
import me.vipa.app.fragments.dialog.AlertDialogFragment
import me.vipa.app.manager.MoEngageNotificationManager
import me.vipa.app.utils.helpers.NetworkConnectivity
import me.vipa.app.utils.helpers.ToolBarHandler
import me.vipa.brightcovelibrary.Logger
import me.vipa.brightcovelibrary.utils.ObjectHelper
import org.json.JSONObject
import java.util.*

class NotificationActivity : BaseBindingActivity<ActivityNotificationBinding?>() {
    private var viewModel: NotificationViewModel? = null
    private var notificationAdapter: NotificationListAdapter? = null

    private var itemClickListener: NotificationListAdapter.OnItemClickListener? =
        object : NotificationListAdapter.OnItemClickListener {
            override fun onItemClicked(inboxMessage: InboxMessage) {
                MoEngageNotificationManager.markAsRead(inboxMessage)
                notificationAdapter?.updateItem(inboxMessage)

                Logger.d("clicked on : $inboxMessage")
                val assetType: String? = inboxMessage.payload.optString("contentType")
                val assetId: String? = inboxMessage.payload.optString("id")
                val seriesId: String? = inboxMessage.payload.optString("seriesId")
                val seasonNumber: String? = inboxMessage.payload.optString("seasonNumber")

                Logger.d("assetType: $assetType | assetId: $assetId | seriesId: $seriesId | seasonNumber: $seasonNumber")

                val json = JSONObject()
                json.apply {
                    put("contentType", assetType)
                    put("id", assetId)
                    put("seriesId", seriesId)
                    put("seasonNumber", seasonNumber)
                }

                branchRedirections(json)
            }

            override fun onDeleteClicked(inboxMessage: InboxMessage) {
                showAlertDialog(
                    message = getString(R.string.delete_notification_single),
                    positiveLabel = getString(R.string.ok),
                    positiveAction = {
                        MoEngageNotificationManager.deleteNotification(inboxMessage)
                        notificationAdapter?.deleteItem(inboxMessage)
                    },
                    negativeLabel = getString(R.string.cancel),
                )
            }
        }

    override fun inflateBindingLayout(inflater: LayoutInflater): ActivityNotificationBinding {
        return ActivityNotificationBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        notificationAdapter = NotificationListAdapter()

        setupToolbarIcons()
        setupListener()

        if (NetworkConnectivity.isOnline(this@NotificationActivity)) {
            getNotifications()
        } else {
            showNoNetwork()
        }
    }

    override fun onDestroy() {
        itemClickListener = null
        notificationAdapter = null
        binding?.toolbar?.clDelete?.setOnClickListener(null)
        super.onDestroy()
    }

    private fun setupToolbarIcons() {
        binding?.toolbar?.let {
            it.titleText.screen_text.setText(R.string.notification)
            it.titleText.visibility = View.VISIBLE
            it.backLayout.visibility = View.VISIBLE
            it.clNotification.visibility = View.GONE
            it.searchIcon.visibility = View.GONE

            ToolBarHandler(this@NotificationActivity).setNotificationAction(it)
        }
    }

    private fun setupListener() {
        binding?.toolbar?.clDelete?.setOnClickListener {
            notificationAdapter?.getDataList()?.let { dataList ->
                showAlertDialog(
                    message = getString(R.string.delete_notification_all),
                    positiveLabel = getString(R.string.ok),
                    positiveAction = {
                        MoEngageNotificationManager.deleteAllNotifications(dataList)
                        notificationAdapter?.clearData()
                        initNoData(false)
                    },
                    negativeLabel = getString(R.string.cancel),
                )
            }
        }
    }

    private fun getNotifications() {
        MoEngageNotificationManager.getAllNotifications().observe(this) { list ->
            binding?.noConnectionLayout?.visibility = View.GONE

            val hasData = ObjectHelper.isNotEmpty(list)
            initNoData(hasData)

            if (hasData) {
                initRecyclerView(list)
            }
        }
    }

    private fun showNoNetwork() {
        binding?.noConnectionLayout?.visibility = View.VISIBLE
        binding?.noConnectionLayout?.findViewById<View>(R.id.retry_txt)
            ?.setOnClickListener { getNotifications() }
    }

    private fun initNoData(hasData: Boolean) {
        if (hasData) {
            binding?.grpNoData?.visibility = View.GONE
            binding?.rvNotification?.visibility = View.VISIBLE
            binding?.toolbar?.clDelete?.visibility = View.VISIBLE
        } else {
            binding?.grpNoData?.visibility = View.VISIBLE
            binding?.rvNotification?.visibility = View.GONE
            binding?.toolbar?.clDelete?.visibility = View.GONE
        }
    }

    private fun initRecyclerView(list: List<InboxMessage>) {
        notificationAdapter?.setupData(list)
        notificationAdapter?.listener = itemClickListener

        binding?.rvNotification?.let { rv ->
            val linearLayoutManager = LinearLayoutManager(rv.context)
            rv.layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(
                rv.context,
                linearLayoutManager.orientation
            )
            dividerItemDecoration.setDrawable(
                ColorDrawable(ContextCompat.getColor(rv.context, R.color.notification_divider))
            )
            rv.addItemDecoration(dividerItemDecoration)
            rv.adapter = notificationAdapter
        }
    }

    private fun showAlertDialog(
        message: String,
        positiveLabel: String,
        positiveAction: () -> Unit,
        negativeLabel: String,
    ) {
        val alertDialog = AlertDialogFragment.newInstance("", message, positiveLabel, negativeLabel)
        alertDialog.setAlertDialogCallBack {
            positiveAction()
            initNoData((notificationAdapter?.itemCount ?: 0) > 0)
        }
        alertDialog.show(Objects.requireNonNull(supportFragmentManager), "fragment_notification")
    }

}