package com.nikolar.noteencrypt.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Optional;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static String base_url = "";

    public static Optional<Retrofit> getClient() {
        if (base_url.isEmpty())
            return Optional.empty();
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return Optional.of(retrofit);
    }

    public static Optional<Retrofit> getClient(String base_url) {
        if (!RetrofitClient.base_url.equals(base_url)){
            retrofit = null;
        }
        RetrofitClient.base_url = base_url;
        return getClient();
    }

}
