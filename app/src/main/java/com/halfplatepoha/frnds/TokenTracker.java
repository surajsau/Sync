package com.halfplatepoha.frnds;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

/**
 * Created by surajkumarsau on 22/09/16.
 */
public class TokenTracker extends AccessTokenTracker {

    private AccessToken mCurrentAccessToken;

    private static TokenTracker mInstance;

    private TokenTracker() {}

    public static TokenTracker getInstance() {
        if(mInstance == null)
            mInstance = new TokenTracker();
        return mInstance;
    }

    @Override
    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
        mCurrentAccessToken = currentAccessToken;
    }

    public AccessToken getCurrentAccessToken() {
        return mCurrentAccessToken;
    }

}
