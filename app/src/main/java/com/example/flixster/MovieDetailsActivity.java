package com.example.flixster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String TAG = "MovieDetailsActivity";
    public static final String GENRE_URL = "https://api.themoviedb.org/3/genre/movie/list?api_key=";
    public static JSONObject genres = new JSONObject();

    Movie movie;
    int posterRadius = 30;
    int posterMargin = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));

        // Unwrap movie information from intent with movie's simple name as key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        if(movie!=null){
            Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

            // Set title and overview
            binding.tvTitle.setText(String.format("%s (%s)", movie.getTitle(), movie.getReleaseDate()));
            binding.tvOverview.setText(movie.getOverview());

            // Get genre ids and names and convert into list to display
            if(genres.length()==0){
                // Collect id->genre String mapping from API and store in genres
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(String.format("https://api.themoviedb.org/3/genre/movie/list?api_key=%s", getResources().getString(R.string.api_key)), new JsonHttpResponseHandler() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d(TAG, "onSuccess");
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray results = jsonObject.getJSONArray("genres");
                            Log.i(TAG, "Results: " + results.toString());
                            for(int i = 0; i<results.length(); i++){
                                JSONObject thisGenre = results.getJSONObject(i);
                                genres.put(thisGenre.getString("id"), thisGenre.getString("name"));
                            }
                            List<Integer> genreIds = movie.getGenres();
                            final List<String> genreStrings = new ArrayList<>();
                            for(int i = 0; i<genreIds.size(); i++){
                                try {
                                    genreStrings.add((String) genres.get(String.valueOf(genreIds.get(i))));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            binding.tvGenres.setText(String.format("Genres: %s", String.join(", ", genreStrings)));

                        } catch (JSONException e) {
                            Log.e(TAG, "Hit JSON exception", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure", throwable);
                    }
                });
            } else {
                List<Integer> genreIds = movie.getGenres();
                final List<String> genreStrings = new ArrayList<>();
                for(int i = 0; i<genreIds.size(); i++){
                    try {
                        genreStrings.add((String) genres.get(String.valueOf(genreIds.get(i))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.tvGenres.setText(String.format("Genres: %s", String.join(", ", genreStrings)));
            }

            Glide.with(this)
                    .load(movie.getBackdropPath())
                    .placeholder(R.drawable.flicks_backdrop_placeholder)
                    .transform(new RoundedCornersTransformation(posterRadius, posterMargin))
                    .into(binding.ivPreview);

            // Vote average is 0-10 scale. Divide by two to get 0-5 scale with 0.5 steps
            float voteAverage = movie.getVoteAverage().floatValue();
            binding.rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
        }
    }

    public void onPlayClick(View view) {
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "onPlayClick: " + movie.getId());
        client.get(String.format("https://api.themoviedb.org/3/movie/%s/videos?api_key=%s", movie.getId(), getString(R.string.api_key)), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    int index = 0;
                    while(index<results.length() && !results.getJSONObject(index).getString("site").equals("YouTube")){
                        index++;
                    }
                    if(index<results.length()){
                        Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                        i.putExtra("videoId", results.getJSONObject(index).getString("key"));
                        startActivity(i);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });
    }
}