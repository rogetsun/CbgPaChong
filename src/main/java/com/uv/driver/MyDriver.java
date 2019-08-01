package com.uv.driver;

import org.openqa.selenium.WebDriver;

import java.io.IOException;

/**
 * @author uvsun 2019-08-02 01:10
 */
public interface MyDriver {
    WebDriver getWebDriver() throws IOException;

    void stop();

}
