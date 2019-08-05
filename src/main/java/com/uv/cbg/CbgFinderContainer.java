package com.uv.cbg;

import com.taobao.api.ApiException;
import com.uv.cbg.finder.CbgFinder;
import com.uv.notify.Notify;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Arrays;
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

    private int threadCount;

    private Notify notify;

    private String finderBeanName;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    /**
     * 分区搜索账号，一个区一个线程,如果没有配置哪个区，则起一个不指定区的搜索配置
     *
     * @return 搜索了几个区
     */
    public int startCbgFinderSearch() throws ApiException {

        ExecutorService executorService = Executors.newFixedThreadPool(this.getThreadCount());

        List<Future<CbgFindResult>> futures = new ArrayList<>();

        if (null != serverNames && !"".equals(serverNames)) {
            String[] serverNameArray = serverNames.split(",");
            log.info("搜索[" + serverNameArray.length + "]个区:" + Arrays.toString(serverNameArray));

            for (String sn : serverNameArray) {

                Callable<CbgFindResult> callable = createCallableCbgFinder(sn);
                futures.add(executorService.submit(callable));

            }

        } else {
            log.info("搜索全区！");
            Callable<CbgFindResult> callable = createCallableCbgFinder(null);
            futures.add(executorService.submit(callable));

        }

        List<CbgGamer> cbgGamers = new ArrayList<>();
        List<Integer> errorCodeList = new ArrayList<>();

        for (Future<CbgFindResult> future : futures) {

            try {
                CbgFindResult result = future.get();
                if (result.getCode() != 0) {
                    log.info("[" + (result.getServerName() == null ? "全区" : result.getServerName()) + "]find over;" + result.toString());

                    if (!errorCodeList.contains(result.getCode())) {
                        notify.sendTextMsg(result.toString());
                        errorCodeList.add(result.getCode());
                        continue;
                    }
                }
                log.info("[" + (result.getServerName() == null ? "全区" : result.getServerName()) + "]find over;" + result.toString());
                if (result.getFoundCount() > 0) {
                    cbgGamers.addAll(result.getGamerList());
                    for (CbgGamer gamer : result.getGamerList()) {
                        log.debug((result.getServerName() == null ? "全区" : result.getServerName()) + ":" + gamer);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("获取执行结果失败!", e);
            }
        }
        executorService.shutdown();
        int noticedNum = 0;
        if (cbgGamers.size() > 0) {
            noticedNum = notify.notify(cbgGamers);
        }
        log.info("total: found [" + cbgGamers.size() + "], noticed[" + noticedNum + "]");

        return futures.size();
    }

    /**
     * 创建藏宝阁爬虫任务
     * 除了区配置以外，其他配置由配置文件->spring-config.xml直接注入到FilterBean
     * 区 配置由本代码设置
     *
     * @param sn 区
     * @return Callable<CbgFindResult>
     */
    private Callable<CbgFindResult> createCallableCbgFinder(String sn) {
        CbgFinder finder = ac.getBean(this.getFinderBeanName(), CbgFinder.class);
        finder.getFilterBean().setServerName(sn);
        return () -> {
            CbgFindResult findResult = new CbgFindResult(null, sn, 0);
            try {
                //初步筛选条件下的搜索结果
                CbgFindResult searchResult = finder.searchCbg();
                log.debug("[" + sn + "]初步搜索结果:[code:" + searchResult.getCode() + ",][count:" + searchResult.getFoundCount() + "].msg:" + searchResult.getMsg());
                if (searchResult.getCode() != 0) {
                    return searchResult;
                }
                if (searchResult.getFoundCount() > 0) {
                    //性价比过滤
                    List<CbgGamer> cbgFilterGamers = costFilter.filter(searchResult.getGamerList());

                    //如果过滤完 依然有找到的游戏号，则放入搜索结果
                    if (cbgFilterGamers != null && cbgFilterGamers.size() > 0) {
                        findResult.setGamerList(cbgFilterGamers);
                        findResult.setFoundCount(cbgFilterGamers.size());
                        findResult.setCode(0);
                        findResult.setMsg("success");
                    }
                }

            } catch (Throwable e) {
                findResult.setCode(10001);
                findResult.setMsg(e.getMessage());
                findResult.setFoundCount(0);
                log.error("查找[" + (sn == null ? "全区" : sn) + "]的号失败!", e);
            }
            return findResult;
        };
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

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public Notify getNotify() {
        return notify;
    }

    public void setNotify(Notify notify) {
        this.notify = notify;
    }

    public String getFinderBeanName() {
        return finderBeanName;
    }

    public void setFinderBeanName(String finderBeanName) {
        this.finderBeanName = finderBeanName;
    }
}
