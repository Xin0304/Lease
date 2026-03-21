package com.sanjin.lease.web.admin.service.impl;

import com.sanjin.lease.common.config.MinioProperties;
import com.sanjin.lease.web.admin.service.FileService;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;
    @Override
    public String upLoad(MultipartFile file) {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucketname())
                    .build());
            if (!bucketExists){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketname()).build());
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                                .bucket(minioProperties.getBucketname())
                                .config(creteBUcketPolicyConfig(minioProperties.getBucketname()))
                        .build());
            }



            String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) +
                    "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketname())
                            .object( filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                    .build());

            return String.join("/", minioProperties.getEndpoint(),
                                minioProperties.getBucketname(),filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String creteBUcketPolicyConfig(String bucketname) {
        return """
            {
              "Statement" : [ {
                "Action" : "s3:GetObject",
                "Effect" : "Allow",
                "Principal" : "*",
                "Resource" : "arn:aws:s3:::%s/*"
              } ],
              "Version" : "2012-10-17"
            }
            """.formatted(bucketname);
    }

}
