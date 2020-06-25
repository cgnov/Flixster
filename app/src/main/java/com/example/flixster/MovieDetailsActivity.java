package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flixster.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvOverview = (TextView)findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar)findViewById(R.id.rbVoteAverage);

        // Unwrap movie information from intent with movie's simple name as key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        if(movie!=null){
            Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

            // Set title and overview
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());

            // Vote average is 0-10 scale. Divide by two to get 0-5 scale with 0.5 steps
            float voteAverage = movie.getVoteAverage().floatValue();
            rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
        }
    }
}