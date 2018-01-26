package xiaoze.veriface.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xiaoze.veriface.utils.FaceUtils;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author xiaoze
 * @date 2018/1/26
 * 用火狐浏览器
 * Api网址：https://console.faceplusplus.com.cn/documents/4888373
 */
@Controller
@RequestMapping(value="/face")
public class FaceController {


    //进入人脸检测界面
    @GetMapping(value="/toDetect")
    public String toDetect() throws IOException {
        return "/face/detect_face.html";
    }

    //人脸各特征实现
    @RequestMapping(value="/detect")
    @ResponseBody
    public String getFace(String imgString) throws IOException {
        System.out.println(imgString);
        System.out.println(imgString.substring(imgString.indexOf(",")+1));

        FaceUtils faceUtils = new FaceUtils();
        byte[] buff = faceUtils.getStringImage(imgString.substring(imgString.indexOf(",")+1));
        String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", "N8j_5lchMSLKBK2F9b1KTthEnDJykqFs");
        map.put("api_secret", "IY1LQbLoJKZllVsPCAo2korpNnzv00cm");
        map.put("return_landmark", "1");
        map.put("return_attributes", "gender,age,smiling,headpose,facequality,blur,eyestatus,emotion,ethnicity,beauty,mouthstatus,eyegaze,skinstatus");
        byteMap.put("image_file", buff);
        String str =null;
        try{
            byte[] bacd = faceUtils.post(url, map, byteMap);
            str = new String(bacd);
            System.out.println(str);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

}
