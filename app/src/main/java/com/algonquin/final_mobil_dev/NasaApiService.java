
// #9 AsyncTask is deprecated, so I used Retrofit instead.
package com.algonquin.final_mobil_dev;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NasaApiService {
    @GET("planetary/apod")
    Call<NasaImage> getImage(@Query("api_key") String apiKey, @Query("date") String date);
}