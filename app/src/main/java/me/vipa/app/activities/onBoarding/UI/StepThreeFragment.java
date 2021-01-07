package me.vipa.app.activities.onBoarding.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import me.vipa.app.R;
import me.vipa.app.baseModels.BaseBindingFragment;
import me.vipa.app.databinding.FragmentStepThreeBinding;
import me.vipa.app.databinding.FragmentStepTwoBinding;

public class StepThreeFragment extends BaseBindingFragment<FragmentStepThreeBinding> {
    @Override
    public FragmentStepThreeBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return FragmentStepThreeBinding.inflate(inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_three, container, false);
    }
}
