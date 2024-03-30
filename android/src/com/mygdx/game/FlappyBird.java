package com.mygdx.game;

import android.net.sip.SipSession;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background, bird, topTube, bottomTube;
    Texture[] birds;
    int currentBirdIndex = 0;
    float birdY = 0;
    float velocity = 1;
    int gap = 400;
    int gameState = 1;

    Tube[] tubes;
    int tubeCount = 4;
    float distanceBetweenTubes;
    float[] tubeX = new float[tubeCount];
    float[] topTubeY = new float[tubeCount];

    int score = 0;
    int scoringTube = 0;
    BitmapFont font;

    Texture replayButton;
    BitmapFont gameOverFont;
    Sound passTubeSound;


    @Override
    public void create () {
        batch = new SpriteBatch();
        background = new Texture("background.png");

        birds = new Texture[3];
        birds[0] = new Texture("bird1.png");
        birds[1] = new Texture("bird2.png");
        birds[2] = new Texture("bird3.png");
        topTube = new Texture("top.png");
        bottomTube = new Texture("bot.png");

        replayButton = new Texture("replay.png");
        gameOverFont = new BitmapFont();
        gameOverFont.getData().setScale(10);

        passTubeSound = Gdx.audio.newSound(Gdx.files.internal("soundEffect.mp3"));

        distanceBetweenTubes = Gdx.graphics.getWidth() /3;
        tubes = new Tube[tubeCount];
        for (int i = 0; i < tubeCount; i++) {
            float topTubeY = Gdx.graphics.getHeight() / 2 + gap / 2 + MathUtils.random(-200, 200);
            float bottomTubeY = Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + MathUtils.random(-200, 200);
            tubes[i] = new Tube(i * distanceBetweenTubes + Gdx.graphics.getWidth(), topTubeY, bottomTubeY);
        }
        birdY = Gdx.graphics.getHeight() / 2 - birds[currentBirdIndex].getHeight() / 2;

        font = new BitmapFont();
        font.getData().setScale(10);
    }

    @Override
    public void render() {

        if (Gdx.input.justTouched()) {
            birdY += (Gdx.graphics.getHeight() / 10 - birds[currentBirdIndex].getHeight() / 10);

        }

        if (gameState != 0) {
            birdY -= velocity / 10 * ((Gdx.graphics.getHeight() / 20 - birds[currentBirdIndex].getHeight() / 20));
            batch.begin();


            if (birdY <= 0 || birdY >= Gdx.graphics.getHeight() - birds[currentBirdIndex].getHeight()) {
                Gdx.app.log("GameOver", "Game Over");
                gameState = 0;
            }

            currentBirdIndex++;
            if (currentBirdIndex >= birds.length) {
                currentBirdIndex = 0;
            }
            ScreenUtils.clear(1, 0, 0, 1);
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.draw(birds[currentBirdIndex], Gdx.graphics.getWidth() / 3 - birds[currentBirdIndex].getWidth() / 3, birdY);

            for (int i = 0; i < tubeCount; i++) {
                batch.draw(topTube, tubes[i].x, tubes[i].topTubeY);
                batch.draw(bottomTube, tubes[i].x, tubes[i].bottomTubeY);
                tubes[i].x -= 4;
                if (tubes[i].x < -topTube.getWidth()) {
                    float maxTubeX = 0;
                    for (Tube tube : tubes) {
                        maxTubeX = Math.max(maxTubeX, tube.x);
                    }
                    tubes[i].x = maxTubeX + distanceBetweenTubes;

                    float randomY = MathUtils.random(-200, 200);
                    float birdHeight = birds[currentBirdIndex].getHeight();
                    float minGap = 2 * birdHeight;

                    tubes[i].topTubeY = Gdx.graphics.getHeight() / 2 + gap / 2 + Math.max(randomY, minGap / 2);
                    tubes[i].bottomTubeY = tubes[i].topTubeY - gap - bottomTube.getHeight();

                }
                if (tubes[i].x + topTube.getWidth() < Gdx.graphics.getWidth() / 3 - birds[currentBirdIndex].getWidth() / 3 && i != scoringTube) {
                    score++;
                    scoringTube = i;
                    passTubeSound.play();

                }
                float birdX = Gdx.graphics.getWidth() / 3 - birds[currentBirdIndex].getWidth() / 3;
                float birdWidth = birds[currentBirdIndex].getWidth();
                float birdHeight = birds[currentBirdIndex].getHeight();

                if ((birdX + birdWidth > tubes[i].x && birdX < tubes[i].x + topTube.getWidth()) &&
                        birdY + birdHeight > tubes[i].topTubeY) {
                    gameState = 0;
                }
                if ((birdX + birdWidth > tubes[i].x && birdX < tubes[i].x + topTube.getWidth()) &&
                        birdY < tubes[i].bottomTubeY + bottomTube.getHeight()) {
                    gameState = 0;
                }
            }
            font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 50);
            if (birdY <= 0 || birdY >= Gdx.graphics.getHeight() - birds[currentBirdIndex].getHeight()) {
                gameState = 0;
            }
            batch.end();
        }
        else {


            batch.begin();
            gameOverFont.draw(batch, "GameOver", Gdx.graphics.getWidth() / 2 - 400, Gdx.graphics.getHeight() - 50);
            batch.draw(replayButton, Gdx.graphics.getWidth() / 2 - replayButton.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            batch.end();

            if (Gdx.input.justTouched()) {
                float touchX = Gdx.input.getX();
                float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Y-coordinate is inverted in LibGDX
                float replayButtonX = Gdx.graphics.getWidth() / 2 - replayButton.getWidth() / 2;
                float replayButtonY = Gdx.graphics.getHeight() / 2;

                if (touchX >= replayButtonX && touchX <= replayButtonX + replayButton.getWidth() &&
                        touchY >= replayButtonY && touchY <= replayButtonY + replayButton.getHeight()) {
                    gameState = 1;

                    birdY = Gdx.graphics.getHeight() / 2 - birds[currentBirdIndex].getHeight() / 2;

                    score = 0;
                    scoringTube = 0;

                    for (int i = 0; i < tubeCount; i++) {
                        float topTubeY = Gdx.graphics.getHeight() / 2 + gap / 2 + MathUtils.random(-200, 200);
                        float bottomTubeY = Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + MathUtils.random(-200, 200);
                        tubes[i] = new Tube(i * distanceBetweenTubes + Gdx.graphics.getWidth(), topTubeY, bottomTubeY);
                    }

                    ScreenUtils.clear(1, 0, 0, 1);
                    batch.begin();
                    batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    batch.draw(birds[currentBirdIndex], Gdx.graphics.getWidth() / 3 - birds[currentBirdIndex].getWidth() / 3, birdY);
                    batch.end();

                }
            }
        }
    }

    @Override
    public void dispose () {
        batch.dispose();
    }
}