package com.uv.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;

/**
 * @author uvsun 2019-08-02 01:10
 */
public class MyPhantomJSWebDriver implements MyDriver {

    private String driverExecutorPath;
    private WebDriver webDriver;

    @Override
    public WebDriver getWebDriver() throws IOException {
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", false);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持
        dcaps.setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                this.getDriverExecutorPath()
        );
        this.webDriver = new PhantomJSDriver(dcaps);
        return webDriver;
    }

    public String getDriverExecutorPath() {
        return driverExecutorPath;
    }

    public void setDriverExecutorPath(String driverExecutorPath) {
        this.driverExecutorPath = driverExecutorPath;
    }

    @Override
    public void stop() {
        this.webDriver.quit();
    }
}
