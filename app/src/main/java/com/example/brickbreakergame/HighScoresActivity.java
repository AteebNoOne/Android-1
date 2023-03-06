package com.example.brickbreakergame;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Comparator;

public class HighScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        setContentView(R.layout.activity_high_scores);

        // Retrieve the high scores data from the intent
        String[][] highScores = (String[][]) getIntent().getSerializableExtra("highScores");

        if (highScores == null || highScores.length == 0) {
            // If there are no high scores to display, show a message
            TextView noScoresLabel = new TextView(this);
            noScoresLabel.setText("There are no high scores to display.");
            noScoresLabel.setTextColor(Color.BLACK);
            noScoresLabel.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            noScoresLabel.setLayoutParams(params);
            setContentView(noScoresLabel);
        } else {
            // Sort the high scores by score descending
            Arrays.sort(highScores, new Comparator<String[]>() {
                public int compare(String[] o1, String[] o2) {
                    return Integer.parseInt(o2[1]) - Integer.parseInt(o1[1]);
                }
            });

            // Display the high scores
            LinearLayout panel = findViewById(R.id.high_scores_panel);
            for (String[] score : highScores) {
                String highScoreText = getResources().getString(R.string.high_scores_format, score[0], score[1]);
                TextView scoreLabel = new TextView(this);
                scoreLabel.setText(highScoreText);
                scoreLabel.setTextSize(20); // set text size
                scoreLabel.setTextColor(Color.RED); // set text color
                scoreLabel.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                scoreLabel.setLayoutParams(params);
                panel.addView(scoreLabel);
            }
        }
    }
}
