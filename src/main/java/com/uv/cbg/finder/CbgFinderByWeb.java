package com.uv.cbg.finder;

import com.uv.cbg.CbgFindResult;
import com.uv.cbg.CbgGamer;
import com.uv.cbg.FilterBean;
import com.uv.driver.MyDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author uvsun 2019-08-02 20:52
 */
public class CbgFinderByWeb implements CbgFinder {

    private static final Log log = LogFactory.getLog(CbgFinder.class);

    @Value("#{config['driver.chrome.headless']}")
    private boolean isChromeHeadless;

    private String cbgURL;

    private MyDriver myDriver;
    private FilterBean filterBean;

    @Override
    public CbgFindResult searchCbg() throws IOException {
        CbgFindResult result = new CbgFindResult();
        result.setServerName(this.getFilterBean().getServerName());

        List<CbgGamer> gamers = new ArrayList<>();
        result.setGamerList(gamers);

        WebDriver webDriver = this.getMyDriver().getWebDriver();
        //设置页面隐性等待加载时间10s
        webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        //设置页面隐性等待元素查找时间10s，也是等待页面加载导致
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        log.debug("size:" + webDriver.manage().window().getSize());

        // 让浏览器访问
        webDriver.get(this.cbgURL);
        // 获取 网页的 title
        log.debug(" Page title is: " + webDriver.getTitle());
        log.debug(webDriver.getCurrentUrl());

        try {
            WebElement el = webDriver.findElement(By.linkText("iOS角色"));
            el.click();

            this.setAllFilterOptions(webDriver);
            gamers.addAll(this.startSearch(webDriver));

        } catch (Throwable e) {
            log.error("发生异常", e);
            result.setCode(201);
            result.setMsg(e.getMessage());
            return result;
        } finally {
            this.getMyDriver().stop();

        }

        result.setCode(0);
        result.setFoundCount(gamers.size());

        return result;

    }

    /**
     * 移动滑块
     *
     * @param webDriver
     */
    private void slide(WebDriver webDriver) {
        WebElement sliderController = webDriver.findElement(By.className("yidun_control"));
        WebElement slider = sliderController.findElement(By.className("yidun_slider"));
        int ctrlWidth = sliderController.getSize().getWidth();
        int sliderWidth = slider.getSize().getWidth();
        Actions action = new Actions(webDriver);
        action.dragAndDropBy(slider, ctrlWidth - sliderWidth, 0).perform();
    }

