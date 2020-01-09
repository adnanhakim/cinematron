package com.adnanhakim.cinematron;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    public List<Movie> movies;
    public Context context;
    public RequestOptions option;

    public MainAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;

        // Request option for Glide
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_image).error(R.drawable.loading_image);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Movie movie = movies.get(position);

        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());
        holder.tvRating.setText(movie.getImdbRating());

        // Load image from the internet and set it into ImageView using Glide
        Glide.with(context).load(movies.get(position).getImageURL()).apply(option).into(holder.ivImage);

        // On click listener for opening movie details page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieActivity.class);
                intent.putExtra("ID", movie.getId());
                intent.putExtra("TITLE", movie.getTitle());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle, tvOverview, tvRating;
        public ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvOverview = itemView.findViewById(R.id.tvMovieOverview);
            tvRating = itemView.findViewById(R.id.tvMovieIMDBRating);
            ivImage = itemView.findViewById(R.id.ivMovieImage);
        }
    }
}
