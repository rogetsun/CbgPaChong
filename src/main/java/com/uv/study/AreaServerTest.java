package com.uv.study;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author uvsun 2019/11/12 1:59 下午
 * 过滤as.json,只要双平台的区
 */
public class AreaServerTest {
    public static void main(String[] args) throws IOException {
        JSONArray ass = JSONArray.parseObject(new FileInputStream("src/main/resources/as.json"), JSONArray.class);
        System.out.println(ass);
        FileOutputStream fos = new FileOutputStream("src/main/resources/as-double-platform.json");
        JSONArray tmpArr = new JSONArray();
        for (Object o : ass) {
            JSONObject json = (JSONObject) o;
            if (json.getString("serverName").startsWith("双平台")) {
                tmpArr.add(json);
            }
        }
        System.out.println(tmpArr);
        tmpArr.sort((o1, o2) -> {
            JSONObject j1 = (JSONObject) o1;
            JSONObject j2 = (JSONObject) o2;
            return j1.getInteger("serverid") - j2.getInteger("serverid");
        });

        fos.write(tmpArr.toString(SerializerFeature.PrettyFormat).getBytes());
        fos.flush();
        fos.close();
        JSONObject jsonConf = new JSONObject(tmpArr.size());
        for (Object o : tmpArr) {
            JSONObject json = (JSONObject) o;
            jsonConf.put(json.getString("serverName"), json);
        }

        FileOutputStream fos2 = new FileOutputStream("src/main/resources/as-conf-double-platform.json");
        fos2.write(jsonConf.toString(SerializerFeature.PrettyFormat).getBytes());
        fos2.flush();
        fos2.close();
    }
}
