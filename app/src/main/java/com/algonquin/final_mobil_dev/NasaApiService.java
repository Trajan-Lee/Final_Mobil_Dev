// #9 AsyncTask is deprecated, so I used Retrofit instead.
package com.algonquin.final_mobil_dev;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * NasaApiService is an interface for making API calls to the NASA APOD (Astronomy Picture of the Day) service.
 * It uses Retrofit to handle HTTP requests and responses.
 */
public interface NasaApiService {
    /**
     * Retrieves the NASA image of the day for a specified date.
     *
     * @param apiKey The API key for authenticating the request.
     * @param date The date for which to retrieve the image (in YYYY-MM-DD format).
     * @return A Call object that can be used to request the image.
     */
    @GET("planetary/apod")
    Call<NasaImage> getImage(@Query("api_key") String apiKey, @Query("date") String date);
}


