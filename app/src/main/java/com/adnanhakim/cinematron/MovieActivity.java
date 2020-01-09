package com.adnanhakim.cinematron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieActivity extends AppCompatActivity {

    // Url is base url + movie id + remaining url + api key
    private final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final String REMAINING_URL = "?api_key=";
    private final String API_KEY = BuildConfig.API_KEY;
    private String URL;
    private String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private String posterURL, backdropURL;

    // Url for cast is base url + movie id + remaining cast url + api key
    private final String REMAINING_CAST_URL = "/credits?api_key=";

    private final String OMDB_API = "&apikey=" + BuildConfig.OMDB_API;
    private final String OMDB_URL = "http://www.omdbapi.com/?i=";
    private String imdbId, imdbRating, rottenTomatoes, metacritic;

    private ProgressBar progressBar;
    private Typeface coolvetica;
    private NestedScrollView nestedScrollView;
    private RelativeLayout relativeRating;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView tvStatus, tvOverview, tvTagline, tvGenre, tvReleaseDate, tvRuntime, tvImdb;
    private TextView tvRottenTomatoes, tvMetacritic, tvProducer, tvDirector;
    private ImageView ivPoster, ivBackDrop;
    private int movieId;
    private String movieTitle;

    private int runtime;
    private double rating;
    private String overview, backdropPath, posterPath, releaseDate, status, tagline;

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Cast> casts;
    private String castCharacter, castName, castProfilePath, castImageURL;

    private JsonObjectRequest request, castRequest, ratingRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        initialize();

        // To get movie title and id from intent and set it to header
        Intent intent = getIntent();
        movieId = intent.getIntExtra("ID", 000000);
        movieTitle = intent.getStringExtra("TITLE");
        setUpCollapsingToolbar(movieTitle);

        relativeRating.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);

        getMovieDetails(movieId);
        getCastDetails(movieId);
    }


    private void initialize() {
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarMovie);
        tvStatus = findViewById(R.id.tvDetailsStatus);
        tvOverview = findViewById(R.id.tvDetailsOverview);
        tvTagline = findViewById(R.id.tvDetailsTagline);
        tvGenre = findViewById(R.id.tvDetailsGenre);
        tvReleaseDate = findViewById(R.id.tvDetailsReleaseDate);
        tvRuntime = findViewById(R.id.tvDetailsRuntime);
        tvDirector = findViewById(R.id.tvDetailsDirector);
        tvProducer = findViewById(R.id.tvDetailsProducer);
        ivPoster = findViewById(R.id.ivDetailsPoster);
        ivBackDrop = findViewById(R.id.ivDetailsBackDrop);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerCast);
        tvImdb = findViewById(R.id.tvDetailsImdb);
        tvRottenTomatoes = findViewById(R.id.tvDetailsRottenTomatoes);
        tvMetacritic = findViewById(R.id.tvDetailsMetacritic);
        nestedScrollView = findViewById(R.id.nestedSVDetails);
        relativeRating = findViewById(R.id.relativeDetailsRatings);
        // Initialize typefaces
        coolvetica = ResourcesCompat.getFont(this, R.font.coolveticafont);
    }

    private void setUpCollapsingToolbar(String title) {
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleTypeface(coolvetica);
        collapsingToolbarLayout.setCollapsedTitleTypeface(coolvetica);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getMovieDetails(int movieId) {
        URL = BASE_URL + movieId + REMAINING_URL + API_KEY;
        request = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);
                String genre = "";
                try {
                    overview = response.getString("overview");
                    genre = getGenre(response.getJSONArray("genres"));
                    rating = response.getDouble("vote_average");
                    backdropPath = response.getString("backdrop_path");
                    posterPath = response.getString("poster_path");
                    releaseDate = response.getString("release_date");
                    runtime = response.getInt("runtime");
                    status = response.getString("status");
                    tagline = response.getString("tagline");
                    // IMDB ID
                    imdbId = response.getString("imdb_id");
                    backdropURL = BASE_IMAGE_URL + backdropPath;
                    posterURL = BASE_IMAGE_URL + posterPath;

                    nestedScrollView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fillData(overview, genre, rating, posterURL, backdropURL, releaseDate, runtime, status, tagline);
                getRatings(imdbId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                error.fillInStackTrace();
            }
        });
        // Send request to Volley and add it to requestQueue
        requestQueue = Volley.newRequestQueue(MovieActivity.this);
        requestQueue.add(request);
    }

    private String getGenre(JSONArray genreArray) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < genreArray.length(); i++) {
            try {
                JSONObject genre = genreArray.getJSONObject(i);
                if (i == genreArray.length() - 1) {
                    builder.append(genre.getString("name"));
                } else {
                    builder.append(genre.getString("name") + ", ");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    private String formatDate(String oldDate) {
        String oldDateFormat = "yyyy-MM-dd";
        String newDateFormat = "dd MMMM yyyy";
        String newDate;
        SimpleDateFormat sdf = new SimpleDateFormat(oldDateFormat);
        try {
            Date date = sdf.parse(oldDate);
            sdf.applyPattern(newDateFormat);
            newDate = sdf.format(date);
            return newDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void fillData(String overview, String genre, double rating, String posterURL, String backdropURL,
                          String releaseDate, int runtime, String status, String tagline) {
        // Set backdrop using Glide
        RequestOptions option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_image)
                .error(R.drawable.loading_image);
        Glide.with(this).load(posterURL).apply(option).into(ivPoster);
        Glide.with(this).load(backdropURL).apply(option).into(ivBackDrop);

        tvGenre.setText("Genre: " + genre);
        tvStatus.setText(status);
        tvOverview.setText(overview);
        if (tagline != null && tagline.equals(""))
            tvTagline.setVisibility(View.INVISIBLE);
        else
            tvTagline.setText("\"" + tagline + "\"");
        tvReleaseDate.setText("Release Date: " + formatDate(releaseDate));
        tvRuntime.setText("Runtime: " + runtime + " minutes");
    }

    private void getCastDetails(int movieId) {
        casts = new ArrayList<>();
        URL = BASE_URL + movieId + REMAINING_CAST_URL + API_KEY;

        castRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                StringBuilder producer = new StringBuilder();
                StringBuilder director = new StringBuilder();
                try {
                    JSONArray castArray = response.getJSONArray("cast");
                    JSONObject castObject;

                    for (int i = 0; i < 10 && i < castArray.length(); i++) {
                        castObject = castArray.getJSONObject(i);

                        castCharacter = castObject.getString("character");
                        castName = castObject.getString("name");
                        castProfilePath = castObject.getString("profile_path");
                        castImageURL = BASE_IMAGE_URL + castProfilePath;
                        Cast cast = new Cast(castCharacter, castName, castImageURL);
                        casts.add(cast);
                    }

                    // To get director and producer
                    JSONArray crew = response.getJSONArray("crew");

                    for (int i = 0; i < crew.length(); i++) {
                        if (crew.getJSONObject(i).getString("job").equals("Producer")) {
                            if (producer.length() == 0)
                                producer.append(crew.getJSONObject(i).getString("name"));
                            else
                                producer.append(", " + crew.getJSONObject(i).getString("name"));
                        } else if (crew.getJSONObject(i).getString("job").equals("Director")) {
                            if (director.length() == 0)
                                director.append(crew.getJSONObject(i).getString("name"));
                            else
                                director.append(", " + crew.getJSONObject(i).getString("name"));
                        }
                    }
                    tvProducer.setText("Producer(s): " + producer.toString());
                    tvDirector.setText("Director(s): " + director.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setUpRecyclerView(casts);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.fillInStackTrace();
            }
        });
        // Send request to Volley and add it to requestQueue
        requestQueue.add(castRequest);
    }

    private void setUpRecyclerView(List<Cast> casts) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MovieAdapter(casts, this);
        recyclerView.setAdapter(adapter);
    }

    // To get imdb, rotten tomatoes and metacritic from OMDB api
    private void getRatings(String imdbId) {
        ratingRequest = new JsonObjectRequest(OMDB_URL + imdbId + OMDB_API, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    imdbRating = response.getString("imdbRating");
                    metacritic = response.getString("Metascore");
                    JSONArray rottenArray = response.getJSONArray("Ratings");
                    JSONObject rottenObject = rottenArray.getJSONObject(1);
                    if (rottenObject.getString("Source").equals("Rotten Tomatoes"))
                        rottenTomatoes = rottenObject.getString("Value");
                    else
                        rottenTomatoes = "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setRatings(imdbRating, rottenTomatoes, metacritic);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.fillInStackTrace();
            }
        });

        requestQueue.add(ratingRequest);
    }

    private void setRatings(String imdbRating, String rottenTomatoes, String metacritic) {
        if (imdbRating != null) {
            if (!imdbRating.equals("")) {
                tvImdb.setText(imdbRating);
            } else {
                tvImdb.setText("N/A");
                tvImdb.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
            }
        } else {
            tvImdb.setText("N/A");
            tvImdb.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
        }

        // To check if rotten tomatoes is available or not and if not assign rotten or not rotten
        tvRottenTomatoes.setText(rottenTomatoes);
        if (rottenTomatoes != null) {
            rottenTomatoes = rottenTomatoes.replaceAll("[^0-9]", "");
            int rating;
            if (!rottenTomatoes.equals("")) {
                rating = Integer.parseInt(rottenTomatoes);
                if (rating < 60)
                    tvRottenTomatoes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_rotten, 0, 0, 0);
                else
                    tvRottenTomatoes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_tomato, 0, 0, 0);

            } else {
                tvRottenTomatoes.setText("N/A");
                tvRottenTomatoes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_tomato, 0, 0, 0);
            }
        } else {
            tvRottenTomatoes.setText("N/A");
            tvRottenTomatoes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_tomato, 0, 0, 0);
        }

        if (metacritic != null && !metacritic.equals("")) {
            tvMetacritic.setText(metacritic);
            metacritic = metacritic.replaceAll("[^0-9]", "");
            int metascore = 0;
            if (!metacritic.equals("")) {
                metascore = Integer.parseInt(metacritic);
                if (metascore > 60)
                    tvMetacritic.setBackgroundColor(Color.parseColor("#65CC35"));
                else if (metascore > 40)
                    tvMetacritic.setBackgroundColor(Color.parseColor("#FCCE32"));
                else
                    tvMetacritic.setBackgroundColor(Color.parseColor("#FE0000"));
            } else {
                tvMetacritic.setText("N/A");
                tvMetacritic.setBackgroundColor(Color.parseColor("#FE0000"));
            }
        }

        relativeRating.setVisibility(View.VISIBLE);
    }
}
