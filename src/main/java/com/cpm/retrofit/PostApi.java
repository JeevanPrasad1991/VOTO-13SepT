package com.cpm.retrofit;

import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by jeevanp on 19-05-2017.
 */

public interface PostApi {
    @POST("Uploadimages")
        Call<String> getUploadImage(@Body RequestBody reqesBody);
}
