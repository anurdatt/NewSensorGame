package org.adgames.newsensorgame;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class GameView extends View {
    private final float CONTROL_PANEL_HEIGHT = 150.0f;
    private final float DEFAULT_BALL_CENTER_SKEW_X = 0.0f;
    private final float DEFAULT_BALL_CENTER_SKEW_Y = 0.0f;
    private final float DEFAULT_BALL_DIAGONAL;
    /* access modifiers changed from: private */
    public final float DEFAULT_BALL_DIAMETER;
    private final float DEFAULT_BALL_HEIGHT = 75.0f;
    private final float DEFAULT_BALL_WIDTH = 75.0f;
    private final float DEFAULT_HOLE_CENTER_SKEW_X = -1.0873884f;
    private final float DEFAULT_HOLE_CENTER_SKEW_Y = 1.4690976f;
    private final float DEFAULT_HOLE_DIAMETER = 61.405464f;
    private final float DEFAULT_HOLE_HEIGHT = 108.58548f;
    private final float DEFAULT_HOLE_WIDTH = 116.41452f;
    private final float DEFAULT_TEXT_SIZE;
    private final int MAX_BALLS;
    /* access modifiers changed from: private */
    public int MAX_COLLIDE;
    private int NUM_BALLS;
    /* access modifiers changed from: private */
    public int REBOUND_DAMPING;
    private final float VIEW_MAX_X;
    private final float VIEW_MAX_Y;
    private final float VIEW_MIN_X = 0.0f;
    private final float VIEW_MIN_Y = 150.0f;
    /* access modifiers changed from: private */
    public boolean allHoled;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public List<Ball> balls;
    private long highestScore;
    private String highestScorer;
    /* access modifiers changed from: private */
    public boolean holeAnimationRunning;
    private Bitmap holeBM = null;
    private float holeCenterSkewX;
    private float holeCenterSkewY;
    private float holeScaledHeight;
    private float holeScaledWidth;
    /* access modifiers changed from: private */
    public final float holeX;
    /* access modifiers changed from: private */
    public final float holeY;
    /* access modifiers changed from: private */
    public boolean holed;
    /* access modifiers changed from: private */
    public List<Ball> holedBalls;
    /* access modifiers changed from: private */
    public final MediaPlayer mp;
    private final MediaPlayer mp2;
    private long playerHighestScore;
    private String playerName;
    private Paint rectPaint;
    /* access modifiers changed from: private */
    public long score;
    /* access modifiers changed from: private */
    public ObjectAnimator scoreAnimator;
    private Paint textPaint;
    CountDownTimer timer;
    /* access modifiers changed from: private */
    public long timerTimeLeftMilliSecs;
    /* access modifiers changed from: private */
    public long timerTimeLeftMins;
    /* access modifiers changed from: private */
    public long timerTimeLeftSecs;
    /* access modifiers changed from: private */
    public boolean timerTimedOut;
    /* access modifiers changed from: private */
    public final float xMax;
    /* access modifiers changed from: private */
    public final float xMin;
    /* access modifiers changed from: private */
    public final float yMax;
    /* access modifiers changed from: private */
    public final float yMin;

    private class LongEvaluator implements TypeEvaluator {
        private LongEvaluator() {
        }

        public Object evaluate(float fraction, Object startValue, Object endValue) {
            long startLong = ((Number) startValue).longValue();
            long ret = (long) (((float) startLong) + (((float) (((Number) endValue).longValue() - startLong)) * fraction));
            GameView.this.setScore(ret);
            return Long.valueOf(ret);
        }
    }

    public GameView(Context context, Point size, MediaPlayer mp, final MediaPlayer mp2, String playerName) {
        super(context);
        float sqrt = (float) Math.sqrt(11250.0d);
        this.DEFAULT_BALL_DIAGONAL = sqrt;
        this.DEFAULT_BALL_DIAMETER = sqrt - (0.456072f * sqrt);
        this.MAX_BALLS = 15;
        this.NUM_BALLS = 1;
        this.MAX_COLLIDE = 10;
        this.REBOUND_DAMPING = 5;
        this.allHoled = false;
        this.holeAnimationRunning = false;
        this.timerTimedOut = false;
        this.highestScore = 0;
        this.playerHighestScore = 0;
        this.highestScorer = "";
        this.playerName = playerName;
        this.VIEW_MAX_X = (float) size.x;
        float f = (float) size.y;
        this.VIEW_MAX_Y = f;
        this.xMin = 37.5f;
        this.yMin = 187.5f;
        float f2 = this.VIEW_MAX_X;
        this.xMax = f2 - 37.5f;
        this.yMax = f - 37.5f;
        this.holeX = (f2 + 0.0f) / 2.0f;
        this.holeY = 150.0f + (f - 150.0f) / 2.0f;
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.LightBlue)); //(R.color.LightBlue, (Resources.Theme) null));
        this.mp = mp;
        this.mp2 = mp2;
        Paint paint = new Paint(1);
        this.textPaint = paint;
        this.DEFAULT_TEXT_SIZE = paint.getTextSize();
        Paint paint2 = new Paint();
        this.rectPaint = paint2;
        paint2.setColor(ContextCompat.getColor(getContext(),R.color.DarkRed));
        initHoleDimentions();
        initHoleBM();
        float f3 = this.holeScaledWidth;
        ObjectAnimator holeAnimatorWidth = ObjectAnimator.ofFloat(this, "holeScaledWidth", new float[]{f3 - (f3 / 20.0f), 1.0f});
        float f4 = this.holeScaledHeight;
        ObjectAnimator holeAnimatorHeight = ObjectAnimator.ofFloat(this, "holeScaledHeight", new float[]{f4 - (f4 / 20.0f), 1.0f});
        holeAnimatorHeight.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
                mp2.start();
                mp2.seekTo(1000);
                boolean unused = GameView.this.holeAnimationRunning = true;
                Log.d("TEST", "Inside Hole Animation start - holeAnimationRunning = " + GameView.this.holeAnimationRunning);
            }

            public void onAnimationEnd(Animator animation) {
                if (mp2.isPlaying()) {
                    mp2.pause();
                }
                boolean unused = GameView.this.holeAnimationRunning = false;
                Log.d("TEST", "Inside Hole Animation stop - holeAnimationRunning = " + GameView.this.holeAnimationRunning);
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        this.scoreAnimator = ObjectAnimator.ofObject(this, "score", new LongEvaluator(), new Object[]{Long.valueOf(this.score)});
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animatorSet = animatorSet2;
        animatorSet2.play(holeAnimatorWidth).with(holeAnimatorHeight).with(this.scoreAnimator);
        this.balls = new ArrayList();
        this.holedBalls = new ArrayList();
        for (int i = 0; i < this.NUM_BALLS; i++) {
            this.balls.add(new Ball(i));
        }
        Log.d("TEST", "player name = " + playerName);
        initScores(playerName);
        setKeepScreenOn(true);
        initTimer();
    }

    public void updateBalls(float xAccel, float yAccel, float zAccel) {
        for (Ball ball : this.balls) {
            ball.checkBallCollision();
            int collide = ball.getCollide();
            if (collide >= 0) {
                PrintStream printStream = System.out;
                printStream.println("adding up effect of collision for id = " + ball.id);
                float unused = ball.xVel = ball.xVel + (((float) (collide / this.MAX_COLLIDE)) * ball.xVelCollision);
                float unused2 = ball.yVel = ball.yVel + (((float) (collide / this.MAX_COLLIDE)) * ball.yVelCollision);
                ball.setxAccel(xAccel);
                ball.setyAccel(yAccel);
                ball.setzAccel(zAccel);
                ball.setCollide(collide - 1);
            } else {
                ball.setxAccel(xAccel);
                ball.setyAccel(yAccel);
                ball.setzAccel(zAccel);
            }
            ball.updatePosition();
            ball.checkBoundaryCollision();
        }
        if (!this.allHoled && this.holedBalls.size() == this.NUM_BALLS) {
            this.allHoled = true;
        }
    }

    public void scaleHoleBM(Bitmap holeSrc) {
        Bitmap bitmap = this.holeBM;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.holeBM = Bitmap.createScaledBitmap(holeSrc, (int) this.holeScaledWidth, (int) this.holeScaledHeight, true);
    }

    public void initHoleBM() {
        scaleHoleBM(BitmapFactory.decodeResource(getResources(), R.drawable.hole2));
    }

    public void initHoleDimentions() {
        setHoleScaledWidth(116.41452f);
        setHoleScaledHeight(108.58548f);
        setHoleCenterSkewX(-1.0873884f);
        setHoleCenterSkewY(1.4690976f);
    }

    private void initTimer() {
        int i = this.NUM_BALLS;
        initTimer((long) ((i * 60000) - ((i / 15) * 60000)));
    }

    private void initTimer(long millisInFuture) {
        CountDownTimer timer = new CountDownTimer(millisInFuture, 10) {
            public void onTick(long millisUntilFinished) {
                long unused = GameView.this.timerTimeLeftMilliSecs = millisUntilFinished;
                long unused2 = GameView.this.timerTimeLeftMins = millisUntilFinished / 60000;
                GameView gameView = GameView.this;
                long unused3 = gameView.timerTimeLeftSecs = (millisUntilFinished / 1000) - (gameView.timerTimeLeftMins * 60);
            }

            public void onFinish() {
                boolean unused = GameView.this.timerTimedOut = true;
            }
        };
        this.timer = timer;
        timer.start();
    }

    /* access modifiers changed from: private */
    public void pauseTimer() {
        this.timer.cancel();
    }

    private void resumeTimer() {
        initTimer(this.timerTimeLeftMilliSecs);
    }

    private void startNewLevel() {
        this.holed = false;
        this.allHoled = false;
        this.balls.clear();
        for (Ball ball : this.holedBalls) {
            ball.initPosition();
            ball.initDynamics();
            ball.initDimentions();
            ball.initBM();
            this.balls.add(ball);
        }
        for (int i = this.balls.size(); i < this.NUM_BALLS; i++) {
            this.balls.add(new Ball(i));
        }
        this.holedBalls.clear();
        initHoleDimentions();
        initHoleBM();
        initTimer();
    }

    private void initScores(String playerName2) {
        Properties scores = new Properties();
        try {
            getContext().openFileOutput("scores.txt", Context.MODE_APPEND).close();
            FileInputStream fin = getContext().openFileInput("scores.txt");
            scores.load(fin);
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        for (String name : scores.stringPropertyNames()) {
            long score2 = Long.parseLong(scores.getProperty(name));
            if (score2 > this.highestScore) {
                this.highestScore = score2;
                this.highestScorer = name;
            }
        }
        String playerHighestScoreStr = scores.getProperty(playerName2, "0");
        this.playerName = playerName2;
        this.playerHighestScore = Long.parseLong(playerHighestScoreStr);
    }

    public boolean updateScores() {
        if (this.playerHighestScore >= this.score) {
            return false;
        }
        Properties scores = new Properties();
        try {
            FileInputStream fin = getContext().openFileInput("scores.txt");
            scores.load(fin);
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        PrintStream printStream = System.out;
        printStream.println("####################BEFORE UPDATE scores list = " + scores.toString());
        scores.setProperty(this.playerName, String.valueOf(this.score));
        PrintStream printStream2 = System.out;
        printStream2.println("####################AFTER UPDATE scores list = " + scores.toString());
        try {
            FileOutputStream fout = getContext().openFileOutput("scores.txt", Context.MODE_APPEND);
            scores.store(fout, "score update");
            fout.close();
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            FileInputStream fin2 = getContext().openFileInput("scores.txt");
            scores.load(fin2);
            fin2.close();
        } catch (FileNotFoundException e5) {
            e5.printStackTrace();
        } catch (IOException e6) {
            e6.printStackTrace();
        }
        PrintStream printStream3 = System.out;
        printStream3.println("####################AFTER UPDATE2 scores list = " + scores.toString());
        return true;
    }

    private void restartGame() {
        this.holed = false;
        this.allHoled = false;
        for (Ball ball : this.balls) {
            ball.destroy();
        }
        this.balls.clear();
        for (Ball ball2 : this.holedBalls) {
            ball2.destroy();
        }
        this.holedBalls.clear();
        for (int i = 0; i < this.NUM_BALLS; i++) {
            this.balls.add(new Ball(i));
        }
        initHoleDimentions();
        initHoleBM();
        initTimer();
        Toast.makeText(getContext(), "Going to update player scores. ", Toast.LENGTH_SHORT);
        if (updateScores()) {
            Toast.makeText(getContext(), "player score updated.. ", Toast.LENGTH_SHORT);
            long j = this.score;
            if (j > this.highestScore) {
                this.highestScore = j;
                this.highestScorer = this.playerName;
                Toast.makeText(getContext(), "highest score set.. ", Toast.LENGTH_SHORT);
            }
            this.score = 0;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = this.holeBM;
        canvas.drawBitmap(bitmap, (this.holeX - ((float) (bitmap.getWidth() / 2))) + this.holeCenterSkewX, (this.holeY - ((float) (this.holeBM.getHeight() / 2))) + this.holeCenterSkewY, (Paint) null);
        for (Ball ball : this.balls) {
            canvas.drawBitmap(ball.getBM(), (ball.getxPos() - ((float) (ball.getBM().getWidth() / 2))) + ball.getCenterSkewX(), (ball.getyPos() - ((float) (ball.getBM().getHeight() / 2))) + ball.getCenterSkewY(), (Paint) null);
        }
        this.textPaint.setTextSize(this.DEFAULT_TEXT_SIZE + 10.0f);
        this.textPaint.setColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        canvas.drawRect(0.0f, 0.0f, this.VIEW_MAX_X, 150.0f, this.rectPaint);
        canvas.drawText(String.format("Level - %02d", new Object[]{Integer.valueOf(this.NUM_BALLS)}), 30.0f, 35.0f, this.textPaint);
        canvas.drawText(String.format("Highest Score - %d", new Object[]{Long.valueOf(this.highestScore)}), 230.0f, 35.0f, this.textPaint);
        canvas.drawText(String.format("Level Score - %d", new Object[]{Long.valueOf(this.timerTimeLeftMilliSecs / 100)}), 510.0f, 35.0f, this.textPaint);
        canvas.drawText(String.format("Time Left - %02d:%02d", new Object[]{Long.valueOf(this.timerTimeLeftMins), Long.valueOf(this.timerTimeLeftSecs)}), 30.0f, 70.0f, this.textPaint);
        Object[] objArr = new Object[1];
        String str = this.highestScorer;
        int i = 10;
        if (str.length() <= 10) {
            i = this.highestScorer.length();
        }
        objArr[0] = str.substring(0, i);
        canvas.drawText(String.format("Highest Scorer - %s", objArr), 230.0f, 70.0f, this.textPaint);
        canvas.drawText(String.format("Score - %d", new Object[]{Long.valueOf(this.score)}), 510.0f, 70.0f, this.textPaint);
        this.textPaint.setTextSize(this.DEFAULT_TEXT_SIZE + 20.0f);
        this.textPaint.setColor(ContextCompat.getColor(getContext(), R.color.Lime));
        if (this.holed) {
            int holedBallCount = this.holedBalls.size();
            if (holedBallCount > 0) {
                Ball holedBall = this.holedBalls.get(holedBallCount - 1);
                if (!holedBall.animationRunning) {
                    holedBall.getBM().eraseColor(0);
                    if (this.allHoled && !this.holeAnimationRunning) {
                        this.holeBM.eraseColor(0);
                        if (this.NUM_BALLS < 15) {
                            canvas.drawText("Congrats, You did it!! - Tap to start new Level..", 30.0f, 115.0f, this.textPaint);
                        } else {
                            canvas.drawText("Super, You cleared all levels!! - Tap to Restart..", 30.0f, 115.0f, this.textPaint);
                        }
                    } else if (!this.allHoled) {
                        canvas.drawText("Spot on, Well done!! - Tap to Continue..", 30.0f, 115.0f, this.textPaint);
                    }
                }
            }
        } else if (this.timerTimedOut) {
            canvas.drawText("Ohhh, Sorry time ran out!! - Tap to Restart..", 30.0f, 115.0f, this.textPaint);
        }
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.holed) {
            int holedBallCount = this.holedBalls.size();
            if (holedBallCount > 0) {
                Ball holedBall = this.holedBalls.get(holedBallCount - 1);
                if (!holedBall.animationRunning) {
                    if (this.allHoled && !this.holeAnimationRunning) {
                        int i = this.NUM_BALLS;
                        if (i < 15) {
                            this.NUM_BALLS = i + 1;
                            startNewLevel();
                        } else {
                            this.NUM_BALLS = 1;
                            restartGame();
                        }
                    } else if (!this.allHoled) {
                        this.holed = false;
                        this.balls.remove(holedBall);
                        for (Ball ball : this.holedBalls) {
                            ball.initDynamics();
                        }
                        resumeTimer();
                    }
                }
            }
        } else if (this.timerTimedOut) {
            this.timerTimedOut = false;
            this.NUM_BALLS = 1;
            restartGame();
        }
        return super.onTouchEvent(event);
    }

    public float getHoleScaledWidth() {
        return this.holeScaledWidth;
    }

    public void setHoleScaledWidth(float holeScaledWidth2) {
        this.holeScaledWidth = holeScaledWidth2;
        if (this.holeAnimationRunning) {
            initHoleBM();
            setHoleCenterSkewX(-0.00934066f * holeScaledWidth2);
        }
    }

    public float getHoleScaledHeight() {
        return this.holeScaledHeight;
    }

    public void setHoleScaledHeight(float holeScaledHeight2) {
        this.holeScaledHeight = holeScaledHeight2;
        if (this.holeAnimationRunning) {
            initHoleBM();
            setHoleCenterSkewY(0.0135294115f * holeScaledHeight2);
        }
    }

    public float getHoleCenterSkewX() {
        return this.holeCenterSkewX;
    }

    public void setHoleCenterSkewX(float holeCenterSkewX2) {
        this.holeCenterSkewX = holeCenterSkewX2;
    }

    public float getHoleCenterSkewY() {
        return this.holeCenterSkewY;
    }

    public void setHoleCenterSkewY(float holeCenterSkewY2) {
        this.holeCenterSkewY = holeCenterSkewY2;
    }

    public long getScore() {
        return this.score;
    }

    public void setScore(long score2) {
        this.score = score2;
    }

    private class Ball {
        private Bitmap BM = null;
        /* access modifiers changed from: private */
        public boolean animationRunning;
        private ObjectAnimator animator;
        private float centerSkewX;
        private float centerSkewY;
        private int collide = 0;
        /* access modifiers changed from: private */
        public final int id;
        private float scaledDiameter;
        private float scaledHeight;
        private float scaledWidth;
        private float xAccel;
        private float xPos;
        /* access modifiers changed from: private */
        public float xVel = 0.0f;
        public float xVelCollision;
        private float yAccel;
        private float yPos;
        /* access modifiers changed from: private */
        public float yVel = 0.0f;
        public float yVelCollision = 0.0f;
        private float zAccel;
        private float zPos;
        private float zVel = 0.0f;

        public Ball(int id2) {
            this.id = id2;
            initPosition();
            initDimentions();
            initBM();
            float f = this.scaledWidth;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "scaledDiameter", new float[]{f - (f / 20.0f), 1.0f});
            this.animator = ofFloat;
            ofFloat.addListener(new Animator.AnimatorListener() {//GameView.this) {
                public void onAnimationStart(Animator animation) {
                    GameView.this.mp.start();
                    GameView.this.mp.seekTo(1000);
                    boolean unused = Ball.this.animationRunning = true;
                    Log.d("TEST", "animationRunning = " + Ball.this.animationRunning);
                }

                public void onAnimationEnd(Animator animation) {
                    if (GameView.this.mp.isPlaying()) {
                        GameView.this.mp.pause();
                    }
                    boolean unused = Ball.this.animationRunning = false;
                    Log.d("TEST", "animationRunning = " + Ball.this.animationRunning);
                    if (GameView.this.allHoled) {
                        GameView.this.scoreAnimator.setObjectValues(new Object[]{Long.valueOf(GameView.this.score), Long.valueOf(GameView.this.score + (GameView.this.timerTimeLeftMilliSecs / 100))});
                        GameView.this.animatorSet.setDuration(5000).start();
                    }
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
        }

        public void destroy() {
            Bitmap bitmap = this.BM;
            if (bitmap != null) {
                bitmap.recycle();
                this.BM = null;
            }
        }

        public void updatePosition() {
            if (!GameView.this.holed && !GameView.this.timerTimedOut) {
                if ((this.xPos < GameView.this.xMin && this.xVel > 0.0f) || (this.xPos > GameView.this.xMax && this.xVel < 0.0f)) {
                    this.xVel = 0.0f;
                    System.out.println("Inside update position id = " + this.id + ", I am setting x dynamics to 0");
                }
                if ((this.yPos < GameView.this.yMin && this.yVel > 0.0f) || (this.yPos > GameView.this.yMax && this.yVel < 0.0f)) {
                    this.yVel = 0.0f;
                    System.out.println("Inside update position id = " + this.id + ", I am setting y dynamics to 0");
                }
                float f = this.xVel + (this.xAccel * 0.666f);
                this.xVel = f;
                float f2 = this.yVel + (this.yAccel * 0.666f);
                this.yVel = f2;
                this.xPos -= (f / 2.0f) * 0.666f;
                this.yPos -= (f2 / 2.0f) * 0.666f;
                System.out.println("Inside update position id = " + this.id + ", xPos = " + this.xPos + ", yPos = " + this.yPos + ", xVel = " + this.xVel + ", yVel = " + this.yVel);
                checkHoled();
            }
        }

        public void checkHoled() {
            float ballRadius = GameView.this.DEFAULT_BALL_DIAMETER / 2.0f;
            if ((30.702732f - ballRadius) * (30.702732f - ballRadius) > ((GameView.this.holeX - this.xPos) * (GameView.this.holeX - this.xPos)) + ((GameView.this.holeY - this.yPos) * (GameView.this.holeY - this.yPos))) {
                PrintStream printStream = System.out;
                printStream.println("Good JOB!!<" + this.xPos + ", " + this.yPos + ">");
                GameView.this.holedBalls.add(this);
                this.animator.setDuration(3000).start();
                GameView.this.pauseTimer();
                boolean unused = GameView.this.holed = true;
            }
        }

        public void checkBallCollision() {
            Iterator it;
            float f;
            Vector2D pos1;
            Vector2D cd1;
            Iterator it2 = GameView.this.balls.iterator();
            while (it2.hasNext()) {
                Ball ball = (Ball) it2.next();
                if (ball.getId() > this.id) {
                    float dx = this.xPos - ball.xPos;
                    float dy = this.yPos - ball.yPos;
                    float dd = (dx * dx) + (dy * dy);
                    if (dd <= GameView.this.DEFAULT_BALL_DIAMETER * GameView.this.DEFAULT_BALL_DIAMETER) {
                        Vector2D c1 = new Vector2D(this.xPos, this.yPos);
                        Vector2D c2 = new Vector2D(ball.xPos, ball.yPos);
                        Vector2D u1 = new Vector2D(this.collide == GameView.this.MAX_COLLIDE ? this.xVelCollision : this.xVel, this.collide == GameView.this.MAX_COLLIDE ? this.yVelCollision : this.yVel);
                        Vector2D u2 = new Vector2D(ball.xVel, ball.yVel);
                        System.out.println("u1 = " + u1.toString());
                        System.out.println("u2 = " + u2.toString());
                        Vector2D cdiff1 = c1.subtract(c2);
                        Vector2D udiff1 = u1.subtract(u2);
                        Vector2D cdiff2 = c2.subtract(c1);
                        Vector2D udiff2 = u2.subtract(u1);
                        if (cdiff1.length() == 0.0f || cdiff2.length() == 0.0f) {
                            it = it2;
                            Toast.makeText(GameView.this.getContext(), "Inside cdiff1.length == 0", Toast.LENGTH_SHORT).show();
                            cdiff1.x = 1.0f;
                            cdiff2.x = -1.0f;
                            dd = 2.0f;
                        } else {
                            it = it2;
                        }
                        System.out.println("udiff1 = " + udiff1.toString() + ", udiff1.length = " + udiff1.length());
                        PrintStream printStream = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("cdiff1 = ");
                        float f2 = dx;
                        sb.append(cdiff1.toString());
                        sb.append(", cdiff1.length = ");
                        float f3 = dy;
                        sb.append(cdiff1.length());
                        printStream.println(sb.toString());
                        System.out.println("udiff2 = " + udiff2.toString() + ", udiff2.length = " + udiff2.length());
                        System.out.println("cdiff2 = " + cdiff2.toString() + ", cdiff2.length = " + cdiff2.length());
                        PrintStream printStream2 = System.out;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("udiff1, cdiff1 Dot product = ");
                        sb2.append(Vector2D.dotProduct(udiff1, cdiff1));
                        printStream2.println(sb2.toString());
                        System.out.println("udiff1, cdiff1 angle = " + Vector2D.getAngle(udiff1, cdiff1));
                        System.out.println("udiff2, cdiff2 Dot product = " + Vector2D.dotProduct(udiff2, cdiff2));
                        System.out.println("udiff2, cdiff2 angle = " + Vector2D.getAngle(udiff2, cdiff2));
                        System.out.println("cdiff1 = " + cdiff1.toString() + ", cdiff1.length = " + cdiff1.length());
                        float intermediate_scalar1a = cdiff1.length() * cdiff1.length();
                        System.out.println("intermediate_scalar1a = " + intermediate_scalar1a);
                        float intermediate_scalar1b = Vector2D.dotProduct(udiff1, cdiff1);
                        System.out.println("intermediate_scalar1b = " + intermediate_scalar1b);
                        float intermediate_scalar1c = intermediate_scalar1b / intermediate_scalar1a;
                        PrintStream printStream3 = System.out;
                        StringBuilder sb3 = new StringBuilder();
                        float f4 = intermediate_scalar1a;
                        sb3.append("intermediate_scalar1c = ");
                        sb3.append(intermediate_scalar1c);
                        printStream3.println(sb3.toString());
                        Vector2D intermediate_vector1 = cdiff1.multiply(intermediate_scalar1c);
                        PrintStream printStream4 = System.out;
                        StringBuilder sb4 = new StringBuilder();
                        float f5 = intermediate_scalar1b;
                        sb4.append("intermediate_vector1 = ");
                        sb4.append(intermediate_vector1.toString());
                        printStream4.println(sb4.toString());
                        Vector2D v1 = u1.subtract(intermediate_vector1);
                        PrintStream printStream5 = System.out;
                        StringBuilder sb5 = new StringBuilder();
                        Vector2D vector2D = intermediate_vector1;
                        sb5.append("v1 = ");
                        sb5.append(v1.toString());
                        printStream5.println(sb5.toString());
                        float intermediate_scalar2 = Vector2D.dotProduct(udiff2, cdiff2) / (cdiff2.length() * cdiff2.length());
                        PrintStream printStream6 = System.out;
                        StringBuilder sb6 = new StringBuilder();
                        float f6 = intermediate_scalar1c;
                        sb6.append("intermediate_scalar2 = ");
                        sb6.append(intermediate_scalar2);
                        printStream6.println(sb6.toString());
                        Vector2D intermediate_vector2 = cdiff2.multiply(intermediate_scalar2);
                        PrintStream printStream7 = System.out;
                        StringBuilder sb7 = new StringBuilder();
                        float f7 = intermediate_scalar2;
                        sb7.append("intermediate_vector2 = ");
                        sb7.append(intermediate_vector2.toString());
                        printStream7.println(sb7.toString());
                        Vector2D v2 = u2.subtract(intermediate_vector2);
                        PrintStream printStream8 = System.out;
                        StringBuilder sb8 = new StringBuilder();
                        Vector2D vector2D2 = intermediate_vector2;
                        sb8.append("v2 = ");
                        sb8.append(v2.toString());
                        printStream8.println(sb8.toString());
                        if (udiff1.length() == 0.0f || udiff2.length() == 0.0f) {
                            f = 0.0f;
                            this.xVelCollision = 0.0f;
                            ball.xVelCollision = 0.0f;
                            this.yVelCollision = 0.0f;
                            ball.yVelCollision = 0.0f;
                        } else {
                            this.xVelCollision = v1.x;
                            ball.xVelCollision = v2.x;
                            this.yVelCollision = v1.y;
                            ball.yVelCollision = v2.y;
                            f = 0.0f;
                        }
                        Vector2D v12 = v1.multiply(0.666f);
                        Vector2D v22 = v2.multiply(0.666f);
                        if (v12.length() != f) {
                            v12.normalize();
                            Vector2D vector2D3 = v12;
                        } else {
                            Vector2D vector2D4 = v12;
                            Toast.makeText(GameView.this.getContext(), "Inside v1.length == 0", Toast.LENGTH_SHORT).show();
                        }
                        if (v22.length() != 0.0f) {
                            v22.normalize();
                        } else {
                            Toast.makeText(GameView.this.getContext(), "Inside v2.length == 0", Toast.LENGTH_SHORT).show();
                        }
                        if (dd == 0.0f) {
                            u1 = u1.multiply(0.666f);
                            u2 = u2.multiply(0.666f);
                            u1.normalize();
                            u2.normalize();
                            cd1 = u1.multiply(GameView.this.DEFAULT_BALL_DIAMETER).multiply(-1.0f);
                            pos1 = u2.multiply(GameView.this.DEFAULT_BALL_DIAMETER).multiply(-1.0f);
                            Vector2D vector2D5 = v22;
                        } else {
                            Vector2D cd12 = new Vector2D(cdiff1.x, cdiff1.y);
                            Vector2D vector2D6 = v22;
                            Vector2D cd2 = new Vector2D(cdiff2.x, cdiff2.y);
                            cd12.normalize();
                            cd2.normalize();
                            cd1 = cd12.multiply(GameView.this.DEFAULT_BALL_DIAMETER);
                            pos1 = cd2.multiply(GameView.this.DEFAULT_BALL_DIAMETER);
                        }
                        float f8 = dd;
                        this.xPos += (cd1.x - cdiff1.x) / 2.0f;
                        Vector2D vector2D7 = cd1;
                        this.yPos += (cd1.y - cdiff1.y) / 2.0f;
                        ball.xPos += (pos1.x - cdiff2.x) / 2.0f;
                        ball.yPos += (pos1.y - cdiff2.y) / 2.0f;
                        PrintStream printStream9 = System.out;
                        StringBuilder sb9 = new StringBuilder();
                        sb9.append("Collission happened between = (");
                        sb9.append(this.id);
                        sb9.append(",");
                        sb9.append(ball.id);
                        sb9.append("), initial xVel = (");
                        sb9.append(this.xVel);
                        sb9.append(", ");
                        Vector2D vector2D8 = pos1;
                        sb9.append(ball.xVel);
                        sb9.append("), yVel1 = (");
                        sb9.append(this.yVel);
                        sb9.append(", ");
                        sb9.append(ball.yVel);
                        sb9.append(")");
                        printStream9.println(sb9.toString());
                        PrintStream printStream10 = System.out;
                        StringBuilder sb10 = new StringBuilder();
                        Vector2D vector2D9 = u1;
                        sb10.append("final xVelCollission = (");
                        sb10.append(this.xVelCollision);
                        sb10.append(",");
                        sb10.append(ball.xVelCollision);
                        sb10.append("), yVelCollision = (");
                        sb10.append(this.yVelCollision);
                        sb10.append(", ");
                        sb10.append(ball.yVelCollision);
                        sb10.append(")");
                        printStream10.println(sb10.toString());
                        PrintStream printStream11 = System.out;
                        StringBuilder sb11 = new StringBuilder();
                        sb11.append("initial xPos = (");
                        sb11.append(c1.x);
                        sb11.append(",");
                        sb11.append(c2.x);
                        sb11.append("), yPos = (");
                        Vector2D vector2D10 = u2;
                        sb11.append(c1.y);
                        sb11.append(", ");
                        sb11.append(c2.y);
                        sb11.append(")");
                        printStream11.println(sb11.toString());
                        System.out.println("final xPos = (" + this.xPos + "," + ball.xPos + "), yPos = (" + this.yPos + ", " + ball.yPos + ")");
                        this.yVel = 0.0f;
                        this.xVel = 0.0f;
                        ball.yVel = 0.0f;
                        ball.xVel = 0.0f;
                        setCollide(GameView.this.MAX_COLLIDE);
                        ball.setCollide(GameView.this.MAX_COLLIDE);
                    } else {
                        it = it2;
                        float f9 = dx;
                        float f10 = dy;
                    }
                    it2 = it;
                }
            }
        }

        public void checkBoundaryCollision() {
            if (Float.isNaN(this.xPos)) {
                System.out.println("Very bad!.. NaN in X");
                this.xPos = GameView.this.xMin;
            }
            if (Float.isNaN(this.yPos)) {
                System.out.println("Very bad!.. NaN in Y");
                this.yPos = GameView.this.yMin;
            }
            if (this.xPos > GameView.this.xMax || this.xPos < GameView.this.xMin || this.yPos > GameView.this.yMax || this.yPos < GameView.this.yMin) {
                boolean col_handled = false;
                if (this.xPos > GameView.this.xMax) {
                    this.xPos = GameView.this.xMax;
                    this.xVelCollision = (-this.xVel) / ((float) GameView.this.REBOUND_DAMPING);
                    this.yVelCollision = 0 != 0 ? this.yVelCollision : this.yVel;
                    col_handled = true;
                    PrintStream printStream = System.out;
                    printStream.println("id = " + this.id + " - I m in xPos>xMax, reverting xVel, xPos = " + this.xVel + ", " + this.xPos);
                    this.collide = GameView.this.MAX_COLLIDE;
                } else if (this.xPos < GameView.this.xMin) {
                    this.xPos = GameView.this.xMin;
                    this.xVelCollision = (-this.xVel) / ((float) GameView.this.REBOUND_DAMPING);
                    this.yVelCollision = 0 != 0 ? this.yVelCollision : this.yVel;
                    col_handled = true;
                    PrintStream printStream2 = System.out;
                    printStream2.println("id = " + this.id + " - I m in xPos<xMin, reverting xVel, xPos = " + this.xVel + ", " + this.xPos);
                    this.collide = GameView.this.MAX_COLLIDE;
                }
                if (this.yPos > GameView.this.yMax) {
                    this.yPos = GameView.this.yMax;
                    this.xVelCollision = col_handled ? this.xVelCollision : this.xVel;
                    this.yVelCollision = (-this.yVel) / ((float) GameView.this.REBOUND_DAMPING);
                    PrintStream printStream3 = System.out;
                    printStream3.println("id = " + this.id + " - I m in yPos>yMax, reverting yVel, yPos = " + this.yVel + ", " + this.yPos);
                    this.collide = GameView.this.MAX_COLLIDE;
                } else if (this.yPos < GameView.this.yMin) {
                    this.yPos = GameView.this.yMin;
                    this.xVelCollision = col_handled ? this.xVelCollision : this.xVel;
                    this.yVelCollision = (-this.yVel) / ((float) GameView.this.REBOUND_DAMPING);
                    PrintStream printStream4 = System.out;
                    printStream4.println("id = " + this.id + " - I m in yPos<yMin, reverting yVel, yPos= " + this.yVel + ", " + this.yPos);
                    this.collide = GameView.this.MAX_COLLIDE;
                }
                this.xVel = 0.0f;
                this.yVel = 0.0f;
            }
        }

        public void initPosition() {
            int i = this.id;
            if (i % 4 == 0) {
                this.xPos = GameView.this.xMin + (((float) Math.random()) * ((GameView.this.holeX - 61.405464f) - GameView.this.xMin));
                this.yPos = GameView.this.yMax - (((float) Math.random()) * (GameView.this.yMax - (GameView.this.holeY + 61.405464f)));
            } else if (i % 4 == 1) {
                this.xPos = GameView.this.xMax - (((float) Math.random()) * (GameView.this.xMax - (GameView.this.holeX + 61.405464f)));
                this.yPos = GameView.this.yMin + (((float) Math.random()) * ((GameView.this.holeY - 61.405464f) - GameView.this.yMin));
            } else if (i % 4 == 2) {
                this.xPos = GameView.this.xMin + (((float) Math.random()) * ((GameView.this.holeX - 61.405464f) - GameView.this.xMin));
                this.yPos = GameView.this.yMin + (((float) Math.random()) * ((GameView.this.holeY - 61.405464f) - GameView.this.yMin));
            } else if (i % 4 == 3) {
                this.xPos = GameView.this.xMax - (((float) Math.random()) * (GameView.this.xMax - (GameView.this.holeX + 61.405464f)));
                this.yPos = GameView.this.yMax - (((float) Math.random()) * (GameView.this.yMax - (GameView.this.holeY + 61.405464f)));
            }
        }

        public void initDynamics() {
            this.xAccel = 0.0f;
            this.yAccel = 0.0f;
            this.xVel = 0.0f;
            this.yVel = 0.0f;
        }

        public void initDimentions() {
            setScaledWidth(75.0f);
            setScaledHeight(75.0f);
            setCenterSkewX(0.0f);
            setCenterSkewY(0.0f);
        }

        public void initBM() {
            scaleBM(BitmapFactory.decodeResource(GameView.this.getResources(), R.drawable.ball));
        }

        public void scaleBM(Bitmap BMSrc) {
            Bitmap bitmap = this.BM;
            if (bitmap != null) {
                bitmap.recycle();
            }
            this.BM = Bitmap.createScaledBitmap(BMSrc, (int) this.scaledWidth, (int) this.scaledHeight, true);
        }

        public int getId() {
            return this.id;
        }

        public float getScaledWidth() {
            return this.scaledWidth;
        }

        public void setScaledWidth(float scaledWidth2) {
            this.scaledWidth = scaledWidth2;
        }

        public float getScaledHeight() {
            return this.scaledHeight;
        }

        public void setScaledHeight(float scaledHeight2) {
            this.scaledHeight = scaledHeight2;
        }

        public float getScaledDiameter() {
            return this.scaledDiameter;
        }

        public void setScaledDiameter(float scaledDiameter2) {
            this.scaledDiameter = scaledDiameter2;
            setScaledWidth(scaledDiameter2);
            setScaledHeight(scaledDiameter2);
            if (this.animationRunning) {
                initBM();
                setCenterSkewX(this.scaledWidth * 0.0f);
                setCenterSkewY(this.scaledHeight * 0.0f);
            }
        }

        public float getCenterSkewX() {
            return this.centerSkewX;
        }

        public void setCenterSkewX(float centerSkewX2) {
            this.centerSkewX = centerSkewX2;
        }

        public float getCenterSkewY() {
            return this.centerSkewY;
        }

        public void setCenterSkewY(float centerSkewY2) {
            this.centerSkewY = centerSkewY2;
        }

        public Bitmap getBM() {
            return this.BM;
        }

        public void setBM(Bitmap BM2) {
            this.BM = BM2;
        }

        public float getxAccel() {
            return this.xAccel;
        }

        public void setxAccel(float xAccel2) {
            this.xAccel = xAccel2;
        }

        public float getyAccel() {
            return this.yAccel;
        }

        public void setyAccel(float yAccel2) {
            this.yAccel = yAccel2;
        }

        public float getxPos() {
            return this.xPos;
        }

        public void setxPos(float xPos2) {
            this.xPos = xPos2;
        }

        public float getyPos() {
            return this.yPos;
        }

        public void setyPos(float yPos2) {
            this.yPos = yPos2;
        }

        public float getzPos() {
            return this.zPos;
        }

        public void setzPos(float zPos2) {
            this.zPos = zPos2;
        }

        public float getzAccel() {
            return this.zAccel;
        }

        public void setzAccel(float zAccel2) {
            this.zAccel = zAccel2;
        }

        public float getzVel() {
            return this.zVel;
        }

        public void setzVel(float zVel2) {
            this.zVel = zVel2;
        }

        public int getCollide() {
            return this.collide;
        }

        public void setCollide(int collide2) {
            this.collide = collide2;
        }
    }
}
