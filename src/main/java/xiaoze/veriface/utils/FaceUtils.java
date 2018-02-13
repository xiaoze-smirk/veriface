package xiaoze.veriface.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import javax.net.ssl.SSLException;
import sun.misc.BASE64Decoder;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author xiaoze
 * @date 2018/1/26
 */
public class FaceUtils {

    public String compare(String token1, String token2) {

        String compareUrl = "https://api-cn.faceplusplus.com/facepp/v3/compare";

        List<BasicNameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("api_key", "N8j_5lchMSLKBK2F9b1KTthEnDJykqFs"));  //涉及到face++技术,请你使用你自己的api_key
        formparams.add(new BasicNameValuePair("api_secret", "IY1LQbLoJKZllVsPCAo2korpNnzv00cm"));  //涉及到face++技术,请你使用你自己的api_secret
        formparams.add(new BasicNameValuePair("face_token1", token1));
        formparams.add(new BasicNameValuePair("face_token2", token2));
        return postHttp(formparams, compareUrl);
    }

    private static String postHttp(List<BasicNameValuePair> formparams, String url) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        UrlEncodedFormEntity uefEntity;
        String returnStr = "";
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    returnStr = EntityUtils.toString(entity, "UTF-8");
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + returnStr);
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnStr;

    }


    public static String checkFace(String imgString) throws IOException {
        byte[] buff = getStringImage(imgString.substring(imgString.indexOf(",")+1));
        return check(buff);
    }

    public static String check(byte[] buff) {

        String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";

        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", "N8j_5lchMSLKBK2F9b1KTthEnDJykqFs");  //涉及到face++技术,请你使用你自己的api_key
        map.put("api_secret", "IY1LQbLoJKZllVsPCAo2korpNnzv00cm");  //涉及到face++技术,请你使用你自己的api_secret
        map.put("return_landmark", "1");
        map.put("return_attributes", "gender,age,smiling,headpose,facequality,blur,eyestatus,emotion,ethnicity,beauty,mouthstatus,eyegaze,skinstatus");
        byteMap.put("image_file", buff);
        String str =null;
        try{
            byte[] bt = post(url, map, byteMap);
            str = new String(bt);
            System.out.println("返回的数据是："+str);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * Base64字符串转 二进制流
     *
     * @param base64String Base64
     * @return base64String
     * @throws IOException 异常
     */
    public static byte[] getStringImage(String base64String) throws IOException {
        BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        return base64String != null ? decoder.decodeBuffer(base64String) : null;
    }



    private final static int CONNECT_TIME_OUT = 30000;
    private final static int READ_OUT_TIME = 50000;
    private static String boundaryString = getBoundary();
    public static byte[] post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
        HttpURLConnection conne;
        URL url1 = new URL(url);
        conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
        Iterator iter = map.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry) iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key
                    + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }
        if(fileMap != null && fileMap.size() > 0){
            Iterator fileIter = fileMap.entrySet().iterator();
            while(fileIter.hasNext()){
                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();
                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey()
                        + "\"; filename=\"" + encode(" ") + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.write(fileEntry.getValue());
                obos.writeBytes("\r\n");
            }
        }
        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();
        InputStream ins = null;
        int code = conne.getResponseCode();
        try{
            if(code == 200){
                ins = conne.getInputStream();
            }else{
                ins = conne.getErrorStream();
            }
        }catch (SSLException e){
            e.printStackTrace();
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int len;
        while((len = ins.read(buff)) != -1){
            baos.write(buff, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        ins.close();
        return bytes;
    }
    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }
    private static String encode(String value) throws Exception{
        return URLEncoder.encode(value, "UTF-8");
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

