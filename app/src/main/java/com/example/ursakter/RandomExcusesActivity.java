package com.example.ursakter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import custom.views.MagicPager;
import custom.views.PreviousButton;
import custom.views.RatingButton;

public class RandomExcusesActivity  extends FragmentActivity implements ExcuseFragment.OnFragmentInteractionListener{
    private PreviousButton previousButton;
    private RatingButton ratingButton;
    private ArrayList<Excuse> excuses;
    private Excuse current;
    private int lastPos = 0;
    private DBHandler dbHandler = new DBHandler(this);
    private MagicPager randomPager;
    private ExcusePagerAdapter excusePagerAdapter;
    private PageListener pageListener;
    private Random random;
    int newRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_excuses);
        initDB();
        ratingButton = (RatingButton) findViewById(R.id.rating_button);

        excuses = dbHandler.getAllExcuses();

        random = new Random();

        randomPager = (MagicPager)findViewById(R.id.pagerz);
        excusePagerAdapter = new ExcusePagerAdapter(getSupportFragmentManager());
        excusePagerAdapter.setExcuses(excuses);
        randomPager.setAdapter(excusePagerAdapter);

        pageListener = new PageListener();
        randomPager.setOnPageChangeListener(pageListener);

        ratingButton.setCurrentRating(excuses.get(0).getApprovals());
        ratingButton.invalidate();
        current = excuses.get(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(int position){
        randomPager.setCurrentItem(position);
    }


    private void initDB(){
        try {
            dbHandler.createDB();
            dbHandler.openDatabase();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadNewExcuse(View view){
        do{
            newRandom = random.nextInt(excuses.size());
        }while(newRandom == lastPos);

        randomPager.setCurrentItem(newRandom);
    }

    public void rateCurrent(View view){
        current.setApprovals(current.getApprovals() + 1);
        ratingButton.setCurrentRating(current.getApprovals());
        saveCurrent();
        ratingButton.invalidate();
    }

    private void saveCurrent(){
        dbHandler.updateExcuse(current);
    }

    public void mainMenu(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private class PageListener extends MagicPager.SimpleOnPageChangeListener{
        public void onPageSelected(int position){
            lastPos = position;
            current = excuses.get(position);
            ratingButton.setCurrentRating(excuses.get(position).getApprovals());
            ratingButton.invalidate();
        }
    }
}
