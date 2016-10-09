package com.ahmedtikiwa.popularmovies.api;

import com.ahmedtikiwa.popularmovies.App;
import com.ahmedtikiwa.popularmovies.models.MoviesResponse;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ahmed on 2016/10/05.
 */

public class TmdbApi {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static TmdbApiInterface mTmdbApiService;

    public static TmdbApiInterface getTmdbApiClient() {
        if (mTmdbApiService == null) {

            // set the logging for the retrofit calls
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    if (App.hasNetworkConnection()) {
                        int maxAge = 60 * 5; // read from cache for 5 minute
                        request = request.newBuilder().header("Cache-Control", "public, max-age=" + maxAge).build();
                    } else {
                        int maxStale = 60 * 60; // 1 hour
                        request = request.newBuilder().header("Cache-Control", "public, max-age=" + maxStale).build();

                    }
                    return chain.proceed(request);
                }
            };

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            File httpCacheDirectory = new File(App.getContext().getCacheDir(), "responses");
            httpClient.cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024));

            // add the logging interceptor to the call
            httpClient.addInterceptor(loggingInterceptor);

            // add cache interceptor
            httpClient.addInterceptor(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mTmdbApiService = retrofit.create(TmdbApiInterface.class);
        }
        return mTmdbApiService;
    }

    public interface TmdbApiInterface {
        @GET("movie/popular/")
        Call<MoviesResponse> popularMovies(@Query("api_key") String apiKey);

        @GET("movie/top_rated/")
        Call<MoviesResponse> topRatedMovies(@Query("api_key") String apiKey);
    }

}
