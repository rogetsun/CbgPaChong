package com.uv.cbg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author uvsun 2019-08-02 00:08
 */
public class CbgGamer {
    private int id;
    private String name;
    /**
     * 门派
     */
    private String menPai;
    /**
     * 是否公示，true：公示期。
     */
    private boolean isPublished;
    /**
     * 是否可以还价
     */
    private boolean isBargin;
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
    private int allScore;
    /**
     * 人物评分
     */
    private int personScore;

    private BigDecimal price;

    /**
     * 亮点
     */
    private String hightLights;

    /**
     * 收藏人数
     */
    private int collectCount;


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

        List<WebElement> icons = gameDiv.findElements(By.className("icon"));
        for (WebElement icon : icons) {
            if (icon.getAttribute("class").contains("icon-bargin")) {
                this.setBargin(true);
            }
            if (icon.getAttribute("class").contains("icon-publicity")) {
                this.setPublished(true);
            }
            if (icon.getAttribute("class").contains("icon-ios")) {
                this.setIOS(true);
            }
        }

        this.setMenPai(gameDiv.findElement(By.className("name")).getText());

        String levelStr = gameDiv.findElement(By.className("level")).getText();
        this.setLevel(Integer.valueOf(levelStr.substring(0, levelStr.length() - 1)));
        this.setServerName(gameDiv.findElement(By.className("server-name")).getText());
        List<WebElement> scores = gameDiv.findElements(By.className("basic_attrs_item"));
        this.setAllScore(Integer.valueOf(scores.get(0).getText().split(":")[1]));
        this.setPersonScore(Integer.valueOf(scores.get(1).getText().split(":")[1]));
        this.setPrice(new BigDecimal(gameDiv.findElement(By.className("price")).getText().substring(1).replaceAll(",", "")));
        this.setHightLights(gameDiv.findElement(By.className("highlights")).getText().replaceAll("\n", ","));
        this.setCollectCount(Integer.valueOf(gameDiv.findElement(By.className("collect")).getText().split("人")[0]));

    }

    public CbgGamer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenPai() {
        return menPai;
    }

    public void setMenPai(String menPai) {
        this.menPai = menPai;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public boolean isBargin() {
        return isBargin;
    }

    public void setBargin(boolean bargin) {
        isBargin = bargin;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getHightLights() {
        return hightLights;
    }

    public void setHightLights(String hightLights) {
        this.hightLights = hightLights;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }


    @Override
    public String toString() {
        return "CbgGamer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", menPai='" + menPai + '\'' +
                ", isPublished=" + isPublished +
                ", isBargin=" + isBargin +
                ", serverName='" + serverName + '\'' +
                ", level=" + level +
                ", isIOS=" + isIOS +
                ", allScore=" + allScore +
                ", personScore=" + personScore +
                ", price=" + price +
                ", hightLights='" + hightLights + '\'' +
                ", collectCount=" + collectCount +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("4000.1"));
    }
}
