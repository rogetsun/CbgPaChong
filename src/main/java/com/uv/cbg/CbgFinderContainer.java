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

    private CostPerformanceFilter costFilter;

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
        List<Future<CbgFindResult>> futures = new ArrayList<>();
        if (null != serverNames && !"".equals(serverNames)) {
            String[] serverNameArray = serverNames.split(",");
            executorService = Executors.newFixedThreadPool(serverNameArray.length);
            for (String sn : serverNameArray) {
                CbgFinder finder = ac.getBean("cbgFinder", CbgFinder.class);
                finder.getFilterBean().setServerName(sn);
                Callable<CbgFindResult> callable = () -> {
                    CbgFindResult findResult = new CbgFindResult(null, finder.getFilterBean().getServerName(), 0);
                    try {
                        List<CbgGamer> gamers = finder.searchCbg();
                        if (null != gamers && gamers.size() > 0) {
                            List<CbgGamer> cbgFilterGamers = costFilter.filter(gamers);
                            if (cbgFilterGamers != null && cbgFilterGamers.size() > 0) {
                                findResult.setGamerList(cbgFilterGamers);
                                findResult.setFoundCount(cbgFilterGamers.size());
                            }
                        }

                    } catch (Throwable e) {
                        log.error("查找[" + sn + "]区的号失败!", e);
                    }
                    return findResult;
                };
                futures.add(executorService.submit(callable));
            }
        }
        for (Future<CbgFindResult> future : futures) {

            try {
                CbgFindResult result = future.get();
                log.debug("find result:[" + result.getServerName() + "] find over！found [" + result.getFoundCount() + "]个游戏号");
            } catch (InterruptedException | ExecutionException e) {
                log.error("获取执行结果失败!", e);
            }
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

    public CostPerformanceFilter getCostFilter() {
        return costFilter;
    }

    public void setCostFilter(CostPerformanceFilter costFilter) {
        this.costFilter = costFilter;
    }
}
