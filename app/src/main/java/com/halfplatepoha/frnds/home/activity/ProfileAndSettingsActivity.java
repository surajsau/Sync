package com.halfplatepoha.frnds.home.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.TokenTracker;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;
import com.halfplatepoha.frnds.models.fb.UserProfile;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAndSettingsActivity extends AppCompatActivity {

    @Bind(R.id.ivUserImage)
    CircleImageView ivUserImage;

    @Bind(R.id.tvUserName)
    OpenSansTextView tvUserName;

    AccessTokenTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_settings);
        ButterKnife.bind(this);

        showProfile();

        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                openSplashActivity();
            }
        };
    }

    private void openSplashActivity() {
        Intent splashIntent = new Intent(this, SplashScreenActivity.class);
        splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(splashIntent);
    }

    private void showProfile() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null) {
            accessToken = TokenTracker.getInstance().getCurrentAccessToken();
        }

        Bundle params = new Bundle();
        params.putString("fields", "name, picture.type(large)");

        new GraphRequest(accessToken, "/me", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if(response.getError() == null) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        FrndsLog.e(response.getRawResponse());
                        UserProfile user = mapper.readValue(response.getRawResponse(), UserProfile.class);

                        tvUserName.setText(user.getName());
                        Glide.with(ProfileAndSettingsActivity.this)
                                .load(user.getPicture().getData().getUrl())
                                .error(R.drawable.pappi)
                                .into(ivUserImage);
                    }  catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).executeAsync();
    }

    @OnClick(R.id.btnLogout)
    public void logout() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
    }

    @OnClick(R.id.back)
    public void onBack() {
        finish();
    }
}
