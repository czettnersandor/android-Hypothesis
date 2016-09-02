package com.czettner.hypothesis;

import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class QueryUtils {

    private static final int TAG_TITLE = 1;
    private static final int TAG_PUBLISHED = 2;
    private static final int TAG_DESCRIPTION = 3;
    private static final int TAG_CATEGORY = 4;
    private static final int TAG_AUTHOR = 5;
    private static final int TAG_LINK = 6;
    // We don't use XML namespaces
    private static final String ns = null;
    private static final String LOG_TAG = "QueryUtils";

    public static ArrayList<News> queryNews(String urlFormat, int pagination) {
        try {
            ArrayList<News> response = makeHttpRequest(createUrl(urlFormat));
            return response;
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage());
            // TODO
        }
        return null;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * @param url URL
     * @return String as a response
     * @throws IOException
     */
    private static ArrayList<News> makeHttpRequest(URL url) throws IOException {
        ArrayList<News> response;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            response = rssInputStreamParse(inputStream);
        } catch (IOException e) {
            // TODO: Handle the exception
            Log.d(LOG_TAG, e.getMessage());
            response = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return response;
    }

    /**
     * Returns new URL object from the given string URL.
     * @param stringUrl Url ni String format
     * @return URL object
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, e.getMessage());
            return null;
        }
        return url;
    }

    private static ArrayList<News> rssInputStreamParse(InputStream is)
            throws IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag();
            int eventType = parser.getEventType();
            return readFeed(parser);
        } catch (XmlPullParserException e) {
            // TODO
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
            return null;
        } finally {
            is.close();
        }
    }

    /**
     * Decode a feed attached to an XmlPullParser.
     *
     * @param parser Incoming XMl
     * @return ArrayList of News
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException on I/O error.
     */
    private static ArrayList<News> readFeed(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        ArrayList<News> entries = new ArrayList<News>();

        // Search for <feed> tags. These wrap the beginning/end of an Atom document.
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            // Starts by looking for the <channel> tag. This tag is inside of <rss>
            // For each article, there's an <item> in the feed.
            parser.require(XmlPullParser.START_TAG, ns, "channel");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String itemName = parser.getName();
                if (itemName.equals("item")) {
                    entries.add(readEntry(parser));
                } else {
                    skip(parser);
                }
            }
        }
        return entries;
    }

    /**
     * Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
     * off to their respective "read" methods for processing. Otherwise, skips the tag.
     */
    private static News readEntry(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String description = null;
        String category = null;
        String author = null;
        String link = null;
        Date publishedOn = null;
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")){
                title = readTag(parser, TAG_TITLE);
            } else if (name.equals("description")) {
                description = readTag(parser, TAG_DESCRIPTION);
            } else if (name.equals("category")) {
                category = readTag(parser, TAG_CATEGORY);
            } else if (name.equals("dc:creator")) {
                author = readTag(parser, TAG_AUTHOR);
            } else if (name.equals("link")) {
                String tempLink = readTag(parser, TAG_LINK);
                if (tempLink != null) {
                    link = tempLink;
                }
            } else if (name.equals("pubDate")) {
                try {
                    publishedOn = formatter.parse(readTag(parser, TAG_PUBLISHED));
                } catch (java.text.ParseException e) {
                    publishedOn = null;
                }

            } else {
                skip(parser);
            }
        }
        return new News(title, description, category, author, link, publishedOn);
    }

    /**
     * Process an incoming tag and read the selected value from it.
     */
    private static String readTag(XmlPullParser parser, int tagType)
            throws IOException, XmlPullParserException {

        switch (tagType) {
            case TAG_TITLE:
                return readBasicTag(parser, "title");
            case TAG_PUBLISHED:
                return readBasicTag(parser, "pubDate");
            case TAG_DESCRIPTION:
                return readBasicTag(parser, "description");
            case TAG_CATEGORY:
                return readBasicTag(parser, "category");
            case TAG_AUTHOR:
                return readBasicTag(parser, "dc:creator");
            case TAG_LINK:
                return readBasicTag(parser, "link");
            default:
                throw new IllegalArgumentException("Unknown tag type: " + tagType);
        }
    }

    /**
     * Reads the body of a basic XML tag, which is guaranteed not to contain any nested elements.
     *
     * <p>You probably want to call readTag().
     *
     * @param parser Current parser object
     * @param tag XML element tag name to parse
     * @return Body of the specified tag
     * @throws java.io.IOException
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    private static String readBasicTag(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }

    /**
     * For the tags title and summary, extracts their text values.
     */
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
     * if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
     * finds the matching END_TAG (as indicated by the value of "depth" being 0).
     */
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
