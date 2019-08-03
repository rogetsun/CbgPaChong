package com.uv.cbg.finder;

import com.alibaba.fastjson.JSONObject;
import com.uv.cbg.CbgGamer;
import com.uv.cbg.FilterBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author uvsun 2019-08-02 20:48
 */
public class CbgFinderByRequest implements CbgFinder {
    private static final Log log = LogFactory.getLog(CbgFinderByRequest.class);

    private String cbgDataURL;

    private FilterBean filterBean;

    @Override
    public List<CbgGamer> searchCbg() {
        List<CbgGamer> gamers = null;

        try {
            if (cbgDataURL != null && !"".equals(cbgDataURL)) {
                if (!cbgDataURL.contains("?")) {

                }
                JSONObject params = getUrlParams(this.filterBean);

            }

        } catch (Throwable e) {
            log.error("发生异常", e);
        }
        return gamers;

    }

    private JSONObject getUrlParams(FilterBean filterBean) {
        JSONObject params = new JSONObject();
        if (filterBean.getPersonScore() != 0) {
            params.put("role_score", filterBean.getPersonScore());
        }
        if (filterBean.getTotalScore() != 0) {
            params.put("total_score", filterBean.getTotalScore());
        }
        if (filterBean.getSchools() != null && "".equals(filterBean.getSchools())) {
            params.put("school", filterBean.getSchools());
        }
        if (filterBean.getLevel() != 0) {
            switch (filterBean.getLevel()) {
                case 1:
                    params.put("equip_level_min", 69);
                    params.put("equip_level_max", 69);
                    break;
                case 2:
                    params.put("equip_level_min", 70);
                    params.put("equip_level_max", 89);
                    break;
                case 3:
                    params.put("equip_level_min", 90);
                    params.put("equip_level_max", 119);
                    break;
                default:
            }
        }

        return params;
    }

    @Override
    public FilterBean getFilterBean() {
        return this.filterBean;
    }

    @Override
    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
    }

    @Override
    public String getCbgURL() {
        return this.cbgDataURL;
    }

    @Override
    public void setCbgURL(String cbgDataURL) {
        this.cbgDataURL = cbgDataURL;
    }

    public static void main(String[] args) throws FileNotFoundException {
        JSONObject a = new JSONObject();
        a.put("双平台-星河千帆", 34);
        System.out.println(a);
        System.out.println(a.getInteger(new String("双平台-星河千帆")));
        File asFile = new File("as.json");
        System.out.println(asFile.exists());
//        FileInputStream fis = new FileInputStream();


    }




}
