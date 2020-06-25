package com.example.flixster.models;

import android.util.Log;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class Movie {
    public static final String IMAGE_CONFIG_URL = String.format("https://api.themoviedb.org/3/configuration?api_key=%s","@string/api_key");

    String backdropPath;
    String posterPath;
    String title;
    String overview;

    public Movie(JSONObject jsonObject) throws JSONException {
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
    }

    public static List<Movie> fromJSONArray(JSONArray movieJSONArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for(int i = 0; i<movieJSONArray.length(); i++){
            movies.add(new Movie(movieJSONArray.getJSONObject(i)));
        }
        return movies;
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/%s/%s", "w342", posterPath); // This currently hard-codes image size
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/%s/%s", "w342", backdropPath); // This currently hard-codes image size
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }
}
