package xiangcai.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

@RestController
public class DeWaterController {
    /**
     * 根据分享视频 获取小程序可以下载的链接
     *
     * @param link
     * @return
     * @throws Exception
     */
    @PostMapping("/deWater")
    public String deWater(@RequestBody String link) throws Exception {
        // link = "6.1 pD:/ “所以你只是心动过，并没有坚定的选择我，对吗?”%2020抖音最火音乐 %江西美好推荐官 %南昌  %明日冬至  https://v.douyin.com/ehVqbWA/ 復制此鏈接，哒开Dou音搜索，直接觀看视pin！";

        //①：过滤视频得到http链接地址
        String finalUrl = decodeHttpUrl(link);

        final String videoPath = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";

        Connection con = Jsoup.connect(finalUrl);

        con.header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
        Connection.Response resp = con.method(Connection.Method.GET).execute();
        String strUrl = resp.url().toString();
        String itemId = strUrl.substring(strUrl.indexOf("video/"), strUrl.lastIndexOf("/")).replace("video/", "");
        String videoUrl = videoPath + itemId;
        String jsonStr = Jsoup.connect(videoUrl).ignoreContentType(true).execute().body();
        JSONObject json = new JSONObject(jsonStr);

        String videoAddress = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list").get(0).toString();


        HashMap headers = MapUtil.newHashMap();
        headers.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
        String replace = videoAddress.replace("playwm", "play"); //替换成无水印链接


        //获取重定向之后的链接
        String realUrl = redictUrl(replace);


        return realUrl;
    }


    /**
     * 过滤视频得到http链接地址
     *
     * @param url
     * @return
     */
    public static String decodeHttpUrl(String url) {
        int start = url.indexOf("http");
        int end = url.lastIndexOf("/");
        String decodeurl = url.substring(start, end);
        return decodeurl;
    }


    /**
     * 获取重定向之后的链接
     *
     * @param url
     * @return
     */
    private static String redictUrl(String url) {
        try {
            StringBuffer buffer = new StringBuffer();

            //发送get请求
            URL serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("GET");
            //必须设置false，否则会自动redirect到重定向后的地址
            conn.setInstanceFollowRedirects(false);
            conn.addRequestProperty("Accept-Charset", "UTF-8;");
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
            conn.addRequestProperty("Referer", "http://matols.com/");
            conn.connect();

            //判定是否会进行302重定向
            if (conn.getResponseCode() == 302) {
                //如果会重定向，保存302重定向地址，以及Cookies,然后重新发送请求(模拟请求)
                String location = conn.getHeaderField("Location");
                String cookies = conn.getHeaderField("Set-Cookie");


                serverUrl = new URL(location);

                conn = (HttpURLConnection) serverUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Charset", "UTF-8;");
                conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
                conn.addRequestProperty("Referer", "http://matols.com/");
                conn.connect();
                //将http替换成https，不然小程序上线请求不到
                String finalUrl = location.replaceFirst("http", "https");
                System.out.println("跳转地址:" + finalUrl);
                return finalUrl;
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }


    /**
     * 此接口会直接保存到电脑本机
     *
     * @param link
     * @return
     * @throws Exception
     */
    @PostMapping("/deWaterAndDown")
    public String deWaterAndDown(@RequestBody String link) throws Exception {
        //①：过滤视频得到http链接地址
        String finalUrl = decodeHttpUrl(link);

        final String videoPath = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";

        Connection con = Jsoup.connect(finalUrl);

        con.header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
        Connection.Response resp = con.method(Connection.Method.GET).execute();
        String strUrl = resp.url().toString();
        String itemId = strUrl.substring(strUrl.indexOf("video/"), strUrl.lastIndexOf("/")).replace("video/", "");
        String videoUrl = videoPath + itemId;
        String jsonStr = Jsoup.connect(videoUrl).ignoreContentType(true).execute().body();
        JSONObject json = new JSONObject(jsonStr);

        String videoAddress = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list").get(0).toString();
        //抖音描述
        //String desc = json.getJSONArray("item_list").getJSONObject(0).getStr("desc");


        HashMap headers = MapUtil.newHashMap();
        headers.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
        String replace = videoAddress.replace("playwm", "play"); //替换成无水印链接
        HttpResponse execute = HttpUtil.createGet(replace).addHeaders(headers).execute();
        String finalVideoAddress = execute.header("Location");
        return downloadVideo(finalVideoAddress);
    }

    /**
     * 下载
     *
     * @param videoAddress
     */
    private static String downloadVideo(String videoAddress) {
        String info = "";
        int byteRead;
        try {
            URL url = new URL(videoAddress);
            //获取链接
            URLConnection conn = url.openConnection();
            //输入流
            InputStream inStream = conn.getInputStream();
            //封装一个保存文件的路径对象
            File fileSavePath = new File("D:/douyin/" + System.currentTimeMillis() + ".mp4");
            //注:如果保存文件夹不存在,那么则创建该文件夹
            File fileParent = fileSavePath.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (fileSavePath.exists()) { //如果文件存在，则删除原来的文件
                fileSavePath.delete();
            }
            //写入文件
            FileOutputStream fs = new FileOutputStream(fileSavePath);
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            inStream.close();
            fs.close();
            info = "\n-----视频保存路径-----\n" + fileSavePath.getAbsolutePath();
            return info;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

}
