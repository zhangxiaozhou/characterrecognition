package com.zxz.recognition.srevice;

import com.baidu.aip.http.AipHttpClient;
import com.baidu.aip.util.Base64Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class RecognitionService {

    private static final String ak = "ZILHYFH1CWUTfumuZiadwHDV";
    private static final String sk = "w6K4ABTxkNl0PfzB4Ys7L49054WFWsud";

    public void recognition(String filePath) throws Exception {

        String otherHost = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate";

        byte[] imgData = FileUtil.readFileByBytes(filePath);

        String imgStr = Base64Util.encode(imgData);

        String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
        /**
         * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
         */
        String accessToken = getAuth(ak, sk);
        String result = HttpUtil.post(otherHost, accessToken, params);


        ObjectMapper mapper = new ObjectMapper();
        RecognitionResult recognitionResult = mapper.readValue(result, RecognitionResult.class);


        printResult(recognitionResult);

    }

    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);

            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    public void printResult(RecognitionResult recognitionResult){

        List<WordResult> words_result = recognitionResult.getWords_result();

        if(words_result!=null && !words_result.isEmpty()){

            int tempTop = 0;
            int tempLeft = 0;

            for(WordResult wordResult : words_result){

                WordLocation location = wordResult.getLocation();

                if(location.getTop() - tempTop > 10){
                    System.out.println();
                    tempTop = location.getTop();
                }

                int left = location.getLeft();

                if(left - tempLeft > 10){
                    int space = (left-tempLeft)/15;
                    for(int i=0; i<space; i++){
                        System.out.print(" ");
                    }
                    tempLeft = left;
                }

                System.out.println(wordResult.getWords());
            }

        }

        System.out.println("====================================================================================================================");

    }

    public static void main(String[] args) {

        String path = "/home/allen/weixin";

        File file = new File(path);

        File[] files = file.listFiles();

        List<String> fileNames = new ArrayList<>();
        for(int i=0; i<files.length; i++){
            fileNames.add(files[i].getAbsolutePath());
        }

        fileNames.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return  o1.compareTo(o2);
            }
        });

        for(String fileName : fileNames){

            //System.out.println(fileName);

            try {
                new RecognitionService().recognition(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
