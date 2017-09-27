package com.rair.flyball;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rair.flyball.model.GameScore;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.update.BmobUpdateAgent;


public class LoadingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Rair";
    //显示分数和最高分
    private TextView tvLevel;
    //提示
    private TextView tvTap;
    //提交按钮
    private Button btnCommit;
    //榜单按钮
    private Button btnList;
    //当前分数
    private int currentScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //初始化控件
        tvTap = (TextView) findViewById(R.id.tv_tap);
        tvLevel = (TextView) findViewById(R.id.tv_level);
        btnCommit = (Button) findViewById(R.id.btn_commit);
        btnList = (Button) findViewById(R.id.btn_list);
        btnCommit.setOnClickListener(this);
        btnList.setOnClickListener(this);
        View gameStart = findViewById(R.id.ll_start);
        gameStart.setOnClickListener(this);

        //获取分数和最高分
        int[] levels = getSettingData();
        tvLevel.setText("最高分：" + levels[1] + '\n' + "分数：" + levels[0]);
        //要提交的分数
        currentScore = levels[0];

        //检查更新
        checkUpdata();
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_start:
                startGame();
                break;
            case R.id.btn_commit:
                commitScore();
                break;
            case R.id.btn_list:
                toList();
                break;
        }
    }

    /**
     * 提交分数
     */
    private void commitScore() {
        GameScore score = new GameScore();
        score.setScore(currentScore);
        score.setPlayerName(getInfo());
        score.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null)
                    Toast.makeText(LoadingActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 跳转到榜单
     */
    private void toList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    /**
     * 开始游戏
     */
    private void startGame() {
        tvTap.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * 获取sp,获取最高分和分数
     *
     * @return
     */
    private int[] getSettingData() {
        SharedPreferences settings = getSharedPreferences(MainActivity.GameSettings, 0);
        int last = settings.getInt(MainActivity.Settings_LevelLast, 0);
        int top = settings.getInt(MainActivity.Settings_LevelTop, 0);
        return new int[]{last, top};
    }

    @Override
    public void onBackPressed() {
        MainActivity.instance.finish();
        finish();
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    private String getInfo() {
        return Build.MODEL;
    }

    /**
     * 检查更新
     */
    private void checkUpdata() {
        BmobUpdateAgent.update(this);
        //仅在wifi状态下提示
        BmobUpdateAgent.setUpdateOnlyWifi(false);
    }
}
