package com.czettner.hypothesis;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    // With pagination: http://thealternativehypothesis.org/index.php/feed/?paged=2
    private static final String RSS_URL = "http://thealternativehypothesis.org/index.php/feed/";
    private static final int URL_LOADER = 0;
    private static final String LOG_TAG = "MainActivity.LOG_TAG";

    private NewsAdapter newsAdatper;
    private ListView listView;
    private ArrayList<News> news;
    private TextView nodataText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        news = new ArrayList<>();

        newsAdatper = new NewsAdapter(this, news);
        listView = (ListView) findViewById(R.id.list_view);
        nodataText = (TextView) findViewById(R.id.no_data);
        nodataText.setVisibility(View.GONE);
        listView.setAdapter(newsAdatper);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = news.get(position).getLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

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
        news.clear();
        if (data != null) {
            news.addAll(data);
        }
        runOnUiThread(new Runnable() {
            public void run() {
                newsAdatper.notifyDataSetChanged();
            }
        });
        if (news.size() == 0) {
            nodataText.setVisibility(View.VISIBLE);
        } else {
            nodataText.setVisibility(View.GONE);
        }
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
