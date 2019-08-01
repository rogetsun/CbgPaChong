package com.uv;

import com.uv.cbg.CbgFinderContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author uvsun 2019-08-02 02:01
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-config.xml");
        CbgFinderContainer finder = applicationContext.getBean("cbgFinderContainer", CbgFinderContainer.class);
        finder.startCbgFinderSearch();
    }
}
