package com.uv.cbg;

import com.alibaba.fastjson.JSONObject;
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
 * search.score.value.totalScore=40000
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
 * <p>
 * https://my.cbg.163.com/cgi/mweb/pl/role?
 * platform_type=1 平台 1：iOS 2：安卓
 * &equip_level_min=70&equip_level_max=89
 * &total_score=40000&role_score=20000 总评分， 角色评分
 * &serverid=170 服务器ID
 * &pass_fair_show= 0 只显示公示期， 1 不现实公示期
 * &school=2,1,8 门派
 * &jm_active_num=12 经脉
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
    private int totalScore;
    private int personScore;

    @Value("#{config['search.school.html.div.idx']}")
    private int schoolHtmlIdx;
    private String schools;
    /**
     * 一个json对象字符串，藏宝阁数据url参数中门派ID 对应 在web界面 门派li在ul中的下标位置idx
     */
    @Value("#{config['search.school.mapper']}")
    private String schoolLiDetailIdx;
    private JSONObject schoolLiDetailJson;

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

    public int getSchoolHtmlIdx() {
        return schoolHtmlIdx;
    }

    public int getSchoolLiDetailIdx(int menpaiID) {
        if (schoolLiDetailJson == null) {
            schoolLiDetailJson = JSONObject.parseObject(this.schoolLiDetailIdx);
        }
        return schoolLiDetailJson.getInteger(String.valueOf(menpaiID));
    }

    public int getSchoolLiDetailIdx(String menpaiID) {
        return this.getSchoolLiDetailIdx(Integer.valueOf(menpaiID));
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

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getPersonScore() {
        return personScore;
    }

    public void setPersonScore(int personScore) {
        this.personScore = personScore;
    }

    public String getSchools() {
        return schools;
    }

    public void setSchools(String schools) {
        this.schools = schools;
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
                ", totalScore=" + totalScore +
                ", personScore=" + personScore +
                ", schools='" + schools + '\'' +
                ", isShowPublish=" + isShowPublish +
                '}';
    }
}
