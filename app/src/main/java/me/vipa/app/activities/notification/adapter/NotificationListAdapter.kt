package me.vipa.app.activities.notification.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moengage.core.internal.MoEConstants
import com.moengage.inbox.core.model.InboxMessage
import me.vipa.app.R
import me.vipa.app.databinding.ListItemNotificationBinding
import me.vipa.brightcovelibrary.Logger
import me.vipa.brightcovelibrary.utils.ObjectHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationListAdapter :
    RecyclerView.Adapter<NotificationListAdapter.NotificationItemVH>() {

    private val data: ArrayList<InboxMessage> = ArrayList()
    var listener: OnItemClickListener? = null

    fun getDataList() = data.toList()

    fun updateItem(item: InboxMessage) {
        val idx = data.indexOf(item)
        if (idx > -1 && !data[idx].isClicked) {
            data[idx].isClicked = true
            notifyItemChanged(idx)
        }
    }

    fun deleteItem(item: InboxMessage) {
        val idx = data.indexOf(item)
        if (idx > -1) {
            data.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }

    fun setupData(data: List<InboxMessage>) {
        this.data.clear()
        this.data.addAll(data)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationListAdapter.NotificationItemVH {
        val binding = DataBindingUtil.inflate<ListItemNotificationBinding>(
            LayoutInflater.from(parent.context),
            R.layout.list_item_notification,
            parent,
            false
        )
        return NotificationItemVH(binding)
    }

    override fun onBindViewHolder(holder: NotificationItemVH, position: Int) {
        holder.setInfo(data[position])
    }

    override fun getItemCount(): Int {
        return ObjectHelper.getSize(data)
    }

    inner class NotificationItemVH(private val binding: ListItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setInfo(message: InboxMessage) {
            Logger.d("payload: ${message.payload}")
            binding.tvTitle.text = message.payload.getString(MoEConstants.PUSH_NOTIFICATION_TITLE)
            binding.tvMessage.text = message.payload.getString(MoEConstants.PUSH_NOTIFICATION_MESSAGE)

            val receivedTime = message.payload.getLong("MOE_MSG_RECEIVED_TIME")
            if (DateUtils.isToday(receivedTime)) {
                binding.tvDate.setText(R.string.today)
            } else {
                val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                binding.tvDate.text = sdf.format(receivedTime)
            }

            val imageUrl = message.payload.optString(MoEConstants.PUSH_NOTIFICATION_IMAGE_URL)
            if (ObjectHelper.isEmpty(imageUrl)) {
                binding.ivBanner.visibility = View.GONE
            } else {
                binding.ivBanner.visibility = View.VISIBLE
                Glide.with(binding.ivBanner.context).load(imageUrl)
                    .placeholder(R.drawable.placeholder_landscape)
                    .error(R.drawable.placeholder_landscape)
                    .into(binding.ivBanner)
            }
            binding.root.setOnClickListener { listener?.onItemClicked(message) }
            binding.ivDelete.setOnClickListener { listener?.onDeleteClicked(message) }

            if (message.isClicked) {
                styleReadMsg()
            } else {
                styleUnreadMsg()
            }
        }

        private fun styleReadMsg() {
            binding.tvTitle.setTextColor(ContextCompat.getColor(binding.tvTitle.context, R.color.sub_heading))
            binding.tvMessage.setTextColor(ContextCompat.getColor(binding.tvMessage.context, R.color.grey_heading))
            binding.tvDate.setTextColor(ContextCompat.getColor(binding.tvDate.context, R.color.sub_heading))
            binding.ivDelete.setColorFilter(ContextCompat.getColor(binding.ivDelete.context, R.color.grey_heading))
        }

        private fun styleUnreadMsg() {
            binding.tvTitle.setTextColor(ContextCompat.getColor(binding.tvTitle.context, R.color.white))
            binding.tvMessage.setTextColor(ContextCompat.getColor(binding.tvMessage.context, R.color.white))
            binding.tvDate.setTextColor(ContextCompat.getColor(binding.tvDate.context, R.color.white))
            binding.ivDelete.setColorFilter(ContextCompat.getColor(binding.ivDelete.context, R.color.white))
        }

    }

    interface OnItemClickListener {
        fun onItemClicked(inboxMessage: InboxMessage)
        fun onDeleteClicked(inboxMessage: InboxMessage)
    }
}
