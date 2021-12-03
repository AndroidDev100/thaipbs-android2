package me.vipa.brightcovelibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vipa.brightcovelibrary.R
import me.vipa.brightcovelibrary.utils.ObjectHelper

class VideoSettingAdapter(val data: List<VideoSettingItem>): RecyclerView.Adapter<VideoSettingAdapter.SettingViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_setting, parent, false)
        return SettingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.initData(data[position])
    }

    override fun getItemCount(): Int {
        return ObjectHelper.getSize(data)
    }

    inner class SettingViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tv_title)

        fun initData(item: VideoSettingItem) {
            tvTitle.text = item.title
            tvTitle.setOnClickListener { itemClickListener?.onClick(item) }
        }
    }

    interface OnItemClickListener {
        fun onClick(item: VideoSettingItem)
    }
}

data class VideoSettingItem(val title: String)