package com.example.flixster.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.R;
import com.example.flixster.databinding.ItemMovieBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Headers;

@Parcel
public class Movie {
    public static final String TAG = "Movietesting";

    String backdropPath;
    String posterPath;
    String title;
    String overview;
    Double voteAverage;
    String releaseDate;
    Integer id;
    List<Integer> genres;

    public Movie(){}

    public Movie(JSONObject jsonObject) throws JSONException {
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        voteAverage = jsonObject.getDouble("vote_average");
        releaseDate = jsonObject.getString("release_date").substring(0,4);
        id = jsonObject.getInt("id");
        genres = new ArrayList<>();
        JSONArray genresJson = jsonObject.getJSONArray("genre_ids");
        for(int i = 0; i<genresJson.length(); i++){
            genres.add((Integer) genresJson.get(i));
        }
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

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getId() {
        return id;
    }

    public List<Integer> getGenres() {
        return genres;
    }
}
