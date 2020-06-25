package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;
import org.w3c.dom.Text;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;

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

            // Vote average is 0-10 scale. Divide by two to get 0-5 scale with 0.5 steps
            float voteAverage = movie.getVoteAverage().floatValue();
            binding.rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
        }
    }
}