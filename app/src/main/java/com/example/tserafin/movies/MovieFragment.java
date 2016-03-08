package com.example.tserafin.movies;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private ArrayAdapter<Image> movieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO: populate movies
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
        return inflater.inflate(R.layout.fragment_main, container, false);


    }

    private void retrieveMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        //TODO: Execute FetchMoviesTask
    }

    public class FetchMoviesTask extends AsyncTask<Pair<String,String>, Void, String> {

        @Override
        protected String doInBackground(Pair<String, String>... params) {
            return null;
            //TODO: Implement API call
        }
    }
}
