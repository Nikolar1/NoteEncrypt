package com.nikolar.noteencrypt.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NoteApiService {
    @GET("/notes")
    Call<List<String>> getNotes();

    @POST("/saveNotes")
    Call<String> saveNotes(@Body List<String> notes);
}
