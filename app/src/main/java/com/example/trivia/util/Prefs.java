package com.example.trivia.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences sharedPreferences;

    public Prefs(Activity activity){
        this.sharedPreferences = activity.getPreferences(activity.MODE_PRIVATE);
    }
    public void saveHighScore(int score){
        int currentScore = score;
        int last = sharedPreferences.getInt("high_score",0);
        if(currentScore>last) {
            sharedPreferences.edit().putInt("high_score", currentScore).apply();
        }
    }
    public int getHighScore(){
        return sharedPreferences.getInt("high_score",0);
    }
    public void setState(int index){
        sharedPreferences.edit().putInt("index",index).apply();
    }
    public int getState(){
        return sharedPreferences.getInt("index",0);
    }
    public void setScores(int score){
        sharedPreferences.edit().putInt("score",score).apply();
    }
    public int getScores(){
        return sharedPreferences.getInt("score",0);
    }
}
