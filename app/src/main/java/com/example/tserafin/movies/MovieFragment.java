package com.example.tserafin.movies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.tserafin.movies.adapter.ImageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
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
import java.util.List;

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

        movieDetails = new ArrayList<>();
        movieAdapter = new ImageAdapter(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                retrieveMovies();
                Log.v("TESTING", "adapter count:" + movieAdapter.getCount());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        //click listener for going to other activity
        return rootView;
    }



    private void retrieveMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(new Pair<>("api_key", getString(R.string.api_key)));
    }

    public class FetchMoviesTask extends AsyncTask<Pair<String,String>, Void, List<Drawable>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPostExecute(List<Drawable> drawables) {
            if (drawables != null) {
                Log.v(LOG_TAG, "Images retrieved: " + drawables);
                movieAdapter.setImages(drawables);
            }
        }

        @Override
        protected List<Drawable> doInBackground(Pair<String, String>... params) {
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
                return getImagesFromJson(jsonResponse, 20, apiKey);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing JSON", e);
            }
            return null;
        }

        private List<Drawable> getImagesFromJson(String movieJsonStr, int numMovies, String apiKey) throws JSONException {
            //JSON Objects
            final String TMDB_results = "results";
            final String TMDB_poster = "poster_path";

            //Store JSON results for later use (details screen)
            movieDetails.clear();

            //Store images and load into adapter after all retrieved
            List<Drawable> savedImages = new ArrayList<>();

            //Parse JSON
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray moviesArray = movieJson.getJSONArray(TMDB_results);
            for(int i=0;(i<numMovies)&&(i<moviesArray.length());i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                movieDetails.add(movie);
                String movieImagePath = movie.getString(TMDB_poster).replace("/","");
                //Request to get Image

                try {
                    // Set up query
                    URL url;
                    if (apiKey != null) {
                        String baseUrl = "http://image.tmdb.org/t/p/w150";
                        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
                        builder.appendPath(movieImagePath);
                        url = new URL(builder.build().toString());
                    } else {
                        throw new MalformedURLException("Missing API Key");
                    }
                    savedImages.add(loadImage(url, movieImagePath));
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Incorrectly formed URL", e);
                    return null;
                }
            }
            return savedImages;
        }

        private Drawable loadImage(URL imageUrl, String destinationFile){
            InputStream in = null;
            BufferedInputStream is = null;
            File cacheDir = getContext().getCacheDir();
            File destFile = new File(cacheDir, destinationFile);
            Drawable image = null;
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
                in = urlConnection.getInputStream();
                is = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(is);

                image = new BitmapDrawable(getResources(), bMap);
            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return image;
        }
    }
}
