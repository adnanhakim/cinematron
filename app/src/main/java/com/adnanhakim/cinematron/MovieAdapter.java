package com.adnanhakim.cinematron;

import android.content.Context;
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

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Cast> casts;
    private Context context;
    private RequestOptions option;

    public MovieAdapter(List<Cast> casts, Context context) {
        this.casts = casts;
        this.context = context;

        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_image).error(R.drawable.loading_image);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cast cast = casts.get(position);

        holder.tvCharacter.setText(cast.getCharacter());
        holder.tvName.setText(cast.getName());

        Glide.with(context).load(cast.getImageURL()).apply(option).into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return casts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivImage;
        public TextView tvCharacter, tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivCastImage);
            tvCharacter = itemView.findViewById(R.id.tvCastCharacter);
            tvName = itemView.findViewById(R.id.tvCastName);
        }
    }
}
