package com.uphf.sae6_app.retrofit;

import com.uphf.sae6_app.model.GreenItData;
import com.uphf.sae6_app.model.InfoItem;
import com.uphf.sae6_app.model.QuizItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface décrivant les endpoints utilisés par l'application.
 * Adapte les chemins @GET en fonction des routes réelles de ton backend.
 */
public interface GreenItApi {

    @GET("greenitdata")
    Call<List<GreenItData>> getGreenItData();

    @GET("fiches-complexes")
    Call<List<InfoItem>> getInfos();

    @GET("quiz")
    Call<List<QuizItem>> getQuiz();

    // Exemple avec filtres côté backend
    // @GET("quiz")
    // Call<List<QuizItem>> getQuiz(@Query("theme") String theme, @Query("difficulty") Integer difficulty);
}

