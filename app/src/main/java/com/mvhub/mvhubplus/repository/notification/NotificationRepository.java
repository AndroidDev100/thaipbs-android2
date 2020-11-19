package com.mvhub.mvhubplus.repository.notification;

import android.app.Activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.mvhub.mvhubplus.activities.notification.adapter.NotificationListAdapter;
import com.mvhub.mvhubplus.beanModel.beanModel.SingleItemModel;
import com.mvhub.mvhubplus.databinding.NotificationBinding;

import java.util.ArrayList;

public class NotificationRepository {
    private static NotificationRepository notificationRepository;

    public synchronized static NotificationRepository getInstance() {

        if (notificationRepository == null) {
            notificationRepository = new NotificationRepository();
        }
        return notificationRepository;
    }

    public void setAdapter(Activity context, NotificationBinding binding) {
        loadData(context, binding);
    }

    private void loadData(Activity activity, NotificationBinding binding) {
        ArrayList<SingleItemModel> singleItem = new ArrayList<>();
        for (int j = 0; j <= 10; j++) {
            singleItem.add(new SingleItemModel("Item " + j, "URL " + j));
        }
        //binding.recyclerView.setHasFixedSize(true);
        NotificationListAdapter notificationListAdapter = new NotificationListAdapter(activity, singleItem);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        binding.recyclerView.setAdapter(notificationListAdapter);
    }

}
