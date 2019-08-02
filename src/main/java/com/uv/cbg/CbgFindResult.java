package com.uv.cbg;

import java.util.List;

/**
 * @author uvsun 2019-08-02 11:57
 */
public class CbgFindResult {
    private List<CbgGamer> gamerList;
    private String serverName;
    private int foundCount;

    public CbgFindResult(List<CbgGamer> gamerList, String serverName, int foundCount) {
        this.gamerList = gamerList;
        this.serverName = serverName;
        this.foundCount = foundCount;
    }

    public CbgFindResult() {
    }

    public List<CbgGamer> getGamerList() {
        return gamerList;
    }

    public void setGamerList(List<CbgGamer> gamerList) {
        this.gamerList = gamerList;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getFoundCount() {
        return foundCount;
    }

    public void setFoundCount(int foundCount) {
        this.foundCount = foundCount;
    }
}
