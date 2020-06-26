package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String TAG = "MovieDetailsActivity";

    Movie movie;
    int posterRadius = 30;
    int posterMargin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Unwrap movie information from intent with movie's simple name as key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        if(movie!=null){
            Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

            // Set title and overview
            binding.tvTitle.setText(movie.getTitle());
            binding.tvOverview.setText(movie.getOverview());
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
                    if(results.length()>0){
                        Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                        i.putExtra("videoId", results.getJSONObject(0).getString("key"));
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