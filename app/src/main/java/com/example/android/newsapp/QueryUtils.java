package com.example.android.newsapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Edvinas on 08/06/2017.
 */

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getName();

    public static List<News> fetchNewsData(String requestUrl, Context context) {
        // Create URL object
        URL url = createURL(requestUrl, context);
        // Perform HTTPS request and receive a JSON response back
        String jsonResponse = null;
        jsonResponse = makeHttpsRequest(url, context);
        // Extract relevant fields from the JSON response and create an (@link News) object
        List<News> newsList = extractFromJSON(jsonResponse, context);
        return newsList;
    }

    private static List<News> extractFromJSON(String jsonResponse, Context context) {
        if (TextUtils.isEmpty(jsonResponse))
            return null;
        // Create an empty ArrayList that we can start adding books to
        ArrayList<News> newsList = new ArrayList<>();
        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // formatted, a JSONException object will be thrown.
        try {
            // build up a list of News objects with the corresponding data.
            JSONObject baseJsonObject = new JSONObject(jsonResponse);
            JSONObject responseJsonObject = baseJsonObject.getJSONObject("response");
            JSONArray rootArray = responseJsonObject.getJSONArray("results");
            for (int i = 0; i < rootArray.length(); i++) {
                JSONObject newsJsonObject = rootArray.getJSONObject(i);
                String articleName = "";
                String sectionName = "";
                String url = "";
                articleName = newsJsonObject.getString("webTitle");
                sectionName = newsJsonObject.getString("sectionName");
                url = newsJsonObject.getString("webUrl");
                News news = new News(sectionName, articleName, url);
                newsList.add(news);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_json_parse), e);
            Toast.makeText(context.getApplicationContext(),
                    context.getString(R.string.problem_json_parse), Toast.LENGTH_SHORT).show();
        }
        return newsList;
    }

    private static String makeHttpsRequest(URL url, Context context) {
        String jsonResponse = null;
        // If the url is null then return from the method
        if (url == null)
            return jsonResponse;
        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successfull (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, context.getString(R.string.https_error) + urlConnection.getResponseCode());
                Toast.makeText(context.getApplicationContext(),
                        context.getString(R.string.https_error), Toast.LENGTH_SHORT).show();
            }
        } catch (ProtocolException e) {
            Log.e(LOG_TAG, context.getString(R.string.protocol_error), e);
            Toast.makeText(context.getApplicationContext(),
                    context.getString(R.string.protocol_error), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.protocol_error), e);
            Toast.makeText(context.getApplicationContext(),
                    context.getString(R.string.protocol_error), Toast.LENGTH_SHORT).show();
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static URL createURL(String requestUrl, Context context) {
        URL url = null;
        if (!requestUrl.isEmpty()) {
            try {
                url = new URL(requestUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error with creating URL", e);
                Toast.makeText(context.getApplicationContext(), "Error with creating URL", Toast.LENGTH_SHORT).show();
            }
        }
        return url;
    }
}
