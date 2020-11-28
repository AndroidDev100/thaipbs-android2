package me.vipa.app.activities.membershipplans.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.purchase.ui.VodOfferType;
import me.vipa.app.beanModel.purchaseModel.PurchaseModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import me.vipa.app.utils.cropImage.helpers.Logger;

import java.util.List;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.PurchaseViewHolder> {


    private final Context context;
    private final List<PurchaseModel> list;
    private final OnPurchaseItemClick fragmentClickNetwork;
    private int rowIndex = -1;
    private List<String> subscribedPlan;
    private boolean isClickable;
    private String localeCurrency;

    public MembershipAdapter(Context context, List<PurchaseModel> list, OnPurchaseItemClick purchaseActivity, boolean isClickable, String localeCurrency) {
        this.context = context;
        this.list = list;
        this.fragmentClickNetwork = purchaseActivity;
        //this.subscribedPlan=subscribedPlans;
        this.isClickable = isClickable;
        this.localeCurrency = localeCurrency;
    }


    public void notifyAdapter(List<PurchaseModel> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_purchase, parent, false);

        return new PurchaseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        if (list.get(position).isSelected()) {
            rowIndex = position;
        }

        try {
            if (list.get(position).getPurchaseOptions().equalsIgnoreCase(VodOfferType.RECURRING_SUBSCRIPTION​.name())) {

                holder.title.setText("" + list.get(position).getTitle());
                holder.description.setText("" + context.getResources().getString(R.string.subscribe_to));
                holder.currency_price.setText("" + list.get(position).getCurrency()+" "+list.get(position).getPrice());
                if (list.get(position).getOfferPeriod().equalsIgnoreCase(VodOfferType.MONTHLY.name())){
                    holder.plan_type.setText("" + context.getResources().getString(R.string.montly));
                }else if (list.get(position).getOfferPeriod().equalsIgnoreCase(VodOfferType.QUARTERLY.name())){
                    holder.plan_type.setText("" + context.getResources().getString(R.string.quarterly));
                }
                else if (list.get(position).getOfferPeriod().equalsIgnoreCase(VodOfferType.HALF_YEARLY.name())){
                    holder.plan_type.setText("" + context.getResources().getString(R.string.half_yearly));
                }
                else if (list.get(position).getOfferPeriod().equalsIgnoreCase(VodOfferType.ANNUAL.name())){
                    holder.plan_type.setText("" + context.getResources().getString(R.string.annual));
                }

            }

            if (!isClickable && (rowIndex == position)) {
              //  holder.title.setText(context.getResources().getString(R.string.buy_subscription));
            } else {
              //  holder.title.setText(context.getResources().getString(R.string.buy_subscription));
            }
            Logger.d("priceIs", "" + list.get(position).getPrice());

        }catch (Exception e){

        }

       /* if (list.get(position).getPurchaseOptions().equalsIgnoreCase("free")) {
            String price = list.get(position).getPrice();
            holder.charges.setVisibility(View.GONE);
            holder.tvPrice.setText("0");
            holder.idr.setText("" + localeCurrency);

            holder.title.setText(context.getResources().getString(R.string.subscribe_to));
            holder.boldtext.setText(context.getResources().getString(R.string.free_plan));
            if (isClickable) {
                holder.title.setText(context.getResources().getString(R.string.subscribed_to));
            } else {
                holder.title.setText(context.getResources().getString(R.string.subscribe_to));
            }
            Logger.d("priceIs", "" + list.get(position).getPrice());

        } else if (list.get(position).getPurchaseOptions().equalsIgnoreCase("Monthly")) {

            holder.idr.setText("" + localeCurrency);
            holder.tvPrice.setText("" + list.get(position).getPrice());
            holder.charges.setText(context.getResources().getString(R.string.per_Month));
            holder.boldtext.setText(context.getResources().getString(R.string.monthly_pack));
            if (!isClickable && (rowIndex == position)) {
                holder.title.setText(context.getResources().getString(R.string.subscribed_to));
            } else {
                holder.title.setText(context.getResources().getString(R.string.subscribe_to));
            }

        } else if (list.get(position).getPurchaseOptions().equalsIgnoreCase("Weekly Pack")) {
            holder.idr.setText("" + localeCurrency);

            holder.tvPrice.setText("" + list.get(position).getPrice());
            holder.charges.setText(context.getResources().getString(R.string.per_Week));
            holder.boldtext.setText(context.getResources().getString(R.string.weekly_pack));
            if (!isClickable && (rowIndex == position)) {
                holder.title.setText(context.getResources().getString(R.string.subscribed_to));
            } else {
                holder.title.setText(context.getResources().getString(R.string.subscribe_to));
            }
        }*//*else if (list.get(position).getPurchaseOptions().equalsIgnoreCase("Daily")) {

            holder.title.setText(context.getResources().getString(R.string.subscribed_daily_pack));
            holder.tvPrice.setText("" + list.get(position).getPrice());
            holder.charges.setText(context.getResources().getString(R.string.per_day));
            holder.boldtext.setText(context.getResources().getString(R.string.daily_pack));

        }*/


        holder.cardView.setOnClickListener(view -> {
            if (isClickable) {
                boolean isCurrencySupported=false;
                for (int i = 0 ; i < SDKConfig.getInstance().getSupportedCurrencies().size() ; i++){
                    if (SDKConfig.getInstance().getSupportedCurrencies().get(i).equalsIgnoreCase(list.get(position).getCurrency())){
                        isCurrencySupported=true;
                        break;
                    }
                }
                if (isCurrencySupported){
                    fragmentClickNetwork.onPurchaseCardClick(true, 0, list.get(position).getPurchaseOptions(),list.get(position));
                    resetSelectable(position);
                    notifyDataSetChanged();
                }else {
                    Toast.makeText(context, "This feature is not available in your region", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (rowIndex == position) {

            holder.currency_price.setBackground(context.getDrawable(R.drawable.rounded_corner_textview_white));
            holder.currency_price.setTextColor(Color.parseColor("#009FFF"));
            list.get(position).setSelected(true);
            holder.title.setTextColor(context.getResources().getColor(R.color.white));
            holder.description.setTextColor(context.getResources().getColor(R.color.white));
            Resources res = context.getResources(); //resource handle
            Drawable drawable = res.getDrawable(R.drawable.rounded_color_with_dark_theme);
            holder.cardView.setBackground(drawable);
        } else {

            holder.currency_price.setBackground(context.getDrawable(R.drawable.rounded_corner_textview_blue));
            holder.currency_price.setTextColor(Color.parseColor("#ffffff"));
            list.get(position).setSelected(false);
            Resources res = context.getResources(); //resource handle
            Drawable drawable = res.getDrawable(R.drawable.rounded_corner_inapp);
            holder.cardView.setBackground(drawable);

        }



    }

    private void resetSelectable(int pos) {
        rowIndex = pos;
        for (int i = 0; i < list.size(); i++) {
            if (i == pos) {
                list.get(i).setSelected(true);
            } else {
                list.get(i).setSelected(false);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnPurchaseItemClick {
        void onPurchaseCardClick(boolean click, int position, String planName,PurchaseModel purchaseModel);
    }

    public class PurchaseViewHolder extends RecyclerView.ViewHolder {
        public final TextView title,description,plan_type;
        // public final CheckBox cbPurchase;
        public final TextView currency_price;
        public final RelativeLayout cardView;
        public ShimmerFrameLayout shimmerFrameLayout;
        public RelativeLayout relativeLayout;


        public PurchaseViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            currency_price = view.findViewById(R.id.currency_price);
            plan_type = view.findViewById(R.id.plan_type);
            cardView = view.findViewById(R.id.cv_tvod);
        }
    }

}
