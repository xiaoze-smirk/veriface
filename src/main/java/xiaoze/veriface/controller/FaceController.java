package xiaoze.veriface.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xiaoze.veriface.utils.FaceUtils;
import xiaoze.veriface.utils.Utils;

import java.io.IOException;
import java.util.HashMap;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @author xiaoze
 * @date 2018/1/26
 * 用谷歌或火狐浏览器
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

        FaceUtils faceUtils = new FaceUtils();
        byte[] buff = faceUtils.getStringImage(imgString.substring(imgString.indexOf(",")+1));
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
            byte[] bacd = faceUtils.post(url, map, byteMap);
            str = new String(bacd);
            System.out.println(str);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    //进入人脸检测界面
    @GetMapping(value="/toContrast")
    public String toContrast() throws IOException {
        return "/face/contrast_face.html";
    }

    @RequestMapping(value="/contrast")
    @ResponseBody
    public String contrast(String imgString,String number) throws IOException {
        if(isEmpty(number)) {
            return "没输入账号!";
        }
        System.out.println("账号："+number);

        FaceUtils faceUtils = new FaceUtils();
        String strOne ;
        String strTwo ;

        String[] sOne ;
        String[] sTwo ;

        Integer sLengthOne;
        Integer sLengthTwo;

        String faceTokenOne;
        String faceTokenTwo;
        int i=0;
        int j=0;


        while(true){
            strOne = faceUtils.check(faceUtils.getStringImage(imgString.substring(imgString.indexOf(",")+1)));
            sOne=strOne.split("\"");
            if(sOne[1].equals("error_message"))
                continue;
            //解决一张图片有多个人问题
            for(String str:sOne){
                if(str.equals("age"))
                    j++;
                if(j>1)
                    return "对不起，您上传的用户头像照片质量不达标(即不是单个有效头像)，请重新上传！";
            }
            sLengthOne = sOne.length;
            faceTokenOne = sOne[sLengthOne-2];
            i++;
            if(i>=10)
                break;
            break;
        }

        if(!sOne[sLengthOne-4].equals("face_token"))
            return "对不起，您上传的用户头像照片质量不达标(即不是单个有效头像)，请重新上传！";

        String imageLocalPath = "F:\\xiaoze\\Photo\\b.jpg";
        Utils utils = new Utils();

        i=0;
        while(true){
            strTwo = faceUtils.check(utils.toByteArray(imageLocalPath));
            sTwo=strTwo.split("\"");
            if(sTwo[1].equals("error_message"))
                continue;
            sLengthTwo = sTwo.length;
            faceTokenTwo = sTwo[sLengthTwo-2];
            i++;
            if(i>=10)
                break;
            break;
        }

        String compareInfo ;
        String[] compareInfoStr;
        while(true){
            compareInfo = faceUtils.compare(faceTokenOne,faceTokenTwo);
            compareInfoStr = compareInfo.split("\"");

            if(compareInfoStr[1].equals("error_message"))
                continue;
            break;
        }

        Double likeInfo = Double.parseDouble(compareInfoStr[2].substring(2, compareInfoStr[2].length()-2));
        if(likeInfo<70)
            return "认证不成功，请本人刷脸";
        return "认证成功";
    }

}
