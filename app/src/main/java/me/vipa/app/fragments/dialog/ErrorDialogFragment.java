package me.vipa.app.fragments.dialog;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;


import me.vipa.app.R;
import me.vipa.app.databinding.ErrorPopUpBinding;


public class ErrorDialogFragment extends DialogFragment {
    private ErrorPopUpBinding errorPopUpBinding;
    private String errorMsg = "";

    private ErrorDialogFragment.CallBackErrorOkClick callBackListenerOkClick;

    public interface CallBackErrorOkClick {
        void okClick();


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {

            callBackListenerOkClick = (ErrorDialogFragment.CallBackErrorOkClick) getTargetFragment();
            Log.e( "onAttach: " , String.valueOf(callBackListenerOkClick));
        } catch (ClassCastException e) {
            Log.e("onAttachClassexception" , e.getMessage());
        }
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
        errorPopUpBinding = DataBindingUtil.inflate(inflater, R.layout.error_pop_up, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return errorPopUpBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            errorMsg = bundle.getString("errorMsg");
            errorPopUpBinding.tvError.setText(errorMsg);

        }
        setClicks();


    }

    private void setClicks() {
        errorPopUpBinding.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callBackListenerOkClick.okClick();
                getDialog().dismiss();
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

  /*  @Override
    public void onCancel(@NonNull @NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        kidPinPopupLayoutBinding.pinViewNumber.setText("");

    }*/

}




