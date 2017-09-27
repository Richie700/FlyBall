package com.rair.flyball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rair.flyball.MainActivity;

import java.util.ArrayList;

/**
 * Created by Rair on 2016/11/29.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder sfh;
    private Paint paint;

    private Thread th;
    private boolean flag;

    private Canvas canvas;
    private static int screenW, screenH;

    private static final int GAME_MENU = 0;
    private static final int GAMEING = 1;
    private static final int GAME_OVER = -1;

    private static int gameState = GAME_MENU;

    private int[] floor = new int[2];
    private int floor_width = 15;

    private int speed = 3;

    private int[] level = new int[2];
    private int level_value = 0;

    private int[] bird = new int[2];
    private int bird_width = 10;
    private int bird_v = 0;
    private int bird_a = 2;
    private int bird_vUp = -16;

    private ArrayList<int[]> walls = new ArrayList<>();
    private ArrayList<int[]> remove_walls = new ArrayList<>();

    private int wall_w = 50;
    private int wall_h = 100;

    private int wall_step = 30;

    private int move_step = 0;


    public GameView(Context context) {
        super(context);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW = this.getWidth();
        screenH = this.getHeight();
        initGame();
        flag = true;
        th = new Thread(this);
        th.start();
    }

    /**
     * 初始化Game
     */
    private void initGame() {
        if (gameState == GAME_MENU) {
            floor[0] = 0;
            floor[1] = screenH - screenH / 5;
            level[0] = screenW / 2;
            level[1] = screenH / 5;
            level_value = 0;
            bird[0] = screenW / 3;
            bird[1] = screenH / 2;
            walls.clear();
            floor_width = dp2px(15);
            speed = dp2px(3);
            bird_width = dp2px(10);
            bird_a = dp2px(2);
            bird_vUp = -dp2px(16);
            wall_w = dp2px(45);
            wall_h = dp2px(100);
            wall_step = wall_w * 4;
        }
    }

    /**
     * dp转像素
     *
     * @param dp
     * @return
     */
    private int dp2px(float dp) {
        int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
        return px;
    }

    /**
     * 绘制画面
     */
    public void myDraw() {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                //刷屏
                canvas.drawColor(Color.BLACK);
                //背景
                int floor_start = floor[0];
                while (floor_start < screenW) {
                    canvas.drawLine(floor_start, floor[1], floor_start + floor_width, floor[1], paint);
                    floor_start += floor_width * 2;
                }

                //墙
                for (int i = 0; i < walls.size(); i++) {
                    int[] wall = walls.get(i);

                    float[] pts = {
                            wall[0], 0, wall[0], wall[1],
                            wall[0], wall[1] + wall_h, wall[0], floor[1],
                            wall[0] + wall_w, 0, wall[0] + wall_w, wall[1],
                            wall[0] + wall_w, wall[1] + wall_h, wall[0] + wall_w, floor[1],
                            wall[0], wall[1], wall[0] + wall_w, wall[1],
                            wall[0], wall[1] + wall_h, wall[0] + wall_w, wall[1] + wall_h
                            //,wall[0],floor[1], wall[0]+wall_w, floor[1]
                    };
                    canvas.drawLines(pts, paint);
                }

                //球
                canvas.drawCircle(bird[0], bird[1], bird_width, paint);

                //分数
                canvas.drawText(String.valueOf(level_value), level[0], level[1], paint);

            }
        } catch (Exception e) {
        } finally {
            //解锁画布
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    private void logic() {

        switch (gameState) {
            case GAME_MENU:

                break;
            case GAMEING:
                //球
                bird_v += bird_a;
                bird[1] += bird_v;
                if (bird[1] > floor[1] - bird_width) {
                    bird[1] = floor[1] - bird_width;
                    gameState = GAME_OVER;
                }
                //顶部
                /*if (bird[1] <= bird_width) {
                    bird[1] = bird_width;
                }*/

                //地面
                if (floor[0] < -floor_width) {
                    floor[0] += floor_width * 2;
                }
                floor[0] -= speed;

                //墙
                remove_walls.clear();
                for (int i = 0; i < walls.size(); i++) {
                    int[] wall = walls.get(i);
                    wall[0] -= speed;
                    if (wall[0] < -wall_w) {
                        remove_walls.add(wall);
                    } else if (wall[0] - bird_width <= bird[0] && wall[0] + wall_w + bird_width >= bird[0]
                            && (bird[1] <= wall[1] + bird_width || bird[1] >= wall[1] + wall_h - bird_width)) {
                        gameState = GAME_OVER;
                    }

                    int pass = wall[0] + wall_w + bird_width - bird[0];
                    if (pass < 0 && -pass <= speed) {
                        level_value++;
                    }
                }
                //溢出屏幕
                if (remove_walls.size() > 0) {
                    walls.removeAll(remove_walls);
                }

                //新的墙
                move_step += speed;
                if (move_step > wall_step) {
                    int[] wall = new int[]{screenW, (int) (Math.random() * (floor[1] - 2 * wall_h) + 0.5 * wall_h)};
                    walls.add(wall);
                    move_step = 0;
                }
                break;
            case GAME_OVER:
                //球
                if (bird[1] < floor[1] - bird_width) {
                    bird_v += bird_a;
                    bird[1] += bird_v;
                    if (bird[1] >= floor[1] - bird_width) {
                        bird[1] = floor[1] - bird_width;
                    }
                } else {
                    MainActivity.instance.showMessage(level_value);
                    gameState = GAME_MENU;
                    initGame();
                }
                break;
        }
    }

    /**
     * 触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            switch (gameState) {
                case GAME_MENU:
                    gameState = GAMEING;
                    //bird_v = bird_vUp;
                    //break;
                case GAMEING:
                    bird_v = bird_vUp;
                    break;
                case GAME_OVER:
                    //球掉下
                    if (bird[1] >= floor[1] - bird_width) {
                        gameState = GAME_MENU;
                        initGame();
                    }

                    break;
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (flag) {
            long start = System.currentTimeMillis();
            myDraw();
            logic();
            long end = System.currentTimeMillis();
            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

}
