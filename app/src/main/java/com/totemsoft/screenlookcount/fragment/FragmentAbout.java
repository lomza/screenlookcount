package com.totemsoft.screenlookcount.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.totemsoft.screenlookcount.AnalyticsApplication;
import com.totemsoft.screenlookcount.R;
import com.totemsoft.screenlookcount.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Holds info about app's functionality and author.
 *
 * @author antonina.tkachuk
 */
public class FragmentAbout extends Fragment {
    @Bind(R.id.tv_about_text)
    TextView aboutTextView;
    @Bind(R.id.tv_about_looks)
    TextView aboutLooksTextView;
    @Bind(R.id.tv_about_unlocks)
    TextView aboutUnlocksTextView;
    @Bind(R.id.tv_about_me)
    TextView aboutMeTextView;
    @Bind(R.id.tv_about_source_code)
    TextView aboutSourceCodeTextView;

    private Tracker tracker;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tracker = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        tracker.setScreenName("About");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        init();

        return view;
    }

    private void init() {
        aboutTextView.setText(Utils.getHtmlFormattedText(getString(R.string.about_text)));
        aboutLooksTextView.setText(Utils.getHtmlFormattedText(getString(R.string.about_looks)));
        aboutUnlocksTextView.setText(Utils.getHtmlFormattedText(getString(R.string.about_unlocks)));
        aboutMeTextView.setText(Utils.getHtmlFormattedText(getString(R.string.about_me)));
        aboutSourceCodeTextView.setMovementMethod(LinkMovementMethod.getInstance());
        aboutSourceCodeTextView.setText(Utils.getHtmlFormattedText(getString(R.string.about_source_code)));
    }

    @OnClick(R.id.b_contact_me)
    public void contactMe() {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Buttons")
                .setAction("Contact me")
                .build());

        Intent contactIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.about_email_to), null));
        contactIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_email_subject));
        startActivity(Intent.createChooser(contactIntent, getString(R.string.email_chooser)));
    }
}
