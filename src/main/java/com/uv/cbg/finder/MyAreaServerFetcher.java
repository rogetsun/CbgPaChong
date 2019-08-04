package com.uv.cbg.finder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.uv.cbg.CbgFindResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author uvsun 2019-08-02 22:28
 * 本类是一个假的finder，只用于获取梦幻手游 大区 服务器 信息
 */
public class MyAreaServerFetcher extends CbgFinderByWeb {

    private static final Log log = LogFactory.getLog(MyAreaServerFetcher.class);
    private static final String LST_SERVER_KEY = "IS_LAST";
    private int areaStartIdx;
    private int serverStartIdx;
    @Value("#{config['search.server.html.div.idx']}")
    private int serverHtmlIdx;

    @Override
    public CbgFindResult searchCbg() throws IOException {
        CbgFindResult result = new CbgFindResult();
        result.setFoundCount(0);

        WebDriver webDriver = this.getMyDriver().getWebDriver();
        //设置页面隐性等待加载时间10s
        webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        //设置页面隐性等待元素查找时间10s，也是等待页面加载导致
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


        // 让浏览器访问
        webDriver.get(this.getCbgURL());
        // 获取 网页的 title
        log.debug(" Page title is: " + webDriver.getTitle());
        log.debug(webDriver.getCurrentUrl());

        try {
            // 通过 id 找到 input 的 DOM
            WebElement el = webDriver.findElement(By.linkText("iOS角色"));
            el.click();

            JSONArray areaServers = new JSONArray();
            //idx[0] : area大区下标； idx[1] : 服务器下标
            int[] idx = new int[]{this.areaStartIdx, this.serverStartIdx};

            int f = 0;
            JSONObject ret = null;
            for (; f < 2; ) {
                ret = this.selectAreaServer(webDriver, idx);
                log.debug(ret);
                // https://my.cbg.163.com/cgi/mweb/pl/role?platform_type=1&serverid=180
                try {

                    String url = webDriver.getCurrentUrl();
                    ret.put("url", url);
                    areaServers.add(ret);
                    log.debug(url);
                    String serverid = this.getServeridFromCurrentUrl(url);
                    ret.put("serverid", serverid);
                    if (ret.containsKey(LST_SERVER_KEY)) {
                        f = 2;
                    } else {
                        f = 0;
                    }
                } catch (Exception e) {
                    f = 1;
                    e.printStackTrace();
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            log.info(areaServers.toString(SerializerFeature.PrettyFormat));
        } catch (Throwable e) {
            log.error("发生异常", e);
            result.setCode(901);
            result.setMsg(e.getMessage());
        } finally {
            this.getMyDriver().stop();
        }
        return result;

    }

    private String getServeridFromCurrentUrl(String url) {
        url = url.substring(url.indexOf("serverid="));
        int x = url.indexOf("&");
        if (x >= 0) {
            url = url.substring(0, x);
        }
        return url.split("=")[1];
    }

    private JSONObject selectAreaServer(WebDriver driver, int[] idx) throws InterruptedException {
        JSONObject areaServerJson;
        driver.findElement(By.linkText("筛选")).click();

        WebElement filterDiv = driver.findElement(By.className("sf-container"));
        List<WebElement> webFilterOptsEls = filterDiv.findElements(By.className("opts"));

        areaServerJson = this.setFilterServer(webFilterOptsEls.get(serverHtmlIdx), driver, idx);

        TimeUnit.MILLISECONDS.sleep(500);

        for (int i = 0; i < 5; i++) {
            try {
                driver.findElement(By.linkText("完成")).click();
                break;
            } catch (Exception e) {
                log.error("无法点击筛选条件配置完成按钮，等下再试！");
                TimeUnit.SECONDS.sleep(1);
            }
        }

        return areaServerJson;
    }

    private JSONObject setFilterServer(WebElement element, WebDriver driver, int[] idx) throws InterruptedException {

        JSONObject areaServerJson = new JSONObject();

        element.findElement(By.cssSelector(".sf-select.sf-select2")).findElements(By.tagName("li")).get(1).click();

        WebElement areaServerContainerEl = driver.findElement(By.className("rl-dialog-area-select")).findElement(By.className("site-container"));

        List<WebElement> areaEls = areaServerContainerEl.findElement(By.className("area")).findElements(By.tagName("a"));


        WebElement areaEl = areaEls.get(idx[0]);
        areaEl.click();
        TimeUnit.MILLISECONDS.sleep(500);
        List<WebElement> serverEls = areaServerContainerEl.findElement(By.className("server")).findElements(By.tagName("li"));


        WebElement serverEl = serverEls.get(idx[1]);
        if (!serverEl.getAttribute("class").contains("on")) {
            serverEl.findElement(By.tagName("a")).click();
        }
        areaServerJson.put("serverName", areaEl.getText() + "-" + serverEl.getText());
        areaServerJson.put("idx", idx[0] + "-" + idx[1]);
        //设置下一个area server下标
        idx[1] = idx[1] + 1;
        if (idx[1] >= serverEls.size()) {

            idx[1] = 1;
            idx[0] = idx[0] + 1;
            if (idx[0] >= areaEls.size()) {
                areaServerJson.put(LST_SERVER_KEY, "1");
            }

        }

        areaServerContainerEl.findElement(By.linkText("完成")).click();

        return areaServerJson;

    }

    public int getAreaStartIdx() {
        return areaStartIdx;
    }

    public void setAreaStartIdx(int areaStartIdx) {
        this.areaStartIdx = areaStartIdx;
    }

    public int getServerStartIdx() {
        return serverStartIdx;
    }

    public void setServerStartIdx(int serverStartIdx) {
        this.serverStartIdx = serverStartIdx;
    }
}
