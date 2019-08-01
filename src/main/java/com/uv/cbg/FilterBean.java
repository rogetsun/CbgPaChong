package com.uv.cbg;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author uvsun 2019-08-02 03:50
 * <p>
 * <p>
 * #搜索条件：服务器，多个英文逗号隔开
 * search.server.html.div.idx=1
 * search.serverNames=双平台-奇经八脉
 * <p>
 * #搜索条件：人物等级，0:69,1:70~89,2:>90
 * search.level.value=1
 * #搜索条件 人物等级 在页面上筛选配置时的大div里的第几个子div，0开始
 * search.level.html.div.idx=4
 * <p>
 * #搜索条件：总评分
 * search.score.value.allScore=40000
 * #搜索条件：人物评分
 * search.score.value.personScore=25555
 * search.score.html.div.idx=7
 * <p>
 * #搜索门派 多个英文逗号隔开
 * search.menpai.values=大唐官府
 * search.menpai.html.div.idx=5
 * <p>
 * search.showPublish=false
 * search.showPublish.html.div.idx=2
 */
public class FilterBean {

    @Value("#{config['search.server.html.div.idx']}")
    private int serverHtmlIdx;
    private String serverName;

    @Value("#{config['search.level.html.div.idx']}")
    private int levelHtmlIdx;
    private int level;

    @Value("#{config['search.score.html.div.idx']}")
    private int scoreHtmlIdx;
    private int allScore;
    private int personScore;

    @Value("#{config['search.menpai.html.div.idx']}")
    private int menpaiHtmlIdx;
    private String menpais;

    @Value("#{config['search.showPublish.html.div.idx']}")
    private int showPublishHtmlIdx;
    private boolean isShowPublish;


    public int getServerHtmlIdx() {
        return serverHtmlIdx;
    }

    public int getLevelHtmlIdx() {
        return levelHtmlIdx;
    }

    public int getScoreHtmlIdx() {
        return scoreHtmlIdx;
    }

    public int getMenpaiHtmlIdx() {
        return menpaiHtmlIdx;
    }

    public int getShowPublishHtmlIdx() {
        return showPublishHtmlIdx;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAllScore() {
        return allScore;
    }

    public void setAllScore(int allScore) {
        this.allScore = allScore;
    }

    public int getPersonScore() {
        return personScore;
    }

    public void setPersonScore(int personScore) {
        this.personScore = personScore;
    }

    public String getMenpais() {
        return menpais;
    }

    public void setMenpais(String menpais) {
        this.menpais = menpais;
    }

    public boolean isShowPublish() {
        return isShowPublish;
    }

    public void setShowPublish(boolean showPublish) {
        isShowPublish = showPublish;
    }

    @Override
    public String toString() {
        return "FilterBean{" +
                "serverName='" + serverName + '\'' +
                ", level=" + level +
                ", allScore=" + allScore +
                ", personScore=" + personScore +
                ", menpais='" + menpais + '\'' +
                ", isShowPublish=" + isShowPublish +
                '}';
    }
}
