package com.cpm.retrofit;

import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by jeevanp on 05-10-2017.
 */

public interface PostApiForFile {
    @POST("Uploadfile")
    Call<String>getUploadImage(@Body RequestBody reqesBody);
}
