package com.halfplatepoha.frnds.fcm.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.models.request.RegisterGCMRequest;
import com.halfplatepoha.frnds.models.response.RegisterGCMResponse;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;

import rx.schedulers.Schedulers;

/**
 * Created by surajkumarsau on 27/08/16.
 */
public class DeviceIdService extends FirebaseInstanceIdService {

    private FrndsClient mClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mClient = new ClientGenerator.Builder()
                .setBaseUrl(IConstants.FRNDS_BASE_URL)
                .setLoggingInterceptor()
                .setHeader(IConstants.CONTENT_TYPE, IConstants.APPLICATION_JSON)
                .setClientClass(FrndsClient.class)
                .buildClient();
    }

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        FrndsLog.i("Refresh token: " +  refreshToken);

        FrndsPreference.setInPref(IPrefConstants.FCM_REFRESH_TOKEN, refreshToken);

        if(FrndsPreference.getBooleanFromPref(IPrefConstants.IS_REGISTERED, false))
            callRegisterGCMApi();
    }

    private void callRegisterGCMApi() {
        RegisterGCMRequest req = new RegisterGCMRequest();
        req.setDeviceId(FrndsPreference.getFromPref(IPrefConstants.FCM_REFRESH_TOKEN, ""));
        req.setFbId(FrndsPreference.getFromPref(IPrefConstants.FB_USER_ID, ""));

        mClient.updateGCM(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new BaseSubscriber<RegisterGCMResponse>() {
                    @Override
                    public void onObjectReceived(RegisterGCMResponse registerGCMResponse) {
                        if(!registerGCMResponse.isSuccessful())
                            callRegisterGCMApi();
                        else
                            FrndsPreference.setInPref(IPrefConstants.FCM_REFRESH_TOKEN_REGISTERED, true);
                    }
                });
    }
}
