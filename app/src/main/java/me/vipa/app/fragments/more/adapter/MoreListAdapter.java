package me.vipa.app.fragments.more.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.R;
import me.vipa.app.callbacks.commonCallbacks.MoreItemClickListener;
import me.vipa.app.databinding.MoreItemBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.DrawableHelper;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.List;

public class MoreListAdapter extends RecyclerView.Adapter<MoreListAdapter.ViewHolder> {
    final MoreItemClickListener itemClickListener;
    private final List<String> itemsList;
    private final Activity mContext;
    private final boolean islogin;
    private long mLastClickTime;

    public MoreListAdapter(Activity context, List<String> itemsList, MoreItemClickListener call, boolean islogin) {
        this.itemsList = itemsList;
        this.mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        this.itemClickListener = call;
        this.islogin = islogin;
    }

    @NonNull
    @Override
    public MoreListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MoreItemBinding moreItemBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.more_item, viewGroup, false);
        return new ViewHolder(moreItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.moreItemBinding.moreListTitle.setText("" + itemsList.get(i));
        setIcons(holder.moreItemBinding.moreListIcon, i, holder.moreItemBinding);
        if (i == 2 || i == 7 || i == 12){
            holder.itemView.findViewById(R.id.view_line).setVisibility(View.VISIBLE);
        }else {
            holder.itemView.findViewById(R.id.view_line).setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setIcons(ImageView v, int i, MoreItemBinding binding) {

        if (islogin) {

            if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {

                switch (i) {
                    case 0:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.profile_icon, v);
                        break;
                    case 1:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.watch_list, v);
                        break;
                    case 2:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.watchhistory_icon, v);
                        break;
                    /*case 4:
                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.ic_save_alt, v);
                        break;*/
//                    case 4:
//                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.vipa_kids, v);
//                        break;
                    case 3:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.contact_us, v);
                        break;
                    case 4:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.frequently_ask_question, v);
                        break;
                    case 5:
                        //callDrawableHelper1(mContext, R.color.more_icon_color, R.drawable.about_vipa, v);
                        v.setBackgroundResource(R.drawable.about_vipa);
                        break;

                    case 6:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.feedback, v);
                        break;
                    case 7:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.other_application, v);
                        break;
                    case 8:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.setting_icon, v);
                        break;
                    case 9:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.t_and_c_more, v);
                        break;
                    case 10:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.privacy, v);
                        break;
                    case 11:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.log_out, v);
                        break;
//                    case 15:
//                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.veryfy_user, v);
//                        binding.verifyAccount.setVisibility(View.VISIBLE);
//                        break;
//
//                    case 16:
//                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.notification, v);
//                        break;


                    default:

                        break;
                }
            } else {

                switch (i) {
                    case 0:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.profile_icon, v);
                        break;
                    case 1:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.watch_list, v);
                        break;
                    case 2:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.watchhistory_icon, v);
                        break;
                    /*case 4:
                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.ic_save_alt, v);
                        break;*/
//                    case 4:
//                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.vipa_kids, v);
//                        break;
                    case 3:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.contact_us, v);
                        break;
                    case 4:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.frequently_ask_question, v);
                        break;
                    case 5:
                        //callDrawableHelper1(mContext, R.color.more_icon_color, R.drawable.about_vipa, v);
                        v.setBackgroundResource(R.drawable.about_vipa);
                        break;

                    case 6:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.feedback, v);
                        break;
                    case 7:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.other_application, v);
                        break;
                    case 8:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.setting_icon, v);
                        break;
                    case 9:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.t_and_c_more, v);
                        break;
                    case 10:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.privacy, v);
                        break;
                    case 11:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.log_out, v);
                        break;
//                    case 15:
//                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.veryfy_user, v);
//                        binding.verifyAccount.setVisibility(View.VISIBLE);
//                        break;
//
//                    case 16:
//                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.notification, v);
//                        break;


                    default:

                        break;
                }
            }
        } else {
            if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
                switch (i) {
                    case 0:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.profile_icon, v);
                        break;
                    case 1:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.watch_list, v);
                        break;
                    case 2:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.watchhistory_icon, v);
                        break;
                    /*case 4:
                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.ic_save_alt, v);
                        break;*/
