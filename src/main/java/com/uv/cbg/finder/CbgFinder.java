package com.uv.cbg.finder;

import com.uv.cbg.CbgFindResult;
import com.uv.cbg.FilterBean;

import java.io.IOException;

/**
 * @author uvsun 2019-08-01 16:54
 */
public interface CbgFinder {

    CbgFindResult searchCbg() throws IOException;

    FilterBean getFilterBean();

    void setFilterBean(FilterBean filterBean);

    String getCbgURL();

    void setCbgURL(String cbgURL);

}
