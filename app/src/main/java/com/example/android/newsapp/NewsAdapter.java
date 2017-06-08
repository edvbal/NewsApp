package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Edvinas on 08/06/2017.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private Context context;
    private List<News> newsList;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView sectionName;
        private TextView articleName;

        public ViewHolder(View view){
            super(view);
            sectionName = (TextView) view.findViewById(R.id.section_name);
            articleName = (TextView) view.findViewById(R.id.article_name);
        }
    }

    public NewsAdapter(Context context, List<News> news) {
        this.context = context;
        this.newsList = news;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recyclerItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_recycler_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(recyclerItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        final News currentNews = newsList.get(position);
        holder.sectionName.setText(currentNews.getSectionName());
        holder.articleName.setText(currentNews.getArticleName());
        holder.sectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentNews.getUrl()));
                context.startActivity(i);
            }
        });
        holder.articleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentNews.getUrl()));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {return newsList.size();}
}
