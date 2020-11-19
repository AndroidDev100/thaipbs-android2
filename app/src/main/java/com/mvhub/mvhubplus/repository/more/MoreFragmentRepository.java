package com.mvhub.mvhubplus.repository.more;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvhub.mvhubplus.beanModel.beanModel.SectionDataModel;
import com.mvhub.mvhubplus.beanModel.params.ParamBean;
import com.mvhub.mvhubplus.callbacks.commonCallbacks.NoInternetConnectionCallBack;
import com.mvhub.mvhubplus.databinding.FragmentMoreBinding;
import com.mvhub.mvhubplus.utils.helpers.NoInternetConnection;

import java.util.ArrayList;

public class MoreFragmentRepository {
    private static MoreFragmentRepository projectRepository;
    Activity activity;
    ArrayList<SectionDataModel> allSampleData;

    public synchronized static MoreFragmentRepository getInstance() {
        if (projectRepository == null) {
            projectRepository = new MoreFragmentRepository();
        }
        return projectRepository;
    }

    public void setAdapter(ParamBean mParam, Activity context, FragmentMoreBinding binding) {
        connectionCheck(context, binding);
    }


    private void connectionCheck(final Activity context, final FragmentMoreBinding binding) {
        new NoInternetConnection(context).hanleAction(binding.connection, new NoInternetConnectionCallBack() {
            @Override
            public void isOnline(boolean connected) {
                binding.noConnectionLayout.setVisibility(View.GONE);
                //loadData(context,binding);

            }

            @Override
            public void isOffline(boolean disconnected) {
                binding.noConnectionLayout.setVisibility(View.VISIBLE);
                binding.connection.retryTxt.setOnClickListener(view -> connectionCheck(context, binding));
            }
        });

    }


    private void loadData(Activity context, FragmentMoreBinding binding) {
        allSampleData = new ArrayList<>();
        // binding.myRecyclerView.setNestedScrollingEnabled(false);
        //   binding.myRecyclerView.setHasFixedSize(true);
        //callScroll(binding.myRecyclerView);
        //CommonAdapter adapter = new CommonAdapter(context, allSampleData,slides);
        //   binding.myRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        //  binding.myRecyclerView.setAdapter(adapter);
    }

    private void callScroll(RecyclerView myRecyclerView) {

        myRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent e) {
                int action = e.getAction();
                // Toast.makeText(activity,"HERE",Toast.LENGTH_SHORT).show();
                switch (action) {
                    case MotionEvent.ACTION_POINTER_UP:
                        recyclerView.getParent().requestDisallowInterceptTouchEvent(true);

                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
    }


}
