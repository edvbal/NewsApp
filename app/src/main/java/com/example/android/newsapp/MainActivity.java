package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.newsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    // @LOG_TAG for logs
    public static final String LOG_TAG = MainActivity.class.getName();

    // URL for news data from Guardian API
    private static final String NEWS_URL =
            "https://content.guardianapis.com/search?q=sport&api-key=test";

    private RecyclerView.LayoutManager layoutManager;
    private ActivityMainBinding binding;
    private NewsAdapter newsAdapter;
    private Boolean isInternetConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        newsAdapter = new NewsAdapter(getApplicationContext(), new ArrayList<News>());
        layoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(layoutManager);
        binding.progressBar.setVisibility(View.VISIBLE);

        isInternetConnected = checkIfInternetConnected();
        startLoader(isInternetConnected);
        /*
        * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
        * performs a swipe-to-refresh gesture.
        */
        binding.swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        newsAdapter = new NewsAdapter(getApplicationContext(), new ArrayList<News>());
                        binding.recycler.setAdapter(newsAdapter);
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        isInternetConnected = checkIfInternetConnected();
                        startLoader(isInternetConnected);
                    }
                }
        );

    }

    private void startLoader(Boolean isInternetConnected) {
        if (isInternetConnected) {
            newsAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());
            binding.recycler.setAdapter(newsAdapter);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, MainActivity.this);

        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.problemText.setVisibility(View.VISIBLE);
            binding.problemText.setText(R.string.no_internet);
        }

    }

    private boolean checkIfInternetConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            binding.swipeRefresh.setRefreshing(false);
            return false;
        }
    }

    public static class NewsLoader extends AsyncTaskLoader<List<News>> {
        // Tag for log messages
        private final String LOG_TAG = NewsLoader.class.getName();

        private String url;

        /**
         * Constructs a new {@link NewsLoader}.
         *
         * @param context Context of the activity
         * @param url     url of query
         */
        public NewsLoader(Context context, String url) {
            super(context);
            this.url = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<News> loadInBackground() {
            if (url == null || url.isEmpty())
                return null;
            List<News> newsList = QueryUtils.fetchNewsData(url, this.getContext());
            return newsList;
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, NEWS_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        // Set unnecessary views invisible
        binding.progressBar.setVisibility(View.GONE);
        binding.problemText.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
        newsAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());
        if (newsList != null && !newsList.isEmpty()) {
            newsAdapter = new NewsAdapter(MainActivity.this, newsList);
            binding.recycler.setAdapter(newsAdapter);

        } else {
            binding.problemText.setText(R.string.no_news_found);
            binding.problemText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
    }
}
