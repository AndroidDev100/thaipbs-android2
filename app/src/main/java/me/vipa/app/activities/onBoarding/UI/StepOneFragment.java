package me.vipa.app.activities.onBoarding.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import me.vipa.app.R;
import me.vipa.app.baseModels.BaseBindingFragment;
import me.vipa.app.databinding.FragmentStepOneBinding;

public class StepOneFragment extends BaseBindingFragment<FragmentStepOneBinding> {
    @Override
    public FragmentStepOneBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return FragmentStepOneBinding.inflate(inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_one, container, false);
    }
}