package com.halfplatepoha.frnds.login.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.halfplatepoha.frnds.R;

/**
 * Created by surajkumarsau on 18/02/17.
 */

public class IntroActivity extends AppIntro2 {

    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        source = getIntent().getStringExtra(IntroConstants.INTRO_SOURCE);

        addSlide(AppIntro2Fragment.newInstance("Choose from SoundCloud", "Choose from millions of SoundCloud tracks to Sync with your friends", R.drawable.com_facebook_favicon_blue, ContextCompat.getColor(this, R.color.soundCloud), ContextCompat.getColor(this, R.color.soundCloud)));
        addSlide(AppIntro2Fragment.newInstance("Sync with friends", "Let your buddies know what you\'re hearing right now", R.drawable.com_facebook_favicon_blue, ContextCompat.getColor(this, R.color.colorAccent), ContextCompat.getColor(this, R.color.colorAccent)));
        addSlide(AppIntro2Fragment.newInstance("Chat with them", "Appreciate the music choice or just have your friendly conversations", R.drawable.com_facebook_favicon_blue, ContextCompat.getColor(this, R.color.purple_500), ContextCompat.getColor(this, R.color.purple_500)));
        addSlide(AppIntro2Fragment.newInstance("Get your friends", "Log in through Facebook to start Syncing with your buddies", R.drawable.com_facebook_favicon_blue, ContextCompat.getColor(this, R.color.com_facebook_blue), ContextCompat.getColor(this, R.color.com_facebook_blue)));

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        switch (source) {
            case IntroConstants.SOURCE_PROFILE:
                finish();
                break;

            case IntroConstants.SOURCE_SPLASH:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
