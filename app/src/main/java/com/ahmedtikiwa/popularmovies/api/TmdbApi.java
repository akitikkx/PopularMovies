package com.ahmedtikiwa.popularmovies.api;

import com.ahmedtikiwa.popularmovies.models.MoviesResponse;

import okhttp3.OkHttpClient;
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

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // add the logging interceptor to the call
            httpClient.addInterceptor(loggingInterceptor);

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
