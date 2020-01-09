package com.adnanhakim.cinematron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String API_KEY = BuildConfig.API_KEY;
    public final String BASE_URL = "https://api.themoviedb.org/3";
    public final String REMAINING_URL = "/day?api_key=";
    public final String PAGE_URL = "&page=";

    public String CRITERIA = "/trending";
    public String TYPE = "/movie";

    public String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    public String URL;
    public int PAGE = 1;

    private Toolbar toolbar;
    private TextView tvHeader;
    private RelativeLayout relativeButtonLayout;
    private Button btnPrevious, btnNext;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerViewMain, recyclerViewSearch;
    private MainAdapter mainAdapter;
    private SearchAdapter searchAdapter;
    public List<Movie> movies;
    public List<SearchItem> searchItems;
    private JsonObjectRequest request, searchRequest;
    private RequestQueue requestQueue, searchRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        relativeButtonLayout.setVisibility(View.INVISIBLE);
        recyclerViewSearch.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar);

        jsonRequest(TYPE, PAGE);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonRequest(TYPE, ++PAGE);
                nestedScrollView.smoothScrollTo(0, 0);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonRequest(TYPE, --PAGE);
                nestedScrollView.smoothScrollTo(0, 0);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                jsonRequest(TYPE, PAGE);
            }
        });
    }

    private void initialize() {
        tvHeader = findViewById(R.id.tvMainHeader);
        toolbar = findViewById(R.id.toolbarMain);
        recyclerViewMain = findViewById(R.id.recyclerMain);
        recyclerViewSearch = findViewById(R.id.recyclerSearch);
        btnNext = findViewById(R.id.btnMainNext);
        btnPrevious = findViewById(R.id.btnMainPrevious);
        nestedScrollView = findViewById(R.id.nestedSVMain);
        swipeRefresh = findViewById(R.id.swipeRefreshMain);
        relativeButtonLayout = findViewById(R.id.relativeMainPage);
    }

    // Criteria is Trending and Type is Movies/TV Series
    private void jsonRequest(final String type, int page) {
        if (PAGE == 1)
            btnPrevious.setVisibility(View.INVISIBLE);
        else
            btnPrevious.setVisibility(View.VISIBLE);

        movies = new ArrayList<>();
        URL = BASE_URL + CRITERIA + type + REMAINING_URL + API_KEY + PAGE_URL + page;
        request = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    relativeButtonLayout.setVisibility(View.VISIBLE);
                    swipeRefresh.setRefreshing(false);
                    int page = response.getInt("page");
                    JSONArray movieArray = response.getJSONArray("results");
                    JSONObject movieObject;

                    // Traversing 20 results on trending page
                    for (int i = 0; i < movieArray.length(); i++) {
                        movieObject = movieArray.getJSONObject(i);
                        int id = movieObject.getInt("id");
                        String title;
                        if (type.equals("/tv"))
                            title = movieObject.getString("name");
                        else
                            title = movieObject.getString("title");
                        String genre = movieObject.getString("overview");
                        double vote_average = movieObject.getDouble("vote_average");
                        String rating = "" + vote_average;
                        String posterPath = movieObject.getString("poster_path");
                        String imageURL = BASE_IMAGE_URL + posterPath;
                        movies.add(new Movie(id, title, genre, rating, imageURL));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setUpRecyclerView(movies);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefresh.setRefreshing(false);
                error.fillInStackTrace();
                Toast.makeText(MainActivity.this, "No internet :(", Toast.LENGTH_SHORT).show();
            }
        });
        // Send request to Volley and add it to requestQueue
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

    private void setUpRecyclerView(List<Movie> movies) {
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        mainAdapter = new MainAdapter(movies, this);
        recyclerViewMain.setAdapter(mainAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_search, menu);

        MenuItem item = menu.findItem(R.id.menuMainSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search for a movie");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    recyclerViewMain.setVisibility(View.INVISIBLE);
                    recyclerViewSearch.setVisibility(View.VISIBLE);
                    searchMovie(newText);
                } else {
                    recyclerViewMain.setVisibility(View.VISIBLE);
                    recyclerViewSearch.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvHeader.setVisibility(View.INVISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tvHeader.setVisibility(View.VISIBLE);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void searchMovie(String movieText) {
        // Search query
        searchItems = new ArrayList<>();
        String query = movieText.replace(" ", "%20");
        String url = BASE_URL + "/search/movie?api_key=" + API_KEY + "&query=" + query + "&page=1";

        searchRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject movie = results.getJSONObject(i);
                        int id = movie.getInt("id");
                        String title = movie.getString("original_title");
                        String releaseDate = movie.getString("release_date");
                        String year = "";
                        if(!releaseDate.equals(""))
                            year = releaseDate.substring(0, 4);
                        String posterPath = movie.getString("poster_path");
                        String imageURL = BASE_IMAGE_URL + posterPath;

                        SearchItem item = new SearchItem(id, title, year, imageURL);
                        searchItems.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setUpSearchRecyclerView(searchItems);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        searchRequestQueue = Volley.newRequestQueue(MainActivity.this);
        searchRequestQueue.add(searchRequest);
    }

    private void setUpSearchRecyclerView(List<SearchItem> searchItems) {
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(searchItems, this);
        recyclerViewSearch.setAdapter(searchAdapter);
    }
}