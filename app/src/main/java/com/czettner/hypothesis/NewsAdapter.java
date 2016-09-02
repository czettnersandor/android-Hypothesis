package com.czettner.hypothesis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    private final static String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * Constructor
     * @param context   Context
     * @param news      News
     */
    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.description);
        TextView tvCategory = (TextView) convertView.findViewById(R.id.category);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.author);
        TextView tvPublishedDate = (TextView) convertView.findViewById(R.id.published_date);

        tvTitle.setText(news.getTitle());
        tvDescription.setText(news.getDescription());
        tvCategory.setText(news.getCategory());
        tvAuthor.setText(news.getAuthor());

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        if (news.getPublishedDate() != null) {
            tvPublishedDate.setText(formatter.format(news.getPublishedDate()));
        } else {
            tvPublishedDate.setText(R.string.no_date);
        }

        return convertView;
    }
}
