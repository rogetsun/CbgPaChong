package com.uv.cbg.finder;

import com.uv.cbg.CbgGamer;
import com.uv.cbg.FilterBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author uvsun 2019-08-02 20:48
 */
public class CbgFinderByRequest implements CbgFinder {
    private static final Log log = LogFactory.getLog(CbgFinderByRequest.class);

    private String cbgURL;

    private FilterBean filterBean;

    @Override
    public List<CbgGamer> searchCbg() {
        List<CbgGamer> gamers = null;

        try {



        } catch (Throwable e) {
            log.error("发生异常", e);
        }
        return gamers;

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
        return this.cbgURL;
    }

    @Override
    public void setCbgURL(String cbgURL) {
        this.cbgURL = cbgURL;
    }

}
