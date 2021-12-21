package me.vipa.app.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moengage.inbox.core.MoEInboxHelper
import com.moengage.inbox.core.model.InboxMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.vipa.app.MvHubPlusApplication

object MoEngageNotificationManager {

    private val mUnreadNotificationCountLiveData: MutableLiveData<Int> = MutableLiveData()
    private val mNotificationsLiveData: MutableLiveData<List<InboxMessage>> = MutableLiveData()

    fun getUnreadCount(): LiveData<Int> {
        CoroutineScope(Dispatchers.IO).launch {
            getUnreadNotificationCount()
        }
        return mUnreadNotificationCountLiveData
    }

    private suspend fun getUnreadNotificationCount() = withContext(Dispatchers.IO) {
        val count = MoEInboxHelper.getInstance().getUnClickedMessagesCount(MvHubPlusApplication.getInstance())
        mUnreadNotificationCountLiveData.postValue(count.toInt())
    }
}