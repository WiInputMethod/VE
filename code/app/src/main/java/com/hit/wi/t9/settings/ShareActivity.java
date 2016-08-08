package com.hit.wi.t9.settings;

import com.hit.wi.t9.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

/**
 * 分享界面
 */
public final class ShareActivity extends Activity {

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        String share = res.getString(R.string.share);
        String shareText = res.getString(R.string.share_text);
        String shareSubject = res.getString(R.string.share_subject);
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(
                Intent.EXTRA_TEXT,
                shareText);
        intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, share));
        this.finish();

    }

    @Override
    protected void onResume() {

        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

}
