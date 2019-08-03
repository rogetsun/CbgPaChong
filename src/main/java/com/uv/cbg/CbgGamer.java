package com.uv.cbg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author uvsun 2019-08-02 00:08
 */
public class CbgGamer {
    private static final Log log = LogFactory.getLog(CbgGamer.class);

    private int serverId;
    private String name;
    /**
     * 网页访问的URL
     */
    private String url;
    /**
     * js ajax请求获取详细数据的URL
     */
    private String detailDataUrl;
    /**
     * 门派
     */
    private String schoolName;
    /**
     * 是否公示，true：公示期。
     */
    private boolean isPublished;

    /**
     * 区
     */
    private String serverName;
    /**
     * 等级
     */
    private int level;
    /**
     * 平台
     */
    private boolean isIOS;

    /**
     * 总评分
     */
    private int totalScore;
    /**
     * 人物评分
     */
    private int personScore;

    private BigDecimal price;

    /**
     * 亮点
     */
    private String highLights;

    /**
     * 收藏人数
     */
    private int collectCount;

    /**
     * 能否搞价
     */
    private boolean allowBargain;

    /**
     * 游戏账号商品编号
     */
    private String gameOrderSn;

    /**
     * <div class="info">
     * <div class="row">
     * <span class="product-item-tag">
     * <i class="icon icon-bargin"></i>
     * </span>
     * <span class="name">阴曹地府</span>
     * <span class="level">89级</span>
     * <i class="flex1"></i>
     * <span class="server-name">二区-梦回奔日</span>
     * <span class="platform"><i class="icon s-l icon-ios"></i></span>
     * </div>
     * <div class="row">
     * <div class="attrs">
     * <span class="basic_attrs_item">总评分:44559</span>
     * <span class="basic_attrs_item">人物评分:25253</span>
     * </div>
     * <div class="price">¥40,000.00</div>
     * </div>
     * <div class="row">
     * <ul class="highlights">
     * <li>双蓝装备x1</li>
     * <li>满11段</li>
     * <li>11红神宠</li>
     * </ul>
     * <div class="collect">3人收藏</div>
     * </div>
     * </div>
     *
     * @param gameDiv
     */
    public CbgGamer(WebElement gameDiv) {

        try {
            List<WebElement> icons = gameDiv.findElements(By.className("icon"));
            for (WebElement icon : icons) {
                if (icon.getAttribute("class").contains("icon-bargin")) {
                    this.setAllowBargain(true);
                }
                if (icon.getAttribute("class").contains("icon-publicity")) {
                    this.setPublished(true);
                }
                if (icon.getAttribute("class").contains("icon-ios")) {
                    this.setIOS(true);
                }
            }

            this.setSchoolName(gameDiv.findElement(By.className("name")).getText());

            String levelStr = gameDiv.findElement(By.className("level")).getText();
            this.setLevel(Integer.valueOf(levelStr.substring(0, levelStr.length() - 1)));
            this.setServerName(gameDiv.findElement(By.className("server-name")).getText());
            List<WebElement> scores = gameDiv.findElements(By.className("basic_attrs_item"));
            this.setTotalScore(Integer.valueOf(scores.get(0).getText().split(":")[1]));
            this.setPersonScore(Integer.valueOf(scores.get(1).getText().split(":")[1]));
            this.setPrice(new BigDecimal(gameDiv.findElement(By.className("price")).getText().substring(1).replaceAll(",", "")));
            this.setHighLights(gameDiv.findElement(By.className("highlights")).getText().replaceAll("\n", ","));
            WebElement tmpEl = gameDiv.findElement(By.className("info")).findElements(By.className("row")).get(2);

            if (tmpEl.getAttribute("innerHTML").contains("collect")) {
                this.setCollectCount(Integer.valueOf(tmpEl.findElement(By.className("collect")).getText().split("人")[0]));
            }

        } catch (Throwable e) {
            log.error("解析帐号信息失败,", e);
            log.error("帐号HTML:" + gameDiv.getAttribute("outerHTML"));
        }

    }

    public CbgGamer() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
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

    public boolean isIOS() {
        return isIOS;
    }

    public void setIOS(boolean IOS) {
        isIOS = IOS;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getHighLights() {
        return highLights;
    }

    public void setHighLights(String highLights) {
        this.highLights = highLights;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public boolean isAllowBargain() {
        return allowBargain;
    }

    public void setAllowBargain(boolean allowBargain) {
        this.allowBargain = allowBargain;
    }

    public String getGameOrderSn() {
        return gameOrderSn;
    }

    public void setGameOrderSn(String gameOrderSn) {
        this.gameOrderSn = gameOrderSn;
    }

    public String getDetailDataUrl() {
        return detailDataUrl;
    }

    public void setDetailDataUrl(String detailDataUrl) {
        this.detailDataUrl = detailDataUrl;
    }

    @Override
    public String toString() {
        return "CbgGamer{" +
                "serverId=" + serverId +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", isPublished=" + isPublished +
                ", serverName='" + serverName + '\'' +
                ", level=" + level +
                ", isIOS=" + isIOS +
                ", totalScore=" + totalScore +
                ", personScore=" + personScore +
                ", price=" + price +
                ", highLights='" + highLights + '\'' +
                ", collectCount=" + collectCount +
                ", allowBargain=" + allowBargain +
                ", gameOrderSn='" + gameOrderSn + '\'' +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("4000.1"));
    }
}