//                    case 4:
//                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.vipa_kids, v);
//                        break;
                    case 3:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.contact_us, v);
                        break;
                    case 4:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.frequently_ask_question, v);
                        break;
                    case 5:
                        //callDrawableHelper1(mContext, R.color.more_icon_color, R.drawable.about_vipa, v);
                        v.setBackgroundResource(R.drawable.about_vipa);
                        break;

                    case 6:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.feedback, v);
                        break;
                    case 7:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.other_application, v);
                        break;
                    case 8:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.setting_icon, v);
                        break;
                    case 9:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.t_and_c_more, v);
                        break;
                    case 10:
                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.privacy, v);
                        break;
//                    case 15:
//                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.veryfy_user, v);
//                        binding.verifyAccount.setVisibility(View.VISIBLE);
//                        break;
//
//                    case 16:
//                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.notification, v);
//                        break;


                    default:

                        break;
                }
            } else {
                switch (i) {
                    case 0:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.profile_icon, v);
                        break;
                    case 1:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.watch_list, v);
                        break;
                    case 2:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.watchhistory_icon, v);
                        break;
                    /*case 4:
                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.ic_save_alt, v);
                        break;*/
//                    case 4:
//                        callDrawableHelper(mContext, R.color.more_icon_color, R.drawable.vipa_kids, v);
//                        break;
                    case 3:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.contact_us, v);
                        break;
                    case 4:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.frequently_ask_question, v);
                        break;
                    case 5:
                        //callDrawableHelper1(mContext, R.color.more_icon_color, R.drawable.about_vipa, v);
                        v.setBackgroundResource(R.drawable.about_vipa);
                        break;

                    case 6:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.feedback, v);
                        break;
                    case 7:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.other_application, v);
                        break;
                    case 8:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.setting_icon, v);
                        break;
                    case 9:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.t_and_c_more, v);
                        break;
                    case 10:
                        callDrawableHelper(mContext, R.color.icons_color_dark, R.drawable.privacy, v);
                        break;
//                    case 15:
//                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.veryfy_user, v);
//                        binding.verifyAccount.setVisibility(View.VISIBLE);
//                        break;
//
//                    case 16:
//                        callDrawableHelper(mContext, R.color.icons_color, R.drawable.notification, v);
//                        break;


                    default:

                        break;
                }
            }
        }

    }

    public void callDrawableHelper(Context context, int mColor, int mDrawable, ImageView imageView) {
        DrawableHelper
                .withContext(context)
                .withColor(mColor)
                .withDrawable(mDrawable)
                .tint()
                .applyTo(imageView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final MoreItemBinding moreItemBinding;

        public ViewHolder(MoreItemBinding view) {
            super(view.getRoot());
            moreItemBinding = view;
            moreItemBinding.getRoot().setOnClickListener(view1 -> {
                if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी") ){
                    AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
                } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")){
                    AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
                }
                Logger.e("Caption", itemsList.get(getLayoutPosition()) + mContext.getResources().getString(R.string.redeem_coupon));
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.change_password))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.change_password));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.manage_account))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.manage_account));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.setting_title))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.setting_title));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.term_condition))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.term_condition));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.notification))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.notification));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.privacy_policy))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.privacy_policy));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.verify_user))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.verify_user));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.my_watchlist))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.my_watchlist));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.my_history))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.my_history));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.my_downloads))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.my_downloads));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.sign_out))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.sign_out));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.membership_plan))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.membership_plan));
                } else if (itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.redeem_coupon))) {
                    itemClickListener.onClick(mContext.getResources().getString(R.string.redeem_coupon));
                }else if(itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.other_application))){
                    itemClickListener.onClick(mContext.getResources().getString(R.string.other_application));
                }else if(itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.contact_us))){
                    itemClickListener.onClick(mContext.getResources().getString(R.string.contact_us));
                }else if(itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.faq))){
                    itemClickListener.onClick(mContext.getResources().getString(R.string.faq));
                }else if(itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.about_us))){
                    itemClickListener.onClick(mContext.getResources().getString(R.string.about_us));
                }else if(itemsList.get(getLayoutPosition()).equalsIgnoreCase(mContext.getResources().getString(R.string.feedback))){
                    itemClickListener.onClick(mContext.getResources().getString(R.string.feedback));
                }

            });
        }

    }

}
