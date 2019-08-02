package com.uv.notify;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.uv.cbg.CbgGamer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author uvsun 2019-08-02 14:31
 */
public class DingNotify implements Notify {

    private static final Log log = LogFactory.getLog(DingNotify.class);

    @Value("#{config['notice.ding.URL']}")
    private String URL;


    @Override
    public void notify(List<CbgGamer> gamers) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(this.URL);
        if (gamers != null && gamers.size() > 0) {
            for (CbgGamer gamer : gamers) {
                OapiRobotSendRequest request = new OapiRobotSendRequest();

                /**
                 * eg代码：
                 * https://my.cbg.163.com/cgi/mweb/pl/role?platform_type=1&equip_level_min=69&equip_level_max=69&role_score=12587&total_score=20925&switch_in_serverid=29
                 */
                request.setMsgtype("link");
                OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
//                StringBuilder url = new StringBuilder().append("https://my.cbg.163.com/cgi/mweb/pl/role?platform_type=1");
//                switch (gamer.getLevel()) {
//                    case 0:
//                        url.append("&equip_level_min=69&equip_level_max=69");
//                        break;
//                    case 1:
//                        url.append("&equip_level_min=70&equip_level_max=89");
//                        break;
//                    case 2:
//                        url.append("&equip_level_min=90&equip_level_max=119");
//                        break;
//                    default:
//
//                }
//                if (gamer.getPersonScore() > 0) {
//                    url.append("&role_score=").append(gamer.getPersonScore());
//                }
//                if (gamer.getTotalScore() > 0) {
//                    url.append("&total_score=").append(gamer.getTotalScore());
//                }

                link.setMessageUrl(gamer.getUrl());
                link.setPicUrl("");
                link.setTitle("[" + gamer.getMenPai() + "]" + ":" + gamer.getPrice().toString());

                link.setText(
                        gamer.getServerName()
                                + "[" + gamer.getMenPai() + "]"
                                + gamer.getTotalScore() + "/" + gamer.getPersonScore()
                                + ":"
                                + gamer.getPrice().toString()
                );
                request.setLink(link);

                OapiRobotSendResponse response = client.execute(request);

                log.debug(response);
            }
        }


    }

}
