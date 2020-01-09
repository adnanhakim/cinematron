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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<SearchItem> searchItems;
    private Context context;
    private RequestOptions option;

    public SearchAdapter(List<SearchItem> searchItems, Context context) {
        this.searchItems = searchItems;
        this.context = context;
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_image).error(R.drawable.loading_image);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SearchItem searchItem = searchItems.get(position);

        holder.tvTitle.setText(searchItem.getTitle());
        holder.tvYear.setText(searchItem.getYear());

        Glide.with(context).load(searchItem.getImageURL()).apply(option).into(holder.ivPoster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieActivity.class);
                intent.putExtra("ID", searchItem.getId());
                intent.putExtra("TITLE", searchItem.getTitle());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle, tvYear;
        public ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvSearchTitle);
            tvYear = itemView.findViewById(R.id.tvSearchYear);
            ivPoster = itemView.findViewById(R.id.ivSearchImage);
        }
    }
}
