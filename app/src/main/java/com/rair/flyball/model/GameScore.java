package com.rair.flyball.model;

import cn.bmob.v3.BmobObject;

/**
 * 完玩家分数
 * Created by Administrator on 2016/12/3.
 */

public class GameScore extends BmobObject {

    //玩家昵称
    private String playerName;
    //分数
    private Integer score;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
