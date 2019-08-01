package com.uv.study;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * PhantomJs是一个基于webkit内核的无头浏览器，即没有UI界面，即它就是一个浏览器，只是其内的点击、翻页等人为相关操作需要程序设计实现;
 * 因为爬虫如果每次爬取都调用一次谷歌浏览器来实现操作,在性能上会有一定影响,而且连续开启十几个浏览器简直是内存噩梦,
 * 因此选用phantomJs来替换chromeDriver
 * PhantomJs在本地开发时候还好，如果要部署到服务器，就必须下载linux版本的PhantomJs,相比window操作繁琐
 *
 * @author uvsun 2019-08-01 15:18
 */
public class Study2 {

    public static PhantomJSDriver getPhantomJSDriver() {
        //设置必要参数
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
                "/Users/uvsun/Documents/ChromeDriver/phantomjs-2.1.1-macosx/bin/phantomjs");

        return new PhantomJSDriver(dcaps);
    }

    public static void main(String[] args) {
        WebDriver driver = getPhantomJSDriver();
        driver.get("http://www.baidu.com");
        System.out.println(driver.getCurrentUrl());
        System.out.println(driver.getTitle());

    }
}
