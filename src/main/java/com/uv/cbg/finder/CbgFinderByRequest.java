package com.uv.cbg.finder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.uv.cbg.CbgFindResult;
import com.uv.cbg.CbgGamer;
import com.uv.cbg.FilterBean;
import com.uv.notify.DingNotify;
import com.uv.notify.HttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author uvsun 2019-08-02 20:48
 * <p>
 * {
 * "status": 1,
 * "paging": {
 * "is_last_page": true
 * },
 * "result": [
 * {
 * "equip_type": "1",
 * "price": 800000,
 * "other_info": {
 * "school": 5,
 * "icon": "/game_res/res/photo/0005.png",
 * "level_desc": "89级",
 * "highlights": [
 * "神兽x3",
 * "9红神宠",
 * "满级阵法x1"
 * ],
 * "desc_sumup": "总评分:40808   人物评分:22423",
 * "basic_attrs": [
 * "总评分:40808",
 * "人物评分:22423"
 * ],
 * "format_equip_name": "阴曹地府",
 * "desc_sumup_short": "总评分:40808   人物评分:22423"
 * },
 * "allow_urs_bargain": true,
 * "storage_type": 4,
 * "expire_remain_seconds": 630599,
 * "serverid": 180,
 * "collect_num": 9,
 * "pass_fair_show": 1,
 * "icon": "/game_res/res/photo/0005.png",
 * "server_name": "星河千帆",
 * "level_desc": "89级",
 * "highlights": [
 * "神兽x3",
 * "9红神宠",
 * "满级阵法x1"
 * ],
 * "game_ordersn": "201907051001716-180-9AJMJI26JAZS",
 * "platform_type": 1,
 * "allow_bargain": true,
 * "equip_level": 89,
 * "format_equip_name": "阴曹地府",
 * "area_name": "双平台",
 * "game_channel": "netease",
 * "desc_sumup_short": "总评分:40808   人物评分:22423"
 * },
 * {
 * "equip_type": "1",
 * "price": 1350000,
 * "other_info": {
 * "school": 1,
 * "icon": "/game_res/res/photo/0001.png",
 * "level_desc": "89级",
 * "highlights": [
 * "满11段",
 * "神兽x1",
 * "10红神宠"
 * ],
 * "desc_sumup": "总评分:42951   人物评分:23992",
 * "basic_attrs": [
 * "总评分:42951",
 * "人物评分:23992"
 * ],
 * "format_equip_name": "大唐官府",
 * "desc_sumup_short": "总评分:42951   人物评分:23992"
 * },
 * "allow_urs_bargain": false,
 * "storage_type": 4,
 * "expire_remain_seconds": 648625,
 * "serverid": 180,
 * "collect_num": 20,
 * "pass_fair_show": 1,
 * "icon": "/game_res/res/photo/0001.png",
 * "server_name": "星河千帆",
 * "level_desc": "89级",
 * "highlights": [
 * "满11段",
 * "神兽x1",
 * "10红神宠"
 * ],
 * "game_ordersn": "201907200201716-180-G7WO572V96KI",
 * "platform_type": 1,
 * "allow_bargain": false,
 * "equip_level": 89,
 * "format_equip_name": "大唐官府",
 * "area_name": "双平台",
 * "game_channel": "netease",
 * "desc_sumup_short": "总评分:42951   人物评分:23992"
 * }
 * ],
 * "total_num": 2
 * }
 */
public class CbgFinderByRequest implements CbgFinder {
    private static final Log log = LogFactory.getLog(CbgFinderByRequest.class);

    private String cbgDataUrl;

    private FilterBean filterBean;

    private JSONObject asInfo;

    @Value("#{config['my.config.area_server.info.json.file']}")
    private String asInfoFilePath;

    @Value("#{config['my.cbg.detail.web.URL']}")
    private String gamerDetailWebUrl;

    @Value("#{config['my.cbg.detail.data.URL']}")
    private String gamerDetailDataUrl;

    @Value("#{config['my.cbg.res.web.URL']}")
    private String myResUrl;

    private DingNotify dingNotify;

