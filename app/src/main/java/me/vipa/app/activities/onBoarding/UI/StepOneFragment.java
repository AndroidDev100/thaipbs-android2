package me.vipa.app.activities.onBoarding.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.vipa.app.baseModels.BaseBindingFragment;
import me.vipa.app.databinding.FragmentStepOneBinding;

public class StepOneFragment extends BaseBindingFragment<FragmentStepOneBinding> {
    @Override
    public FragmentStepOneBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return FragmentStepOneBinding.inflate(inflater);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // setClickListeners();
    }

//    private void setClickListeners() {
//        getBinding().skipLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityLauncher.getInstance().homeScreen(getActivity(), HomeActivity.class);
//            }
//        });
//        getBinding().register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ActivityLauncher.getInstance().signUpActivity(getActivity(), SignUpActivity.class, "OnBoarding");
//
//            }
//        });
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}