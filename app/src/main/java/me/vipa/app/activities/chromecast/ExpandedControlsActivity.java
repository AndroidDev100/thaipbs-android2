package me.vipa.app.activities.chromecast;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.mediarouter.app.MediaRouteButton;

import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.brightcovelibrary.chromecast.CastContextAttachedListner;
import me.vipa.brightcovelibrary.chromecast.ChromeCastConnectionListener;
import me.vipa.brightcovelibrary.chromecast.ChromecastManager;
import me.vipa.brightcovelibrary.chromecast.ChromecastStateCallback;
import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.databinding.CastCustomePlayerBinding;
import me.vipa.app.utils.helpers.ImageHelper;

import com.brightcove.cast.DefaultExpandedControllerActivity;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;

public class ExpandedControlsActivity extends DefaultExpandedControllerActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.cast_expanded_controller_activity);

    }

    @Override
    protected void onResume() {
        super.onResume();
        EnveuVideoItemBean asset = (EnveuVideoItemBean) getIntent().getSerializableExtra("Asset");
        if (asset != null)
            ((TextView) findViewById(R.id.asset_title)).setText(asset.getTitle());
        findViewById(R.id.blurred_background_image_view).setVisibility(View.GONE);
        if (asset.getPosterURL()!=null && asset.getPosterURL()!="") {
            Glide.with(this)
                    .asBitmap()
                    .load(asset.getPosterURL())
                    .apply(AppCommonMethod.castOptions)
                    .into((ImageView) findViewById(R.id.background_place_holder_image_view));
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

}
