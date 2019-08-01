package com.uv.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author uvsun 2019-08-02 01:09
 * 本类为远程测试方式，此代码部署机器为客户机，还需要单独安装selenium-server到一台服务器上。
 * 客户机发送web测试命令给服务器，服务器测试完成奖结果返回
 */
public class MyChromeRemoteWebDriver implements MyDriver {
    private ChromeDriverService service;

    private WebDriver driver;

    private String driverExecutorPath;

    @Override
    public WebDriver getWebDriver() throws IOException {

        System.setProperty("webdriver.chrome.driver", this.getDriverExecutorPath());
        // 创建一个 ChromeDriver 的接口，用于连接 Chrome（chromedriver.exe 的路径可以任意放置，只要在newFile（）的时候写入你放的路径即可）
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(this.getDriverExecutorPath()))
                .withSilent(true)
                .usingAnyFreePort().build();
        service.start();
        //创建一个 Chrome 的浏览器实例.第一个参数：表示服务器的地址。第二个参数：表示预期的执行对象，其他的浏览器都可以以此类推
        this.driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub/"), DesiredCapabilities.chrome());
        return this.driver;

    }

    @Override
    public void stop() {
        this.driver.quit();
        this.service.stop();
    }

    public ChromeDriverService getService() {
        return service;
    }

    public void setService(ChromeDriverService service) {
        this.service = service;
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
}
