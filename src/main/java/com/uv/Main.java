package com.uv;

import com.taobao.api.ApiException;
import com.uv.cbg.CbgFinderContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author uvsun 2019-08-02 02:01
 */
public class Main {
    private static final Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws ApiException {
        try {
            systemVariables();
            long beginTime = System.currentTimeMillis();
            log.info("begin to search my cbg!!!");
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-config.xml");
            log.debug("applicationContext:" + applicationContext);
            CbgFinderContainer finder = applicationContext.getBean("cbgFinderContainer", CbgFinderContainer.class);
            log.debug("finder:" + finder);
            finder.startCbgFinderSearch();
            log.info("end to search !!! 耗时:[" + ((System.currentTimeMillis() - beginTime) / 1000) + "s]!");
        } catch (BeansException | ApiException e) {
            log.error("运行失败,", e);
        }

    }

    public static void systemVariables() {
        //print available locales
//        Locale[] list = DateFormat.getAvailableLocales();
//        System.out.println("======System available locales:======== ");
//        for (int i = 0; i < list.length; i++) {
//            System.out.println(list[i].toString() + "/t" + list[i].getDisplayName());
//        }

        //print JVM default properties
        System.out.println("======System property======== ");
        System.getProperties().list(System.out);
        System.out.println("log.dir=" + System.getProperties().getProperty("log.dir"));
    }
}
