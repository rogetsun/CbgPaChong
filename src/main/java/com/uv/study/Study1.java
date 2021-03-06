package com.uv.study;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * * chromeDriver是谷歌的浏览器驱动，用来适配Selenium,有图形页面存在，在调试爬虫下载运行的功能的时候会相对方便
 *
 * @author uvsun 2019-08-01 14:55
 */
public class Study1 {

    private static ChromeDriverService service;

    public static WebDriver getChromeDriver() throws IOException {

        System.setProperty("webdriver.chrome.driver", "/Users/uvsun/Documents/ChromeDriver/chromedriver-78");
        // 创建一个 ChromeDriver 的接口，用于连接 Chrome（chromedriver.exe 的路径可以任意放置，只要在newFile（）的时候写入你放的路径即可）
        service = new ChromeDriverService.Builder().usingDriverExecutable(
                new File("/Users/uvsun/Documents/ChromeDriver/chromedriver-78")
        )
                .usingAnyFreePort().build();
        Study1.service.start();
        // 创建一个 Chrome 的浏览器实例
        return new RemoteWebDriver(Study1.service.getUrl(), DesiredCapabilities.chrome());
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        WebDriver driver = Study1.getChromeDriver();
        // 让浏览器访问 Baidu
        driver.get("https://www.baidu.com/");
        // 用下面代码也可以实现
        //driver.navigate().to("http://www.baidu.com");
        // 获取 网页的 title
        System.out.println(" Page title is: " + driver.getTitle());
        System.out.println(driver.getCurrentUrl());
        // 通过 id 找到 input 的 DOM
        WebElement element = driver.findElement(By.id("lg"));
        String outerHTML = element.getAttribute("outerHTML");
        String innerHTML = element.getAttribute("innerHTML");
        System.out.println(outerHTML);
        System.out.println(innerHTML);
        List<WebElement> list = element.findElements(By.xpath("img"));
        System.out.println(list.size());
        list.forEach(webElement -> {
            System.out.println(webElement.getAttribute("outerHTML"));
        });
        // 输入关键字
        // 提交 input 所在的 form
        element = driver.findElement(By.id("kw"));
        element.sendKeys("藏宝阁");
        element.submit();
        // 通过判断 title 内容等待搜索页面加载完毕，间隔秒
        new WebDriverWait(driver, 10).until(new ExpectedCondition() {
            @Override
            public Object apply(Object input) {
                System.out.println((((WebDriver) input).getTitle().toLowerCase()));
                return true;
            }
        });
        // 显示搜索结果页面的 title
        System.out.println(" Page title is: " + driver.getTitle());
        // 关闭浏览器
        TimeUnit.SECONDS.sleep(10);
        driver.quit();
        // 关闭 ChromeDriver 接口
        service.stop();
    }


}