    @Override
    public CbgFindResult searchCbg() {

        CbgFindResult result = new CbgFindResult();
        result.setServerName(this.getFilterBean().getServerName() == null ? "全区" : this.getFilterBean().getServerName());

        try {

            log.debug("[" + this.getFilterBean().getServerName() + "]begin to search ,filter:" + this.getFilterBean());

            if (cbgDataUrl == null || "".equals(cbgDataUrl)) {
                result.setMsg("无请求地址");
                result.setCode(101);
                log.info("[" + this.getFilterBean().getServerName() + "]搜索结束,未配置cbgDataUrl,无法搜索帐号.请查看config.properties");
                return result;
            }

            /**
             * 拼接请求URL
             */
            JSONObject params = getUrlParams(this.filterBean);
            log.debug("Search Param:" + params);
            StringBuilder requestUrl = new StringBuilder();
            requestUrl.append(cbgDataUrl);
            for (String key : params.keySet()) {
                requestUrl.append("&").append(key).append("=").append(params.getString(key));
            }
            log.debug("Search URL:" + requestUrl.toString());
            /**
             * 开始根据筛选条件搜索藏宝阁
             */
            List<CbgGamer> gamers = new ArrayList<>();
            for (int page = 1; ; page++) {

                JSONObject retMsg = this.fetchGameAccount(requestUrl.toString() + "&page=" + page);
                log.debug("request ret:" + retMsg);

                /**
                 * 非正常情况判断
                 */
                {
                    if (retMsg == null) {

                        result.setCode(102);
                        result.setMsg("无法请求藏宝阁!url=" + requestUrl.toString());
                        result.setGamerList(gamers);
                        result.setFoundCount(gamers.size());
                        return result;

                    }

                    if (retMsg.getInteger("status") != 1) {

                        result.setCode(103);
                        result.setMsg(retMsg.toString());
                        result.setGamerList(gamers);
                        result.setFoundCount(gamers.size());
                        return result;

                    }
                }
                //搜索到的符合筛选条件的 游戏号 总数
                int totalNum = retMsg.getInteger("total_num");
                boolean isLastPage = retMsg.containsKey("paging") && retMsg.getJSONObject("paging").getBoolean("is_last_page");
                log.debug("第" + page + "次搜索到" + totalNum + "个号。最后一页：" + isLastPage);

                if (totalNum > 0) {

                    //本次返回的游戏账号信息
                    JSONArray gameAccounts = retMsg.getJSONArray("result");
                    /**
                     * 解析每一个游戏账号信息，生成CbgGamer,并放进返回List<CbgGamer>
                     */
                    for (int i = 0; i < gameAccounts.size(); i++) {
                        JSONObject acc = gameAccounts.getJSONObject(i);
                        CbgGamer gamer = new CbgGamer();
                        gamer.setCollectCount(acc.getInteger("collect_num"));
                        String price = acc.getString("price");
                        price = price.substring(0, price.length() - 2) + "." + price.substring(price.length() - 2);
                        gamer.setPrice(new BigDecimal(price));
                        gamer.setLevel(acc.getInteger("equip_level"));
                        gamer.setSchoolName(acc.getString("format_equip_name"));
                        gamer.setServerName(acc.getString("area_name") + "-" + acc.getString("server_name"));
                        gamer.setServerId(acc.getInteger("serverid"));
                        JSONObject otherInfo = acc.getJSONObject("other_info");
                        gamer.setHighLights(otherInfo.getJSONArray("highlights").toString());
                        JSONArray scores = otherInfo.getJSONArray("basic_attrs");
                        gamer.setTotalScore(Integer.valueOf(scores.get(0).toString().split(":")[1]));
                        gamer.setPersonScore(Integer.valueOf(scores.get(1).toString().split(":")[1]));
                        gamer.setIOS(acc.getInteger("platform_type") == 1);
                        gamer.setAllowBargain(acc.getBoolean("allow_bargain"));
                        gamer.setGameOrderSn(acc.getString("game_ordersn"));
                        gamer.setPublished(acc.getInteger("pass_fair_show") == 0);
                        gamer.setUrl(this.gamerDetailWebUrl + gamer.getServerId() + "/" + gamer.getGameOrderSn());
                        //https://cbg-my.res.netease.com/game_res/res/photo/0007.png
                        gamer.setHeadIconLink(this.myResUrl + acc.getString("icon"));
                        gamers.add(gamer);
                        log.debug("gamer:" + gamer);
                    }

                }
                //判断是否最后一页
                if (isLastPage) {
                    break;
                }

            }

            result.setCode(0);
            result.setMsg("success");
            result.setGamerList(gamers);
            result.setFoundCount(gamers.size());

        } catch (Throwable e) {
            log.error("发生异常", e);
        }
        return result;

    }

