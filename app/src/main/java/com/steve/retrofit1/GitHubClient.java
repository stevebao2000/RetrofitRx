package com.steve.retrofit1;


import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface GitHubClient {
    @GET("users")
    Single<List<GitHubEntry>> getGTUsers(); // for RxAndroid (Single)

    @GET("users")
    Call<List<GitHubEntry>> getGitHubUsers(); // for Retrofit (Call)
}
