package com.mvhub.mvhubplus.activities.chromecast;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.mediarouter.app.MediaRouteButton;
import com.mvhub.brightcovelibrary.chromecast.CastContextAttachedListner;
import com.mvhub.brightcovelibrary.chromecast.ChromeCastConnectionListener;
import com.mvhub.brightcovelibrary.chromecast.ChromecastManager;
import com.mvhub.brightcovelibrary.chromecast.ChromecastStateCallback;
import com.mvhub.mvhubplus.R;
import com.mvhub.mvhubplus.SDKConfig;
import com.mvhub.mvhubplus.beanModel.enveuCommonRailData.RailCommonData;
import com.mvhub.mvhubplus.databinding.CastCustomePlayerBinding;
import com.mvhub.mvhubplus.utils.helpers.ImageHelper;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;

public class ExpandedControlsActivity extends ExpandedControllerActivity implements View.OnClickListener, ChromecastStateCallback {


    private CastCustomePlayerBinding castControllerActivityBinding;
    private RailCommonData railCommonData;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ChromecastManager.getInstance().setChromecastStateCallBacks(this);
        // setContentView(R.layout.cast_controller_activity);
        castControllerActivityBinding = DataBindingUtil.setContentView(this, R.layout.cast_custome_player);
        //railCommonData = (RailCommonData) getIntent().getParcelableExtra(AppConstants.ASSET);


        // Toast.makeText(getApplicationContext(),strImageUrl,Toast.LENGTH_LONG).show();
        getPlayerPoster(getIntent().getStringExtra("image_url"));
        MediaRouteButton mediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        castControllerActivityBinding.seekBar.setPosition(0);

        setClicks();


        CastButtonFactory.setUpMediaRouteButton(getApplication(), mediaRouteButton);
        //Log.w("runningPosition",ChromecastManager.getInstance().runningPosition+"");
        //Log.w("runningPosition",ChromecastManager.getInstance().getRunningVideoId()+"");
        ChromecastManager.getInstance().init(getApplicationContext(),this,mediaRouteButton, new CastContextAttachedListner() {
            @Override
            public void onDevicesAvailable() {

                ChromecastManager.getInstance().addCastListener();

                if (mediaRouteButton != null) {
                    mediaRouteButton.setVisibility(View.VISIBLE);
                    mediaRouteButton.setRemoteIndicatorDrawable(getResources().getDrawable(R.drawable.cast_connected_icon));
                }else{

//                    mediaRouteButton.setVisibility(View.GONE);
                }

            }

            @Override
            public void onDevicesUnavailable() {
                if(mediaRouteButton !=null)
                    mediaRouteButton.setVisibility(View.GONE);
                // Toast.makeText(getApplicationContext(),"Device Not Available", Toast.LENGTH_LONG).show();
            }
        }, new ChromeCastConnectionListener() {
            @Override
            public void onSessionStarted() {
                if(SDKConfig.ApplicationStatus == "disconnected") {
                    SDKConfig.ApplicationStatus = "connected";
                }
            }

            @Override
            public void onSessionFailed() {

            }
        });




/*
        castControllerActivityBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Toast.makeText(getApplicationContext(),"positionIs"+i,Toast.LENGTH_LONG).show();

              //  ChromecastManager.getInstance().remoteMediClientUpdate(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
*/

        castControllerActivityBinding.seekBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                /*currentPosition.setText(Utils.stringForTime(position));
                hideSkipIntro();*/
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                castControllerActivityBinding.seekBar.setPosition(position);
                ChromecastManager.getInstance().remoteMediClientUpdate(position);
                //  playerCallbacks.SeekbarLastPosition(position);
            }
        });

        ChromecastManager.getInstance().setPlaybackDuration(castControllerActivityBinding.totalDuration, castControllerActivityBinding.currentTime, castControllerActivityBinding.seekBar,castControllerActivityBinding.buttonPlayPauseToggle);


        castControllerActivityBinding.buttonPlayPauseToggle.setOnClickListener(this);

    }

    private void getPlayerPoster(String image_url) {
        ImageHelper.getInstance(ExpandedControlsActivity.this).loadListImage(castControllerActivityBinding.posterImage, image_url);
    }

    private void setClicks() {
        castControllerActivityBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new KsPreferenceKeys(ExpandedControlsActivity.this).setFromChrome(true);
                onBackPressed();
                //exitChromecastView();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.expanded_controller, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "True");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop", "True");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_play_pause_toggle:
                ChromecastManager.getInstance().playPauseToggle(castControllerActivityBinding.buttonPlayPauseToggle);
                break;
        }
    }

    String  image_url="";
/*
    private void getPlayerPoster(Asset playerAsset) {

        if (playerAsset.getImages().size() > 0) {
            for (int i = 0; i<playerAsset.getImages().size(); i++){
                if (playerAsset.getImages().get(i).getRatio().equals("16:9")) {
                    image_url = playerAsset.getImages().get(i).getUrl();
                    image_url = image_url + AppConstants.WIDTH + (int) getResources().getDimension(R.dimen.carousel_image_width) + AppConstants.HEIGHT + (int) getResources().getDimension(R.dimen.carousel_image_height) + AppConstants.QUALITY;
                    Glide.with(getApplicationContext()).load(image_url)
                            .thumbnail(0.7f).into(castControllerActivityBinding.posterImage);
                }
            }

            if(image_url.equalsIgnoreCase("")){
                if (new KsPreferenceKeys(this).getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {

                    Glide.with(getApplicationContext()).load(R.drawable.shimmer_banner)
                            .thumbnail(0.7f).into(castControllerActivityBinding.posterImage);
                } else {
                    Glide.with(getApplicationContext()).load(R.drawable.banner_dark)
                            .thumbnail(0.7f).into(castControllerActivityBinding.posterImage);

                }
            }
            // if (playerAsset.getImages().get(0).getRatio().equals("9:16"))


        }else {
            if (new KsPreferenceKeys(this).getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {

                Glide.with(getApplicationContext()).load(R.drawable.shimmer_banner)
                        .thumbnail(0.7f).into(castControllerActivityBinding.posterImage);
            } else {
                Glide.with(getApplicationContext()).load(R.drawable.banner_dark)
                        .thumbnail(0.7f).into(castControllerActivityBinding.posterImage);

            }

        }

//        }

    }
*/

    @Override
    public void onBuffering() {
        castControllerActivityBinding.pBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaying() {
        castControllerActivityBinding.pBar.setVisibility(View.GONE);
    }

    private void exitChromecastView() {




//        String name = asset.getName();
//        RailCommonData railCommonData= new RailCommonData();
//        railCommonData.setObject(asset);


        // clearPlayerStack(name, railCommonData, layoutPosition, layoutType, detailRailClick);

       /*   new ActivityLauncher(getApplicationContext()).railClickCondition(strMenuNavigationName, strRailName, name, railCommonData), getLayoutPosition(), layoutType, (_url, position, type, commonData) -> {

            if (NetworkConnectivity.isOnline(ApplicationMain.getAppContext())) {
                detailRailClick.detailItemClicked(_url, position, type, commonData);
            } else {
                ToastHandler.show(mContext.getResources().getString(R.string.no_internet_connection), ApplicationMain.getAppContext());
            }

        });*/

    }

}
