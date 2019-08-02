package com.uv.notify;

import com.taobao.api.ApiException;
import com.uv.cbg.CbgGamer;

import java.util.List;

/**
 * @author uvsun 2019-08-02 14:32
 */
public interface Notify {
    void notify(List<CbgGamer> gamers) throws ApiException;
}
