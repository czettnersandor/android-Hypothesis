package com.czettner.hypothesis;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    private static final String RSS_URL = "http://thealternativehypothesis.org/index.php/feed/?paged=2";
    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<News> news = new ArrayList<>();

        getSupportLoaderManager().initLoader(URL_LOADER, null, this);

        news.add(new News("Title", "Lorem ipsum, dolor sit amet", "Category", "Author Joe", new Date(System.currentTimeMillis())));
        news.add(new News("Title", "Lorem ipsum, dolor sit amet", "Category", "Author Joe", new Date(System.currentTimeMillis())));

        NewsAdapter newsAdatper = new NewsAdapter(this, news);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(newsAdatper);
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                // Returns a new AsyncTaskLoader
                return new RssListLoader(this);
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> data) {

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {

    }

    public static class RssListLoader extends AsyncTaskLoader<ArrayList<News>> {
        public RssListLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<News> loadInBackground() {
            return null;
        }
    }
}