    private JSONObject fetchGameAccount(String url) throws UnsupportedEncodingException {
        String ret = HttpUtil.doPost(url, new JSONObject());
        if (ret != null && !"".equals(ret)) {
            return JSON.parseObject(ret);
        }
        return null;
    }

    private JSONObject getUrlParams(FilterBean filterBean) {
        JSONObject params = new JSONObject();
        if (filterBean.getPersonScore() != 0) {
            params.put("role_score", filterBean.getPersonScore());
        }
        if (filterBean.getTotalScore() != 0) {
            params.put("total_score", filterBean.getTotalScore());
        }
        if (filterBean.getSchools() != null && !"".equals(filterBean.getSchools())) {
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
        if (null != filterBean.getServerName() && !"".equals(filterBean.getServerName())
                && this.asInfo != null && !this.asInfo.isEmpty() && this.asInfo.containsKey(filterBean.getServerName())) {
            JSONObject as = this.asInfo.getJSONObject(filterBean.getServerName());

            params.put("serverid", as.getString("serverid"));

        }
        if (!filterBean.isShowPublish()) {
            params.put("pass_fair_show", "1");
        }
        return params;
    }

    private void loadAreaServerConfig() {

        try {
            this.asInfo = JSON.parseObject(new FileInputStream(this.asInfoFilePath), JSONObject.class);
        } catch (Throwable e) {
            log.error("加载大区服务器信息失败，", e);
        }
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
        return this.cbgDataUrl;
    }

    @Override
    public void setCbgURL(String cbgDataUrl) {
        this.cbgDataUrl = cbgDataUrl;
    }

    public String getAsInfoFilePath() {
        return asInfoFilePath;
    }

    public void setAsInfoFilePath(String asInfoFilePath) {
        this.asInfoFilePath = asInfoFilePath;
    }

    public DingNotify getDingNotify() {
        return dingNotify;
    }

    public void setDingNotify(DingNotify dingNotify) {
        this.dingNotify = dingNotify;
    }

    public static void main(String[] args) throws IOException {
        String a = "{\n" +
                "    \"storage_type\": 4, \n" +
                "    \"area_name\": \"双平台\", \n" +
                "    \"server_name\": \"星河千帆\", \n" +
                "    \"other_info\": {\n" +
                "        \"highlights\": [\n" +
                "            \"神兽x1\"\n" +
                "        ], \n" +
                "        \"school\": 1, \n" +
                "        \"format_equip_name\": \"大唐官府\", \n" +
                "        \"icon\": \"/game_res/res/photo/0007.png\", \n" +
                "        \"level_desc\": \"85级\", \n" +
                "        \"basic_attrs\": [\n" +
                "            \"总评分:26266\", \n" +
                "            \"人物评分:15125\"\n" +
                "        ], \n" +
                "        \"desc_sumup\": \"总评分:26266   人物评分:15125\", \n" +
                "        \"desc_sumup_short\": \"总评分:26266   人物评分:15125\"\n" +
                "    }, \n" +
                "    \"format_equip_name\": \"大唐官府\", \n" +
                "    \"icon\": \"/game_res/res/photo/0007.png\", \n" +
                "    \"collect_num\": 1, \n" +
                "    \"equip_level\": 85, \n" +
                "    \"serverid\": 180, \n" +
                "    \"allow_urs_bargain\": false, \n" +
                "    \"desc_sumup_short\": \"总评分:26266   人物评分:15125\", \n" +
                "    \"highlights\": [\n" +
                "        \"神兽x1\"\n" +
                "    ], \n" +
                "    \"expire_remain_seconds\": 1260981, \n" +
                "    \"platform_type\": 1, \n" +
                "    \"allow_bargain\": false, \n" +
                "    \"price\": 88800, \n" +
                "    \"game_channel\": \"netease\", \n" +
                "    \"level_desc\": \"85级\", \n" +
                "    \"game_ordersn\": \"201907271601716-180-IIYXVEEI8X0Q\", \n" +
                "    \"equip_type\": \"1\", \n" +
                "    \"pass_fair_show\": 0\n" +
                "}";
        JSONObject acc = JSON.parseObject(a);
        System.out.println(acc.toString(SerializerFeature.PrettyFormat));
        JSONArray o = acc.getJSONObject("other_info").getJSONArray("highlights");
        System.out.println(o);
        System.out.println(o.getClass());
        System.out.println(o.getString(0));
        System.out.println(Arrays.toString(o.toArray()));
    }


}
