package com.sky.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.common.comm.SignVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.io.ByteArrayInputStream;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil implements DisposableBean {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    private OSS ossClient;

    public AliOssUtil(String bucketName, String endpoint, String accessKeyId, String accessKeySecret) {

        try{
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            this.bucketName = bucketName;
            this.endpoint = endpoint;
        }catch(ClientException ce){
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }

    }

    /**
     * 文件上传
     *
     * @param bytes
     * @param objectName
     * @return
     */


    public String upload(byte[] bytes, String objectName) {


        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        }

        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);

        log.info("文件上传到:{}", stringBuilder.toString());

        return stringBuilder.toString();
    }



    @Override
    public void destroy() throws Exception {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
