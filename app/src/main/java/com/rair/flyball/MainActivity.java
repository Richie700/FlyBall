package com.rair.flyball;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rair.flyball.view.GameView;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    //配置信息
    public static final String GameSettings = "Game_Settings";
    //最后得分
    public static final String Settings_LevelLast = "LevelLast";
    //最高分
    public static final String Settings_LevelTop = "LevelTop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        instance = this;
        setContentView(new GameView(this));
    }

    /**
     * 跳转到LoadingActivity
     * 显示loading的提示
     *
     * @param level
     */
    public void showMessage(int level) {
        saveSettingData(level);
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 保存配置信息
     *
     * @param level
     */
    private void saveSettingData(int level) {
        SharedPreferences settings = getSharedPreferences(GameSettings, 0);
        settings.edit().putInt(Settings_LevelLast, level).apply();
        int top = settings.getInt(Settings_LevelTop, 0);
        if (level > top) {
            settings.edit().putInt(Settings_LevelTop, level).apply();
        }
    }
}
