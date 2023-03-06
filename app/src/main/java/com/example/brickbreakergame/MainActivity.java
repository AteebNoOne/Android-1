package com.example.brickbreakergame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private LinearLayout panel;
    private TextView heading;
    private Button highScoresButton, playButton, backButton;
    private EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        panel = findViewById(R.id.panel);
        heading = findViewById(R.id.heading);
        highScoresButton = findViewById(R.id.highScoresButton);
        playButton = findViewById(R.id.playButton);
        nameField = findViewById(R.id.nameField);
        backButton = findViewById(R.id.backButton);

        // Set the heading text style
        heading.setTextSize(40);
        heading.setTextColor(Color.BLACK);
        heading.setGravity(Gravity.CENTER);

        // Set the button texts
        highScoresButton.setText("HighScores");
        playButton.setText("Play");
        backButton.setText("Back");

        // Set button click listeners
        highScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameField.setVisibility(View.GONE);
                showHighScores();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the nameField visibility to VISIBLE
                nameField.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                highScoresButton.setVisibility(View.GONE);
                String name = nameField.getText().toString().trim();

                // Validate the name input (not empty and no numbers)
                if (name.isEmpty() || name.matches(".*\\d.*")) {
                    Toast.makeText(MainActivity.this, "Please enter a valid name (numbers not allowed).", Toast.LENGTH_LONG).show();
                } else {
                    // Start the gameplay with the entered name
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setVisibility(View.GONE);
                nameField.setVisibility(View.GONE);
                nameField.setText("");
                highScoresButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.VISIBLE);
            }
        });

        // Hide the nameField initially
        nameField.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
    }

    private void showHighScores() {
        Intent intent = new Intent(this, HighScoresActivity.class);

        // Pass the high scores data to the intent as a string array
        ArrayList<String[]> highScores = new ArrayList<>();
        // Check if file exists
        File file = new File(getFilesDir(), "hsfile.bb");
        if (!file.exists()) {
            // If file does not exist, add a "Game was never played" message to high scores list
            highScores.add(new String[]{"Game was never played", ""});
        } else {
            // Load high scores from file
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    String[] score = line.split(":");
                    highScores.add(score);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Sort the high scores by score descending
        Collections.sort(highScores, new Comparator<String[]>() {
            public int compare(String[] o1, String[] o2) {
                return Integer.parseInt(o2[1]) - Integer.parseInt(o1[1]);
            }
        });

        // Get the top ten high scores
        int numHighScoresToShow = Math.min(10, highScores.size());
        ArrayList<String[]> topHighScores = new ArrayList<>(highScores.subList(0, numHighScoresToShow));

        String[][] highScoresArray = new String[topHighScores.size()][2];
        for (int i = 0; i < topHighScores.size(); i++) {
            highScoresArray[i][0] = topHighScores.get(i)[0];
            highScoresArray[i][1] = topHighScores.get(i)[1];
        }
        intent.putExtra("highScores", highScoresArray);

        // Start the new activity
        startActivity(intent);
    }


}
