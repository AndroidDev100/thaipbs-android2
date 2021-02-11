package me.vipa.app.activities.onBoarding.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.vipa.app.R;
import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpActivity;
import me.vipa.app.baseModels.BaseBindingFragment;
import me.vipa.app.databinding.FragmentStepTwoBinding;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

public class StepTwoFragment extends BaseBindingFragment<FragmentStepTwoBinding> {
    @Override
    public FragmentStepTwoBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return FragmentStepTwoBinding.inflate(inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClickListeners();
    }

    private void setClickListeners() {
        getBinding().skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityLauncher(getActivity()).homeScreen(getActivity(), HomeActivity.class);
            }
        });

        getBinding().register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityLauncher(getActivity()).signUpActivity(getActivity(), SignUpActivity.class, "OnBoarding");
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
