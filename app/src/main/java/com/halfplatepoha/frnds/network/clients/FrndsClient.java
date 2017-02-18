package com.halfplatepoha.frnds.network.clients;

import com.halfplatepoha.frnds.models.request.GetPendingRequest;
import com.halfplatepoha.frnds.models.request.RegisterGCMRequest;
import com.halfplatepoha.frnds.models.request.RegisterRequest;
import com.halfplatepoha.frnds.models.request.SendMessageRequest;
import com.halfplatepoha.frnds.models.request.UpdateTrackRequest;
import com.halfplatepoha.frnds.models.response.GetPendingResponse;
import com.halfplatepoha.frnds.models.response.RegisterGCMResponse;
import com.halfplatepoha.frnds.models.response.RegisterResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
import rx.observables.ConnectableObservable;

/**
 * Created by surajkumarsau on 28/08/16.
 */
public interface FrndsClient {

    @POST("v0/registerGCM")
    Observable<RegisterGCMResponse> updateGCM(@Body RegisterGCMRequest request);

    @POST("v0/register")
    Observable<RegisterResponse> register(@Body RegisterRequest request);

    @POST("v0/updateTrack")
    Observable<Void> updateTrack(@Body UpdateTrackRequest request);

    @POST("v0/sendMessage")
    Observable<Void> sendMessage(@Body SendMessageRequest request);

    @POST("v0/getPending")
    Observable<GetPendingResponse> getPending(@Body GetPendingRequest request);

}
