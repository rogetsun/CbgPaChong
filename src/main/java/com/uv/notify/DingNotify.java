package com.uv.notify;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.uv.cbg.CbgGamer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author uvsun 2019-08-02 14:31
 */
public class DingNotify implements Notify {

    private static final Log log = LogFactory.getLog(DingNotify.class);

    @Value("#{config['notice.file.path']}")
    private String filePath;

    @Value("#{config['notice.ding.URL']}")
    private String URL;

    private JSONObject noticeJson;

    @Override
    public int notify(List<CbgGamer> gamers) throws ApiException {

        File noticeFile = new File(this.filePath);
        if (noticeFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(noticeFile);
                byte[] bytes = new byte[fis.available()];
                fis.read(bytes);
                fis.close();
                noticeJson = JSONObject.parseObject(new String(bytes, StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("", e);
            }
        }
        if (noticeJson == null) {
            noticeJson = new JSONObject();
        }
        int noticedNum = 0;
        if (gamers != null && gamers.size() > 0) {
            for (CbgGamer gamer : gamers) {
                if (noticeJson.containsKey(gamer.getTotalScore() + "-" + gamer.getPrice().toString())) {
                    continue;
                }
                noticeJson.put(gamer.getTotalScore() + "-" + gamer.getPrice().toString(), "[" + gamer.getServerName() + "]" + gamer.getSchoolName());
                noticedNum++;
                /**
                 * eg代码：
                 * https://my.cbg.163.com/cgi/mweb/pl/role?platform_type=1&equip_level_min=69&equip_level_max=69&role_score=12587&total_score=20925&switch_in_serverid=29
                 */

                String title = (gamer.isPublished() ? "[公]" : "") + (gamer.isAllowBargain() ? "[还]" : "") + "[" + gamer.getSchoolName() + "]" + ":￥" + gamer.getPrice().toString() + "元";
                String content = gamer.getServerName() + "\n"
                        + "[" + gamer.getTotalScore() + "/" + gamer.getPersonScore() + "]\n"
                        + gamer.getHighLights();
                String msgUrl = gamer.getUrl();
                String picUrl = gamer.getHeadIconLink();
                this.sendLinkMsg(title, content, msgUrl, picUrl);
            }

        }

        try {
            if (!noticeFile.exists()) {
                noticeFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(noticeFile);
            byte[] bytes = noticeJson.toString(SerializerFeature.PrettyFormat).getBytes(StandardCharsets.UTF_8);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            log.error("", e);
        }
        return noticedNum;
    }

    /**
     * 发送藏宝阁 帐号信息 link的钉钉通知
     *
     * @param title
     * @param content
     * @param msgUrl
     * @param picUrl
     * @return
     * @throws ApiException
     */
    @Override
    public OapiRobotSendResponse sendLinkMsg(String title, String content, String msgUrl, String picUrl) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(this.URL);
        OapiRobotSendRequest request = new OapiRobotSendRequest();

        request.setMsgtype("link");
        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
        link.setMessageUrl(msgUrl);
        link.setPicUrl(picUrl);
        link.setTitle(title);
        link.setText(content);
        request.setLink(link);

        OapiRobotSendResponse response = client.execute(request);

        log.debug(response);
        return response;
    }

    @Override
    public OapiRobotSendResponse sendTextMsg(String content) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(this.URL);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(content);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(Arrays.asList("13835176799"));
        request.setAt(at);
        OapiRobotSendResponse response = client.execute(request);
        log.debug(response);
        return response;
    }

    @Override
    public OapiRobotSendResponse sendMarkDownMsg(String title, String content) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(this.URL);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        markdown.setText(content);
        request.setMarkdown(markdown);
        OapiRobotSendResponse response = client.execute(request);
        log.debug(response);
        return response;
    }

}
