package com.uv.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author uvsun 2019-08-02 01:09
 * 生成ChromeDriver对象类。
 * 区别于 MyChromeRemoteWebDriver ，但其实ChromeDriver底层也是继承于 RemoteWebDriver 的。
 */
public class MyChromeWebDriver implements MyDriver {

    private WebDriver driver;

    private String driverExecutorPath;

    /**
     * 是否无头模式，true：可以运行在无桌面系统的Linux上。false：会打开一个chrome，运行演示。
     */
    private boolean isHeadless;

    private int port;

    @Override
    public WebDriver getWebDriver() {

        System.setProperty("webdriver.chrome.driver", this.getDriverExecutorPath());
        ChromeOptions chromeOptions = new ChromeOptions();
        //设置 chrome 的无头模式
        chromeOptions.setHeadless(this.isHeadless);
        // disabling extensions
        chromeOptions.addArguments("--disable-extensions");
        // applicable to windows os only
        chromeOptions.addArguments("--disable-gpu");
        // overcome limited resource problems
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--no-sandbox");

//        ChromeDriverService.Builder builder = new ChromeDriverService.Builder()
//                .usingDriverExecutable(new File(this.getDriverExecutorPath()))
//                .withSilent(true);
//        if (port != 0) {
//            builder.usingPort(port);
//        } else {
//            builder.usingAnyFreePort();
//        }
//        ChromeDriverService service = builder.build();

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
