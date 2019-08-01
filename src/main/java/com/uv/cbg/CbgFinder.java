package com.uv.cbg;

import com.uv.driver.MyDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author uvsun 2019-08-01 16:54
 */
public class CbgFinder {

    private static final Log log = LogFactory.getLog(CbgFinder.class);

    @Value("#{config['driver.chrome.headless']}")
    private boolean isChromeHeadless;

    @Value("#{config['cbgURL']}")
    private String cbgURL;

    private MyDriver myDriver;
    private FilterBean filterBean;

    public void searchCbg() throws IOException {
        WebDriver webDriver = this.getMyDriver().getWebDriver();
        long startTime = System.currentTimeMillis();
        //设置页面隐性等待加载时间10s
        webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        //设置页面隐性等待元素查找时间10s，也是等待页面加载导致
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        Dimension windowDimension = webDriver.manage().window().getSize();
        log.debug("browser size:" + windowDimension);
        // 让浏览器访问
        webDriver.get(this.cbgURL);
        // 获取 网页的 title
        log.debug(new Date());
        log.debug(" Page title is: " + webDriver.getTitle());
        log.debug(webDriver.getCurrentUrl());
        log.debug(new Date());
        try {
            //主动等待对象，可以调用until方法主动等待某个东西完成
            WebDriverWait wait = new WebDriverWait(webDriver, 10);

            // 通过 id 找到 input 的 DOM
            WebElement el = webDriver.findElement(By.linkText("iOS角色"));
            log.debug(new Date());
            log.debug(el);
            log.debug(el.getText());
            el.click();

//            this.setFilterOptions(webDriver);
            this.setAllFilterOptions(webDriver);

            WebElement productDiv = null;
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
                List<CbgGamer> gamers = loadItem((JavascriptExecutor) webDriver, windowDimension, productDiv);
                log.debug("=======================================================");
                for (CbgGamer gamer : gamers) {
                    log.debug(gamer);
                }
                log.info("找到 " + gamers.size() + " 个游戏号,用时:" + ((System.currentTimeMillis() - startTime) / 1000) + "s");
                if (!isChromeHeadless) {
                    TimeUnit.SECONDS.sleep(60);
                }

            }


        } catch (Exception e) {
            log.debug("发生异常");
            e.printStackTrace();
        } finally {
            this.getMyDriver().stop();
        }

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
    private List<CbgGamer> loadItem(JavascriptExecutor driver, Dimension windowDimension, WebElement productDiv) throws InterruptedException {
        List<CbgGamer> gamers = new ArrayList<>();

        boolean isLoadingNew = true;
        int doneIdx = 0;
        while (isLoadingNew) {

            try {
                waitItemLoading(productDiv);
                log.info(productDiv.getSize());

                List<WebElement> products = productDiv.findElements(By.cssSelector(".product-item.list-item.list-item-link"));
                for (; doneIdx < products.size(); doneIdx++) {
                    WebElement p = products.get(doneIdx);
                    try {

                        CbgGamer gamer = new CbgGamer(p);
                        gamers.add(gamer);
                    } catch (Exception e) {
                        log.error("解析出问题！", e);
                        String html = p.getAttribute("outerHTML");
                        log.error("解析出问题HTML：" + html);
                    }
                }
                log.info("found count:" + doneIdx);
                scrollBottom(driver, windowDimension, productDiv);
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
        if (filterBean.getAllScore() != 0 || filterBean.getPersonScore() != 0) {
            log.debug("set score");
            this.setFilterScore(filters.get(filterBean.getScoreHtmlIdx()));
        }
        if (filterBean.getMenpais() != null && !"".equals(filterBean.getMenpais())) {
            log.debug("set menpai");
            this.setFilterMenpai(filters.get(filterBean.getMenpaiHtmlIdx()));
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

    private void setFilterServer(WebElement element, WebDriver driver) throws InterruptedException {
        element.findElements(By.cssSelector(".sf-select.sf-select2")).get(0).click();

        WebElement serverEl = driver.findElement(By.className("rl-dialog-area-select")).findElement(By.className("site-container"));
        String[] server = this.getFilterBean().getServerName().split("-");
        serverEl.findElement(By.className("area")).findElement(By.linkText(server[0])).click();
        serverEl.findElement(By.className("server")).findElement(By.linkText(server[1])).click();

        serverEl.findElement(By.linkText("完成")).click();

    }

    private void setFilterMenpai(WebElement element) {
        String[] menpaiArray = this.getFilterBean().getMenpais().split(",");
        List<WebElement> mpList = element.findElement(By.cssSelector(".sf-select.sf-select3")).findElements(By.tagName("li"));
        for (String menpai : menpaiArray) {
            mpList.get(Integer.valueOf(menpai)).click();
        }
    }

    private void setFilterScore(WebElement element) throws InterruptedException {
        element.click();
        if (this.getFilterBean().getPersonScore() != 0) {
            WebElement input = element.findElement(By.name("role_score"));
            input.sendKeys(this.getFilterBean().getPersonScore() + "");
        }
        if (this.getFilterBean().getAllScore() != 0) {
            element.findElement(By.name("total_score")).sendKeys(this.getFilterBean().getAllScore() + "");
        }
        element.click();
    }

    private void setFilterLevel(WebElement element) {
        element.findElement(By.cssSelector(".sf-select.sf-select3")).findElements(By.tagName("li")).get(filterBean.getLevel()).click();
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
                liList.get(this.getFilterBean().getLevel()).click();
                driver.findElement(By.linkText("确定")).click();
                continue;

            }
            if ("评分".equals(filter.getText())) {
                if (this.getFilterBean().getAllScore() == 0 && this.getFilterBean().getPersonScore() == 0) {
                    continue;
                }
                filter.click();
                if (this.getFilterBean().getPersonScore() != 0) {
                    driver.findElement(By.name("role_score")).sendKeys(this.getFilterBean().getPersonScore() + "");
                }
                if (this.getFilterBean().getAllScore() != 0) {
                    driver.findElement(By.name("total_score")).sendKeys(this.getFilterBean().getAllScore() + "");
                }
                driver.findElement(By.linkText("确定")).click();
                continue;

            }
        }
    }


    public FilterBean getFilterBean() {
        return filterBean;
    }

    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
    }

    public String getCbgURL() {
        return cbgURL;
    }

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
