package com.uv.cbg.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.uv.cbg.filter.costperformance.CostPerformance;
import com.uv.cbg.filter.search.FilterBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uvsun 2019-08-11 11:48
 */
public class ConfigJsonFactory {
    private static final Log log = LogFactory.getLog(ConfigJsonFactory.class);

    private static final String JSON_KEY_FOR_FILTER = "filters";
    private static final String JSON_KEY_FOR_COST_PERFORMANCE = "cost_performance_info";

    private static final String JSON_KEY_FOR_FILTER_LEVEL = "level";
    private static final String JSON_KEY_FOR_FILTER_PERSON_SCORE = "person_score";
    private static final String JSON_KEY_FOR_FILTER_TOTAL_SCORE = "total_score";
    private static final String JSON_KEY_FOR_FILTER_SCHOOLS = "schools";
    private static final String JSON_KEY_FOR_FILTER_SHOW_PUBLISH = "show_publish";
    private static final String JSON_KEY_FOR_FILTER_PLATFORM = "platform";

    @Value("#{config['search.serverNames']}")
    private String serverNames;

    private String configJsonFile;
    private JSONObject configJson;

    public List<FilterBean> getSearchFilters() {
        List<FilterBean> filterBeans = new ArrayList<>();
        if (null != serverNames && !"".equals(serverNames)
                && null != configJson && !configJson.isEmpty()
                && configJson.containsKey(JSON_KEY_FOR_FILTER)
        ) {
            String[] serverNameArr = serverNames.split(",");
            JSONArray filterJSONArray = configJson.getJSONArray(JSON_KEY_FOR_FILTER);
            for (String sn : serverNameArr) {
                filterJSONArray.forEach(filter -> {
                    JSONObject filterJson = JSONObject.parseObject(filter.toString());
                    log.debug(filterJson.toString(SerializerFeature.PrettyFormat));
                    FilterBean filterBean = new FilterBean();
                    filterBean.setLevel(filterJson.getIntValue(JSON_KEY_FOR_FILTER_LEVEL));
                    if (filterJson.containsKey(JSON_KEY_FOR_FILTER_PERSON_SCORE)) {
                        filterBean.setPersonScore(filterJson.getInteger(JSON_KEY_FOR_FILTER_PERSON_SCORE));
                    }
                    // todo
                });

            }
        }

        return filterBeans;
    }

    public Map<String, List<CostPerformance>> getCostPerformanceFilters() {
        Map<String, List<CostPerformance>> map = new HashMap<>();


        return map;
    }

    public void init() {
        File f = new File(configJsonFile);
        log.debug("configJsonFile:" + configJsonFile + " exists? " + f.exists());
        if (f.exists()) {
            try {
                JSONObject filterJson = JSON.parseObject(new FileInputStream(f), JSONObject.class);
                log.debug("filterJson:" + filterJson.toString(SerializerFeature.PrettyFormat));

            } catch (IOException e) {
                log.error("解析搜索配置json文件失败,", e);
            }
        }
    }


    public String getConfigJsonFile() {
        return configJsonFile;
    }

    public void setConfigJsonFile(String configJsonFile) {
        this.configJsonFile = configJsonFile;
    }
}
