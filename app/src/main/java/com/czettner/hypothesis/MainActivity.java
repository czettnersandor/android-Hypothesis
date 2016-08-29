package com.czettner.hypothesis;

import android.app.DownloadManager;
import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    private static final String RSS_URL = "http://thealternativehypothesis.org/index.php/feed/?paged=2";
    private static final int URL_LOADER = 0;
    private static final String LOG_TAG = "MainActivity.LOG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<News> news = new ArrayList<>();

        news.add(new News("Title", "Lorem ipsum, dolor sit amet", "Category", "Author Joe", new Date(System.currentTimeMillis())));
        news.add(new News("Title", "Lorem ipsum, dolor sit amet", "Category", "Author Joe", new Date(System.currentTimeMillis())));

        NewsAdapter newsAdatper = new NewsAdapter(this, news);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(newsAdatper);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(URL_LOADER, null, this).forceLoad();
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                // Returns a new AsyncTaskLoader
                Log.d(LOG_TAG, "onCreateLoader");
                return new RssListLoader(this);
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> data) {
        ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
    }

    /**
     * Async task loader to load RSS data
     */
    public static class RssListLoader extends AsyncTaskLoader<ArrayList<News>> {
        public RssListLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<News> loadInBackground() {
            Log.d(LOG_TAG, "loadInBackground");
            return QueryUtils.queryNews(RSS_URL, 0);
        }
    }
}
