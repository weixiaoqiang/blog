package com.mszlu.blog.utils;

import com.alibaba.fastjson.JSON;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class QiniuUtils {

    public static  final String url = "http://qyjh25oc1.hn-bkt.clouddn.com/";

    private  String accessKey="Y9hVGSRfIF2SDRsCP9L2Y4c_T2X_yiBZBklQE1g3";
    private  String accessSecretKey="egILw68oKrmwl-9rSru1E6WmHJXEDMYmbsRPSVhu";
    String bucket = "myweiqiang";


    public  boolean upload(MultipartFile file,String fileName){

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        try {
            byte[] uploadBytes = file.getBytes();
            Auth auth = Auth.create(accessKey, accessSecretKey);
            String upToken = auth.uploadToken(bucket);
                Response response = uploadManager.put(uploadBytes, fileName, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
               System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return true;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        return false;
    }
}