    private List<CbgGamer> startSearch(WebDriver webDriver) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        List<CbgGamer> gamers = null;
        WebElement productDiv = null;
        Dimension windowDimension = webDriver.manage().window().getSize();
        log.debug("browser size:" + windowDimension);
        for (int i = 0; i < 5; i++) {
            try {
                productDiv = webDriver.findElement(By.cssSelector(".infinite-scroll.list-block.border"));
            } catch (Exception e) {
                log.error("筛选刚配置完，没找到查询游戏号的结果。等下再试");
                TimeUnit.SECONDS.sleep(1);
            }
        }
        if (null == productDiv) {

            log.error("配置完筛选条件，但是无法执行下去，退出本次爬虫！！！");

        } else {

            log.debug(productDiv.getSize());
            gamers = loadItem(webDriver, windowDimension, productDiv);
            log.debug("=======================================================");
            for (CbgGamer gamer : gamers) {
                log.debug(gamer);
            }
            log.debug("[" + (this.filterBean.getServerName() == null ? "全区" : this.filterBean.getServerName()) + "]找到 " + gamers.size() + " 个初步满足要求的游戏号,用时:" + ((System.currentTimeMillis() - startTime) / 1000) + "s");
            if (!isChromeHeadless) {
                TimeUnit.SECONDS.sleep(60);
            }

        }
        return gamers;
    }

    /**
     * 循环加载游戏账号
     * 章鱼 指挥说能打到5 来个辅助
     *
     * @param driver
     * @param windowDimension
     * @param productDiv
     * @throws InterruptedException
     */
    private List<CbgGamer> loadItem(WebDriver driver, Dimension windowDimension, WebElement productDiv) {
        List<CbgGamer> gamers = new ArrayList<>();

        boolean isLoadingNew = true;
        int doneIdx = 0;
        while (isLoadingNew) {

            try {
                waitItemLoading(productDiv);
                log.debug(productDiv.getSize());

                List<WebElement> products = productDiv.findElements(By.cssSelector(".product-item.list-item.list-item-link"));
                for (; doneIdx < products.size(); doneIdx++) {
                    WebElement p = products.get(doneIdx);

                    CbgGamer gamer = new CbgGamer(p);
                    gamer.setUrl(driver.getCurrentUrl());
                    gamers.add(gamer);

                }
                log.debug("found count:" + doneIdx);
                scrollBottom((JavascriptExecutor) driver, windowDimension, productDiv);
                if (doneIdx == 0) {
                    WebElement notFound = productDiv.findElement(By.cssSelector(".empty-text"));
                    isLoadingNew = false;
                } else {
                    WebElement nomore = productDiv.findElement(By.cssSelector(".spinner-text.spinner-bottom"));
                    if ("已加载全部".equals(nomore.getText())) {
                        isLoadingNew = false;
                    }
                }

            } catch (InterruptedException e) {
                log.debug("加载出问题");
            }
        }
        return gamers;
    }

    private void scrollBottom(JavascriptExecutor driver, Dimension windowDimension, WebElement productDiv) {
        if (productDiv.getSize().getHeight() > windowDimension.getHeight()) {
            int scrollLen = productDiv.getSize().getHeight() - windowDimension.height + 50;
            driver.executeScript("window.scrollTo(0," + scrollLen + ")");
        }
    }

    private void waitItemLoading(WebElement productDiv) throws InterruptedException {
        int itemCount = -1;
        while (true) {
            List<WebElement> itemList = productDiv.findElements(By.xpath("div/div/div"));
            if (itemCount == itemList.size()) {
                log.debug("items always " + itemCount + ", quit waitItemLoading");
                break;
            } else {
                itemCount = itemList.size();
                TimeUnit.MILLISECONDS.sleep(500);
            }
            log.debug("items:" + itemCount);

        }
    }


    private void setAllFilterOptions(WebDriver driver) throws InterruptedException {
        log.debug(this.getFilterBean());
        driver.findElement(By.linkText("筛选")).click();

        WebElement filterDiv = driver.findElement(By.className("sf-container"));
        List<WebElement> filters = filterDiv.findElements(By.className("opts"));

        if (filterBean.getLevel() != 0) {
            log.debug("set level");
            this.setFilterLevel(filters.get(filterBean.getLevelHtmlIdx()));
        }
        if (filterBean.getTotalScore() != 0 || filterBean.getPersonScore() != 0) {
            log.debug("set score");
            this.setFilterScore(filters.get(filterBean.getScoreHtmlIdx()));
        }
        if (filterBean.getSchools() != null && !"".equals(filterBean.getSchools())) {
            log.debug("set menpai");
            this.setFilterMenpai(filters.get(filterBean.getSchoolHtmlIdx()));
        }
        if (!filterBean.isShowPublish()) {
            log.debug("set publish");
            filters.get(filterBean.getShowPublishHtmlIdx()).findElements(By.tagName("li")).get(1).click();
        }
        log.debug(filterBean.getServerName());
        if (null != filterBean.getServerName() && !"".equals(filterBean.getServerName())) {
            log.debug("set server");
            this.setFilterServer(filters.get(filterBean.getServerHtmlIdx()), driver);
        }
        if (!isChromeHeadless) {
            TimeUnit.SECONDS.sleep(8);
        } else {
            TimeUnit.SECONDS.sleep(1);
        }
        for (int i = 0; i < 5; i++) {
            try {
                driver.findElement(By.linkText("完成")).click();
                break;
            } catch (Exception e) {
                log.error("无法点击筛选条件配置完成按钮，等下再试！");
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    private void setFilterServer(WebElement element, WebDriver driver) {
        element.findElement(By.cssSelector(".sf-select.sf-select2")).findElements(By.tagName("li")).get(1).click();

        WebElement serverEl = driver.findElement(By.className("rl-dialog-area-select")).findElement(By.className("site-container"));
        String[] server = this.getFilterBean().getServerName().split("-");
        serverEl.findElement(By.className("area")).findElement(By.linkText(server[0])).click();
        serverEl.findElement(By.className("server")).findElement(By.linkText(server[1])).click();

        serverEl.findElement(By.linkText("完成")).click();

    }

    private void setFilterMenpai(WebElement element) {
        String[] menpaiArray = this.getFilterBean().getSchools().split(",");
        List<WebElement> mpList = element.findElement(By.cssSelector(".sf-select.sf-select3")).findElements(By.tagName("li"));
        for (String menpai : menpaiArray) {
            mpList.get(this.filterBean.getSchoolLiDetailIdx(menpai)).click();
        }
    }

    private void setFilterScore(WebElement element) throws InterruptedException {
        element.click();
        if (this.getFilterBean().getPersonScore() != 0) {
            WebElement input = element.findElement(By.name("role_score"));
            input.sendKeys(this.getFilterBean().getPersonScore() + "");
        }
        if (this.getFilterBean().getTotalScore() != 0) {
            element.findElement(By.name("total_score")).sendKeys(this.getFilterBean().getTotalScore() + "");
        }
        element.click();
    }

    private void setFilterLevel(WebElement element) {
        element.findElement(By.cssSelector(".sf-select.sf-select3")).findElements(By.tagName("li")).get(filterBean.getLevel() - 1).click();
    }

    /**
     * 设置搜索条件，如：等级 70~89的，个人评分>=22222的
     *
     * @param driver
     */
    private void setFilterOptions(WebDriver driver) {
        List<WebElement> filters = driver.findElement(By.className("filter-nav")).findElements(By.className("filter-item"));
        for (WebElement filter : filters) {

            log.debug(filter.getText());

            if (this.getFilterBean().getLevel() != 0 && "等级".equals(filter.getText())) {

                filter.click();
                WebElement optionDiv = driver.findElement(By.cssSelector("ul.sf-select.sf-select3"));
                List<WebElement> liList = optionDiv.findElements(By.tagName("li"));
                liList.get(this.getFilterBean().getLevel() - 1).click();
                driver.findElement(By.linkText("确定")).click();
                continue;

            }
            if ("评分".equals(filter.getText())) {
                if (this.getFilterBean().getTotalScore() == 0 && this.getFilterBean().getPersonScore() == 0) {
                    continue;
                }
                filter.click();
                if (this.getFilterBean().getPersonScore() != 0) {
                    driver.findElement(By.name("role_score")).sendKeys(this.getFilterBean().getPersonScore() + "");
                }
                if (this.getFilterBean().getTotalScore() != 0) {
                    driver.findElement(By.name("total_score")).sendKeys(this.getFilterBean().getTotalScore() + "");
                }
                driver.findElement(By.linkText("确定")).click();
                continue;

            }
        }
    }


    @Override
    public FilterBean getFilterBean() {
        return filterBean;
    }

    @Override
    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
    }

    @Override
    public String getCbgURL() {
        return cbgURL;
    }

    @Override
    public void setCbgURL(String cbgURL) {
        this.cbgURL = cbgURL;
    }

    public MyDriver getMyDriver() {
        return myDriver;
    }

    public void setMyDriver(MyDriver myDriver) {
        this.myDriver = myDriver;
    }
}
