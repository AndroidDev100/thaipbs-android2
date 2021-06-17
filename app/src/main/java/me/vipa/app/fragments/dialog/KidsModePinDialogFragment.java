package me.vipa.app.fragments.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import me.vipa.app.R;
import me.vipa.app.databinding.KidPinPopupLayoutBinding;

public class KidsModePinDialogFragment extends DialogFragment {
    private KidPinPopupLayoutBinding kidPinPopupLayoutBinding;
    private static KidsModePinDialogFragment mInstance;
    public static KidsModePinDialogFragment getInstance(){
        if(mInstance == null){
            mInstance = new KidsModePinDialogFragment();
        }
        return mInstance;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        if (getDialog() != null) {
            getDialog().getWindow().getAttributes().width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().getAttributes().height = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        }
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        kidPinPopupLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.kid_pin_popup_layout,container,false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return kidPinPopupLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClicks();




    }

    private void setClicks() {
       kidPinPopupLayoutBinding.llContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        /*        if (!TextUtils.isEmpty(kidPinPopupLayoutBinding.pinViewNumber.getText())) {
                    if (kidPinPopupLayoutBinding.pinViewNumber.getSelectionEnd() == 4) {
                        pinOtp = String.valueOf(getBinding().pinViewOtp.getText());


                    }
                    else {
                        getBinding().llErrorPin.setVisibility(View.VISIBLE);
                    }
                } else {
                    try {
                        String selectedLanguage = AppPrefManager.getInstance().getAppLanguage();
                        if (selectedLanguage.equalsIgnoreCase(Constants.LANGUAGE_ENGLISH)) {
                            getBinding().llErrorPin.setVisibility(View.VISIBLE);

                        } else {
                            getBinding().llErrorPin.setVisibility(View.VISIBLE);

                        }

                    } catch (Exception exc) {

                    }
                }*/



            }
        });
        kidPinPopupLayoutBinding.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kidPinPopupLayoutBinding.pinViewNumber.setText("");
                dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCancel(@NonNull @NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        kidPinPopupLayoutBinding.pinViewNumber.setText("");

    }
}



