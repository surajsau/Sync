package com.halfplatepoha.frnds.network.clients;

import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.models.response.TrackResponse;
import com.halfplatepoha.frnds.network.ILoadingObservable;
import com.halfplatepoha.frnds.network.OffsetAndLimit;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by surajkumarsau on 24/08/16.
 */
public interface SoundCloudClient {

    @GET("tracks/{id}")
    Observable<TrackDetails> getTrackDetails(@Path("id") String id);

    @GET("tracks")
    Observable<List<TrackDetails>> getSearchResult(@Query("q") String searchString,
                                                   @Query("limit")int pageSize,
                                                   @Query("offset")int offset);

}
