package me.vipa.app.utils.helpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vipa.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomDialogFragment extends BottomSheetDialogFragment {

    public static BottomDialogFragment getInstance() {
        return new BottomDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.custom_bottom_sheet, container, false);

        view.findViewById(R.id.tvTitle).setOnClickListener(v -> {
// DO SOMETHING
        });
        return view;
    }

}