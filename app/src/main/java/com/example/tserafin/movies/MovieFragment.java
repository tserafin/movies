package com.example.tserafin.movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.example.tserafin.movies.adapter.ImageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private ArrayList<JSONObject> movieDetails;
    private ImageAdapter movieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveMovies();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
        //TODO: Menu stuff
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieDetails = new ArrayList<>();
        movieAdapter = new ImageAdapter(
                getContext()
        );

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        //click listener for going to other activity

        return rootView;
    }

    private void retrieveMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(new Pair<>("api_key", getString(R.string.api_key)));
    }

    public class FetchMoviesTask extends AsyncTask<Pair<String,String>, Void, Void> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        @Override
        protected Void doInBackground(Pair<String, String>... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResponse = null;
            String apiKey = null;

            try {
                // Set up query
                URL url;
                if (params.length > 0) {
                    String baseUrl = "http://api.themoviedb.org/3/movie/popular";
                    Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
                    for (Pair<String, String> param: params) {
                        builder.appendQueryParameter(param.first, param.second);
                        if (param.first.equals("api_key")) {
                            apiKey = param.second;
                        }
                    }
                    url = new URL(builder.build().toString());
                } else {
                    throw new MalformedURLException("Missing API Key");
                }
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonResponse = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getImagesFromJson(jsonResponse, 20, apiKey);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing JSON", e);
            }
            return null;
        }

//        @Override
//        protected void onPostExecute() {
//            //TODO: traverse through downloaded images directory, set reference array to ids of all files
//        }

        private void getImagesFromJson(String movieJsonStr, int numMovies, String apiKey) throws JSONException {
            //JSON Objects
            final String TMDB_results = "results";
            final String TMDB_poster = "poster_path";

            //Store JSON results for later use (details screen)
            movieDetails.clear();

            //Parse JSON
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray moviesArray = movieJson.getJSONArray(TMDB_results);
            for(int i=0;(i<numMovies)&&(i<moviesArray.length());i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                movieDetails.add(movie);
                String movieImagePath = movie.getString(TMDB_poster);
                //Request to get Image

                try {
                    // Set up query
                    URL url;
                    if (apiKey != null) {
                        String baseUrl = "http://http://image.tmdb.org/t/p/w150";
                        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
                        builder.appendPath(movieImagePath);
                        url = new URL(builder.build().toString());
                    } else {
                        throw new MalformedURLException("Missing API Key");
                    }
                    saveImage(url, "test.jpg");
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Incorrectly formed URL", e);
                    return;
                }
            }
        }

        private void saveImage(URL imageUrl, String destinationFile){
            InputStream is = null;
            OutputStream os = null;
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
                is = urlConnection.getInputStream();
                os = new FileOutputStream(destinationFile);
                byte[] b = new byte[2048];
                int length;

                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (is != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
