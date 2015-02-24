package com.railzapp.tours;

import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.railzapp.tours.model.tour.Tour;
import com.railzapp.tours.model.tour.ToursDataSource;

public class TestDatabaseActivity extends ListActivity {
    private ToursDataSource datasource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);

        datasource = new ToursDataSource(this);
        datasource.open();

        List<Tour> values = datasource.getAllTours();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<Tour> adapter = new ArrayAdapter<Tour>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    // Will be called via the onClick attribute
    // of the buttons in main.xml
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Tour> adapter = (ArrayAdapter<Tour>) getListAdapter();
        Tour tour = null;
        switch (view.getId()) {
            case R.id.add:
                // Add some random data
                String[] code = new String[] { "BE_BRU_1", "BE_BRU_2", "BE_BRU_3" };
                String[] name = new String[] { "Beers of Brussels", "Sweet choco", "Downtown" };
                String[] duration = new String[] { "4h", "2h", "5min" };
                String[] complexity = new String[] { "Medium", "Easy", "Brain dumb" };
                String[] shortDesc = new String[] { "Short desc 1", "Short desc 2", "Short desc 3" };
                String[] longDesc = new String[] { "Cool", "Very nice", "Hate it" };
                int nextInt = new Random().nextInt(3);
                // save the new tour to the database
                tour = datasource.createTour(code[nextInt], name[nextInt], duration[nextInt], complexity[nextInt], shortDesc[nextInt], longDesc[nextInt]);
                adapter.add(tour);
                break;
            case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    tour = (Tour) getListAdapter().getItem(0);
                    datasource.deleteTour(tour);
                    adapter.remove(tour);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

} 
