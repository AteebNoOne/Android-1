package com.example.brickbreakergame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback, Runnable, View.OnTouchListener {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint;
    private boolean play;
    private int score;
    public String name;
    private int totalBricks;
    private Handler handler;
    private int delay;
    private int playerX;
    private int ballposX;
    private int ballposY;
    private int ballXdir;
    private int ballYdir;
    private MapGenerator map;

    public GameActivity() {
        this.play = false;
        this.score = 0;
        this.totalBricks = 48;
        this.playerX = 310;
        this.ballposX = 120;
        this.ballposY = 350;
        this.ballXdir = -1;
        this.ballYdir = -2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        setContentView(R.layout.activity_gameplay);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceView.setOnTouchListener(this);
        paint = new Paint();
        handler = new Handler();
        delay = 8;
        map = new MapGenerator(4, 12);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        handler.postDelayed(this, delay);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        handler.removeCallbacks(this);
    }

    @Override
    public void run() {
        update();
        draw();
        handler.postDelayed(this, delay);
    }

    private void update() {
        if (play) {
            ballposX += ballXdir * 2;
            ballposY += ballYdir * 3;
            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > surfaceView.getWidth() - 20) {
                ballXdir = -ballXdir;
            }

            int ballCenterX = ballposX + 10;
            int paddleCenterX = playerX + 50;
            int distanceX = Math.abs(ballCenterX - paddleCenterX);
            if (distanceX <= 60 && ballYdir > 0) {
                int ballCenterY = ballposY + 10;
                int paddleTopY = surfaceView.getHeight() - 50;
                int distanceY = Math.abs(ballCenterY - paddleTopY);
                if (distanceY <= 10) {
                    ballYdir = -ballYdir;
                }
            }

            if (ballposY > surfaceView.getHeight()) {
                // Ball has missed the paddle, game over
                play = false;
                handler.removeCallbacks(this);
                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("score", score);
                startActivity(intent);
                saveScore(GameActivity.this, name, score);
                finish();
            }

            loop:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;

                        int brickY = i * map.brickHeight + 150;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;
                        Rect brickRect = new Rect(brickX, brickY, brickX + brickWidth, brickY + brickHeight);
                        Rect ballRect = new Rect(ballposX, ballposY, ballposX + 20, ballposY + 20);

                        if (brickRect.intersect(ballRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballposX + 19 <= brickRect.left || ballposX + 1 >= brickRect.right) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }
                            break loop;
                        }
                    }
                }
            }

            if (totalBricks == 0) {
                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("score", score);
                startActivity(intent);
                saveScore(GameActivity.this, name, score);
                finish();
            }
        }
    }


    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            paint.setTextSize(30);
            canvas.drawText("Score: " + score, 10, 30, paint);
            canvas.drawText("Player: " + name, surfaceView.getWidth() - 250, 30, paint);
            canvas.drawRect(playerX, surfaceView.getHeight() - 50, playerX + 100, surfaceView.getHeight() - 42, paint);
            paint.setColor(Color.RED);
            canvas.drawCircle(ballposX, ballposY, 20, paint);
            paint.setColor(Color.YELLOW);
            map.draw(canvas, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                play = true;
                float touchX = event.getX();
                if (touchX < surfaceView.getWidth() / 2) {
                    playerX -= 20;
                    if (playerX < 0) {
                        playerX = 0;
                    }
                } else {
                    playerX += 20;
                    if (playerX > surfaceView.getWidth() - 100) {
                        playerX = surfaceView.getWidth() - 100;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // handle when the user stops touching the screen
                break;
        }
        return true;
    }

    public static void saveScore(Context context , String name, int score) {
        try {
            File file = new File(context.getFilesDir(), "hsfile.bb");
            if (!file.exists()) {
                file.createNewFile();
            }
            Path filePath = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                filePath = file.toPath();
            }
            boolean isNamePresent = false;
            // Check if name already exists in file
            BufferedReader reader = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                reader = new BufferedReader(new FileReader(filePath.toFile()));
            }
            StringBuilder stringBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(name)) {
                    int existingScore = Integer.parseInt(parts[1]);
                    // Update the score if the new score is higher
                    line = name + ":" + (existingScore+score);

                    isNamePresent = true;
                }
                stringBuilder.append(line + "\n");
                line = reader.readLine();
            }
            reader.close();
            // Add the new name and score if it is not already present in the file
            if (!isNamePresent) {
                stringBuilder.append(name + ":" + score + "\n");
            }

            // Write the updated high scores to the file
            FileWriter writer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                writer = new FileWriter(filePath.toFile());
            }
            writer.write(stringBuilder.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}