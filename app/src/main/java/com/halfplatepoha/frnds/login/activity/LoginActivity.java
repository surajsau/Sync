package com.halfplatepoha.frnds.login.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.home.activity.HomeActivity;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.models.request.RegisterGCMRequest;
import com.halfplatepoha.frnds.models.request.RegisterRequest;
import com.halfplatepoha.frnds.models.response.RegisterGCMResponse;
import com.halfplatepoha.frnds.models.response.RegisterResponse;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;
import com.halfplatepoha.frnds.ui.OpenSansButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener,
        FacebookCallback<LoginResult>, OnCompleteListener<AuthResult>, GraphRequest.GraphJSONObjectCallback{

    private static final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private FrndsClient mClient;

    private int gcmRetryCount = 5;
    private int registerRetryCount = 5;

    private String fbId;
    private String name;

    private Realm mRealm;

    private AnimationDrawable animationDrawable;

    @Bind(R.id.btnFbLogin)
    OpenSansButton btnFbLogin;

    @Bind(R.id.animation)
    View animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        animationDrawable = (AnimationDrawable) animation.getBackground();

        mRealm = Realm.getDefaultInstance();
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mClient = new ClientGenerator.Builder()
                                .setBaseUrl(IConstants.FRNDS_BASE_URL)
                                .setLoggingInterceptor()
                                .setHeader(IConstants.CONTENT_TYPE, IConstants.APPLICATION_JSON)
                                .setClientClass(FrndsClient.class)
                                .buildClient();

        initFbButton();
    }

    private void initFbButton() {
        LoginManager.getInstance().registerCallback(mCallbackManager, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            FrndsLog.e(user.getUid());
            callRegisterApi();
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        FrndsLog.d(loginResult.getAccessToken().getToken());
        getFacebookName(loginResult.getAccessToken());
        getFriendsWhoInstalledApp(loginResult.getAccessToken());
        handleAccessToken(loginResult.getAccessToken());
    }

    private void getFacebookName(AccessToken token) {
        GraphRequest req = GraphRequest.newMeRequest(token, this);
        Bundle params = new Bundle();
        params.putString("fields", "id, name");
        req.setParameters(params);
        req.executeAsync();
    }

    private void handleAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        fbId = accessToken.getUserId();
        FrndsPreference.setInPref(IPrefConstants.FB_USER_ID, fbId);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, this);
    }

    @Override
    public void onCancel() {
        btnFbLogin.setVisibility(View.VISIBLE);
        FrndsLog.e("Cancelled");
    }

    @Override
    public void onError(FacebookException error) {
        btnFbLogin.setVisibility(View.VISIBLE);
        FrndsLog.e(error.getMessage());
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(!task.isSuccessful()) {
            FrndsLog.e("signInWithCredential " + task.getException());
        } else {
            FrndsLog.d("signInWithCredential " + task.isSuccessful());
        }
    }

    private void callRegisterApi() {
        RegisterRequest req = new RegisterRequest();
        req.setFbId(fbId);
        req.setName(name);
        mClient.register(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(registerSubscriber);
    }

    private void callRegisterGCMApi() {
        RegisterGCMRequest req = new RegisterGCMRequest();
        req.setDeviceId(FrndsPreference.getFromPref(IPrefConstants.FCM_REFRESH_TOKEN, ""));
        req.setFbId(fbId);

        mClient.updateGCM(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(registergcmSubscriber);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {
        try {
            name = object.getString("name");
            FrndsPreference.setInPref(IPrefConstants.USER_NAME, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnFbLogin)
    public void onFbLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
        btnFbLogin.setVisibility(View.GONE);
    }

    private BaseSubscriber<RegisterGCMResponse> registergcmSubscriber = new BaseSubscriber<RegisterGCMResponse>() {
        @Override
        public void onObjectReceived(RegisterGCMResponse registerGCMResponse) {
            if(!registerGCMResponse.isSuccessful() && gcmRetryCount > 0)
                callRegisterGCMApi();
            else
                FrndsPreference.setInPref(IPrefConstants.FCM_REFRESH_TOKEN_REGISTERED, true);
        }
    };

    private BaseSubscriber<RegisterResponse> registerSubscriber = new BaseSubscriber<RegisterResponse>() {
        @Override
        public void onObjectReceived(RegisterResponse registerResponse) {
            if(!registerResponse.isSuccessful() && gcmRetryCount > 0)
                callRegisterApi();
            else
                callRegisterGCMApi();
        }
    };

    private void getFriendsWhoInstalledApp(AccessToken token) {

        Bundle params = new Bundle();
        params.putString("fields", "installed, id, name, picture.type(large)");

        new GraphRequest(token, "/me/friends", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if(response.getError() == null) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        FrndsLog.e(response.getRawResponse());
                        InstalledFrnds frnds = mapper.readValue(response.getRawResponse(), InstalledFrnds.class);
                        updateFrndsList(frnds);
                        startHomeActivity();
                    }  catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }).executeAsync();
    }

    private void startHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    private void updateFrndsList(final InstalledFrnds frnds) {
        if(frnds != null && frnds.getData() != null && !frnds.getData().isEmpty()) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for(InstalledFrnds.Frnd frnd : frnds.getData()) {
                        if(realm.where(Chat.class).equalTo(IDbConstants.FRND_ID_KEY, frnd.getId()).findFirst() == null) {
                            Chat chat = new Chat();
                            chat.setFrndId(frnd.getId());
                            chat.setFrndName(frnd.getName());
                            chat.setFrndImageUrl(frnd.getPicture().getData().getUrl());
                            realm.insert(chat);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animationDrawable.stop();
        mRealm.close();
        mAuth.removeAuthStateListener(this);
    }
}
