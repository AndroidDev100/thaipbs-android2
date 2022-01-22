package me.vipa.app.activities.deeplink;

import android.os.Bundle;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import io.branch.referral.Branch;
import me.vipa.app.R;
import me.vipa.app.activities.article.ArticleActivity;
import me.vipa.app.activities.detail.ui.EpisodeActivity;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

public class DeepLinkActivity extends AppCompatActivity {

    private long mLastClickTime = 0;
    private boolean viaIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);
    }

    @Override
    protected void onStart() {
        super.onStart();
        branchRedirection();
    }

    private void branchRedirection() {
        Logger.e("onResume--", "Branch.getInstance()");
        Branch.getInstance().initSession((referringParams, error) -> {
            Logger.e("onResume-----", "Branch.getInstance()");
            KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
            int assestId = 0;
            String contentType = "";
            try {
                assestId = Integer.valueOf(referringParams.getString("id"));
                contentType = referringParams.getString("contentType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Logger.e("ASSET TYPE", String.valueOf(viaIntent));

            if (!viaIntent) {
                preference.setAppPrefJumpTo(contentType);
                preference.setAppPrefBranchIo(true);
                preference.setAppPrefJumpBackId(assestId);

            }
            if (contentType.equalsIgnoreCase(MediaTypeConstants.getInstance().getSeries())) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ActivityLauncher.getInstance().seriesDetailScreen(DeepLinkActivity.this, SeriesDetailActivity.class, assestId);
            } else if (contentType.equalsIgnoreCase(AppConstants.ContentType.VIDEO.toString())) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ActivityLauncher.getInstance().articleScreen(DeepLinkActivity.this, ArticleActivity.class, assestId, "0", false);
            } else if (contentType.equalsIgnoreCase(MediaTypeConstants.getInstance().getEpisode())) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ActivityLauncher.getInstance().episodeScreen(DeepLinkActivity.this, EpisodeActivity.class, assestId, "0", false);
            }
            finish();
        });

    }
}
