package com.example.androidgame;

import android.view.MotionEvent;

import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // creating the variables for the frame
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;

    // creating variables for all the images located within the 'drawable' file
    private ImageView character, rocket, green, red; // all the images
    private Drawable imageCharacterRight, imageCharacterLeft; // mario looking left and right

    // Size of the mario character
    private int characterSize;

    // Position
    private float characterX, characterY; // for the character
    private float rocketX, rocketY; // for the rocket
    private float greenX, greenY; // green mushroom
    private float redX, redY; // red mushroom

    // creating the variable for the Score system
    private TextView scoreLabel, highScoreLabel;
    private int score, highScore, timeCount;
    private SharedPreferences settings;

    // Class
    private Timer timer; // timer
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer; // play sound player

    // Status
    private boolean start_function = false;
    private boolean action_flg = false;
    private boolean red_flg = false;

// linking the names of the variables to the buttons and images
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        // this is linking the names of the variables to its dedicated button and image
        gameFrame = findViewById(R.id.gameFrame); // frame
        startLayout = findViewById(R.id.startLayout); // the layout
        character = findViewById(R.id.character); // mario
        rocket = findViewById(R.id.rocket); // rocket
        green = findViewById(R.id.green); // green mushroom
        red = findViewById(R.id.red); // red mushroom
        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);

        imageCharacterLeft = getResources().getDrawable(R.drawable.character_left);
        imageCharacterRight = getResources().getDrawable(R.drawable.character_right);

        // High Score
        // Saving the highest score
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score : " + highScore);
    }

    public void changePos() {

        // Adding a 'timeCount'
        timeCount += 20;

        //The creation of the green mushroom
        greenY += 12;

        float greenCenterY = greenY + green.getHeight() / 2;

        float greenCenterX = greenX + green.getWidth() / 2;

        //this is implementing how much the green mushroom is when collecting it
        if (hitCheck(greenCenterX, greenCenterY)) {
            greenY = frameHeight + 100; // frame height
            score += 10; // worth 10 points
            soundPlayer.playHitGreenSound(); // play the the green sound
        }

        // sets random coordinates
        if (greenY > frameHeight) {
            greenY = -100;
            greenX = (float) Math.floor(Math.random() * (frameWidth - green.getWidth()));
        }
        green.setX(greenX);
        green.setY(greenY);

        // Red mushroom
        if (!red_flg && timeCount % 10000 == 0) {
            red_flg = true;
            redY = -20;
            redX = (float) Math.floor(Math.random() * (frameWidth - red.getWidth()));
        }

        if (red_flg) {
            redY += 20;

            float redCenterX = redX + red.getWidth() / 2;
            float redCenterY = redY + red.getWidth() / 2;


            if (hitCheck(redCenterX, redCenterY)) { //adding the point system for the red mushroom
                redY = frameHeight + 30;
                score += 30; // 30 points for thr red mushroom
                // Change FrameWidth
                if (initialFrameWidth > frameWidth * 110 / 100) {
                    frameWidth = frameWidth * 110 / 100;
                    changeFrameWidth(frameWidth);
                }
                soundPlayer.playHitRedSound(); // plays the red sound file
            }

            if (redY > frameHeight) red_flg = false;
            red.setX(redX);
            red.setY(redY);
        }

        // Rocket
        rocketY += 18;

        float rocketCenterX = rocketX + rocket.getWidth() / 2;
        float rocketCenterY = rocketY + rocket.getHeight() / 2;

        if (hitCheck(rocketCenterX, rocketCenterY)) {
            rocketY = frameHeight + 100;

            // Change FrameWidth for the rocket
            frameWidth = frameWidth * 80 / 100;
            changeFrameWidth(frameWidth);
            soundPlayer.playHitRocketSound();
            if (frameWidth <= characterSize) {
                gameOver();
            }

        }
// if its greater it reduces the Y coordinate
        if (rocketY > frameHeight) {
            rocketY = -100;
            rocketX = (float) Math.floor(Math.random() * (frameWidth - rocket.getWidth()));
        }
// setting the variables for X and Y
        rocket.setX(rocketX);
        rocket.setY(rocketY);

        // Move Character
        if (action_flg) {
            // Touching
            characterX += 14;
            character.setImageDrawable(imageCharacterRight); //adding the image moving right
        } else {
            // Releasing
            characterX -= 14;
            character.setImageDrawable(imageCharacterLeft); // adding the image looking left
        }

        // Check character position.
        if (characterX < 0) {
            characterX = 0;
            character.setImageDrawable(imageCharacterRight); // image for looking right
        }
        if (frameWidth - characterSize < characterX) {
            characterX = frameWidth - characterSize;
            character.setImageDrawable(imageCharacterLeft);
        }

        character.setX(characterX);

        scoreLabel.setText("Score : " + score);

    }

    public boolean hitCheck(float x, float y) {
        if (characterX <= x && x <= characterX + characterSize &&
                characterY <= y && y <= frameHeight) {
            return true;
        }
        return false;
    }

    public void changeFrameWidth(int frameWidth) {
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);
    }

    public void gameOver() {
        // Stop timer.
        timer.cancel();
        timer = null;
        start_function = false;

        // Before showing the start layout, it will sleep for 1 second.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);

        startLayout.setVisibility(View.VISIBLE);
        character.setVisibility(View.INVISIBLE);
        rocket.setVisibility(View.INVISIBLE);
        green.setVisibility(View.INVISIBLE);
        red.setVisibility(View.INVISIBLE);

        // Updates the High Score
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.commit();
        }
    }

    @Override
    // when moving the character left and right
    public boolean onTouchEvent(MotionEvent event) {
        if (start_function) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;

            }
        }
        return true;
    }

    public void startGame(View view) {
        start_function = true;
        startLayout.setVisibility(View.INVISIBLE);

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            characterSize = character.getHeight();
            characterX = character.getX();
            characterY = character.getY();
        }

        frameWidth = initialFrameWidth;

        character.setX(0.0f);
        green.setY(3000.0f);
        rocket.setY(3000.0f);
        red.setY(3000.0f);

        greenY = green.getY();
        rocketY = rocket.getY();
        redY = red.getY();

        // making the character, mushrooms and rocket to be visible once clicking to play the game
        character.setVisibility(View.VISIBLE); // character
        green.setVisibility(View.VISIBLE);
        rocket.setVisibility(View.VISIBLE);
        red.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        scoreLabel.setText("Score : 0"); // setting the score to be 0


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_function) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }

    public void quitGame(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

}
