package com.uphf.sae6_app.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

/**
 * Fournit une instance singleton de Retrofit configurée avec Gson.
 * Modifier BASE_URL selon l'environnement (10.0.2.2 pour émulateur si backend local).
 */
public class RetrofitClient {

    // Adapter l'URL de base à ton backend. Exemple local pour émulateur : "http://10.0.2.2:8080/"
    private static final String BASE_URL = "https://backendsae-production.up.railway.app/";

    private static Retrofit instance;

    public static Retrofit getInstance() {
        if (instance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();

            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}

