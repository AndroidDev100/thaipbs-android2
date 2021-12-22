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
import kotlin.coroutines.CoroutineContext

object MoEngageNotificationManager {

    private val mUnreadNotificationCountLiveData: MutableLiveData<Int> = MutableLiveData()
    private val mNotificationsLiveData: MutableLiveData<List<InboxMessage>> = MutableLiveData()

    fun getUnreadCount(): LiveData<Int> {
        CoroutineScope(Dispatchers.IO).launch {
            getUnreadNotificationCount(this.coroutineContext)
        }
        return mUnreadNotificationCountLiveData
    }

    private suspend fun getUnreadNotificationCount(coroutineContext: CoroutineContext) =
        withContext(coroutineContext) {
            val count = MoEInboxHelper.getInstance()
                .getUnClickedMessagesCount(MvHubPlusApplication.getInstance())
            mUnreadNotificationCountLiveData.postValue(count.toInt())
        }

    private fun refreshNotificationCount() {
        getUnreadCount()
    }

    fun getAllNotifications(): LiveData<List<InboxMessage>> {
        CoroutineScope(Dispatchers.IO).launch {
            getAllNotificationList(this.coroutineContext)
            getUnreadNotificationCount(this.coroutineContext)
        }
        return mNotificationsLiveData
    }

    private suspend fun getAllNotificationList(coroutineContext: CoroutineContext) =
        withContext(coroutineContext) {
            mNotificationsLiveData.postValue(
                MoEInboxHelper.getInstance().fetchAllMessages(MvHubPlusApplication.getInstance())
            )

        }

    fun markAsRead(inboxMessage: InboxMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            MoEInboxHelper.getInstance().trackMessageClicked(MvHubPlusApplication.getInstance(), inboxMessage)
            refreshNotificationCount()
        }
    }

    fun deleteNotification(inboxMessage: InboxMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            MoEInboxHelper.getInstance().deleteMessage(MvHubPlusApplication.getInstance(), inboxMessage)
            refreshNotificationCount()
        }
    }

    fun deleteAllNotifications(inboxMessages: List<InboxMessage>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (message in inboxMessages) {
                MoEInboxHelper.getInstance().deleteMessage(MvHubPlusApplication.getInstance(), message)
            }
            refreshNotificationCount()
        }
    }

}