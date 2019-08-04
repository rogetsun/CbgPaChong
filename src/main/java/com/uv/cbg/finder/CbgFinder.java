package com.uv.cbg.finder;

import com.uv.cbg.CbgGamer;
import com.uv.cbg.FilterBean;

import java.io.IOException;
import java.util.List;

/**
 * @author uvsun 2019-08-01 16:54
 */
public interface CbgFinder {

    List<CbgGamer> searchCbg() throws IOException;

    FilterBean getFilterBean();

    void setFilterBean(FilterBean filterBean);

    String getCbgURL();

    void setCbgURL(String cbgURL);

}
