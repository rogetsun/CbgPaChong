package com.uv.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author uvsun 2019-08-02 01:09
 */
public class MyChromeWebDriver implements MyDriver {

    private WebDriver driver;

    private String driverExecutorPath;

    /**
     * 是否无头模式，true：可以运行在无桌面系统的Linux上。false：会打开一个chrome，运行演示。
     */
    private boolean isHeadless;

    @Override
    public WebDriver getWebDriver() {

        System.setProperty("webdriver.chrome.driver", this.getDriverExecutorPath());
        ChromeOptions chromeOptions = new ChromeOptions();
        //设置 chrome 的无头模式
        chromeOptions.setHeadless(this.isHeadless);
        //启动一个 chrome 实例
        this.driver = new ChromeDriver(chromeOptions);
        return driver;


    }

    @Override
    public void stop() {
        this.driver.quit();
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public String getDriverExecutorPath() {
        return driverExecutorPath;
    }

    public void setDriverExecutorPath(String driverExecutorPath) {
        this.driverExecutorPath = driverExecutorPath;
    }

    public boolean isHeadless() {
        return isHeadless;
    }

    public void setHeadless(boolean headless) {
        isHeadless = headless;
    }
}
