package com.uv.cbg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author uvsun 2019-08-02 06:27
 */
public class CbgFinderContainer implements ApplicationContextAware {
    private static final Log log = LogFactory.getLog(CbgFinderContainer.class);

    private ApplicationContext ac;

    private String serverNames;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    /**
     * 分区搜索账号，一个区一个线程
     *
     * @return 搜索了几个区
     */
    public int startCbgFinderSearch() {
        ExecutorService executorService = null;
        List<Future<String>> futures = new ArrayList<>();
        if (null != serverNames && !"".equals(serverNames)) {
            String[] serverNameArray = serverNames.split(",");
            executorService = Executors.newFixedThreadPool(serverNameArray.length);
            for (String sn : serverNameArray) {
                CbgFinder finder = ac.getBean("cbgFinder", CbgFinder.class);
                finder.getFilterBean().setServerName(sn);
                Callable<String> callable = () -> {
                    String tmp = finder.getFilterBean().getServerName();
                    try {
                        finder.searchCbg();
                    } catch (Throwable e) {
                        log.error("查找[" + sn + "]区的号失败!", e);
                    }
                    return tmp;
                };
                futures.add(executorService.submit(callable));
            }
        }
        for (Future<String> future : futures) {
            String sn = null;
            try {
                sn = future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("获取执行结果失败!", e);
            }
            log.debug("[" + sn + "] find over！");
        }
        if (executorService != null) {
            executorService.shutdown();
        }
        return futures.size();
    }

    public String getServerNames() {
        return serverNames;
    }

    public void setServerNames(String serverNames) {
        this.serverNames = serverNames;
    }
}
