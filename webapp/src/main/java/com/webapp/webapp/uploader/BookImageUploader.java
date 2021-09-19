package com.webapp.webapp.uploader;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class BookImageUploader {

    private static final String accessKey = "<put your access>";
    private static final String secretKey = "<put your secret>";

    Logger logger = LoggerFactory.getLogger(BookImageUploader.class);

    AmazonS3 amazonS3;

    @Value("${images.bucket.name}")
    private String imageBucketName;

    @Autowired
    StatsDClient statsDClient;

    public BookImageUploader() {
        amazonS3 =  AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.US_EAST_1)
                    //.withCredentials(new AWSStaticCredentialsProvider(new
                      //      BasicAWSCredentials(accessKey, secretKey)))
                    .build();

    }

    public void deleteBookImageFromS3(String s3Key) {
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(imageBucketName)
                .withKeys(s3Key)
                .withQuiet(false);
        DeleteObjectsResult delObjRes = amazonS3.deleteObjects(multiObjectDeleteRequest);
        int successfulDeletes = delObjRes.getDeletedObjects().size();
        logger.info(successfulDeletes + " objects successfully deleted.");
    }

    public String uploadBookImageToS3(String s3Key, MultipartFile multipartFile){
        String s3ObjectName=null;
        long imageUploadS3Start = System.currentTimeMillis();
        logger.info("File upload in progress.");
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            s3ObjectName = uploadFileToS3Bucket(file, s3Key);
            logger.info("File upload is completed.");
            file.delete();  // To remove the file locally created in the project folder.

        } catch (final AmazonServiceException ex) {
            logger.info("File upload is failed.");
            logger.error("Error= {} while uploading file.", ex.getMessage());
        }
        long imageUploadS3Complete = System.currentTimeMillis();
        statsDClient.recordExecutionTime("ImageUploadTime", imageUploadS3Complete-imageUploadS3Start);
        return s3ObjectName;
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            logger.error("Error converting the multi-part file to file= ", ex.getMessage(), ex);
        }
        return file;
    }

    private String uploadFileToS3Bucket(final File file, final String s3Key) {
        logger.info("Uploading file with name= " + s3Key);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(imageBucketName, s3Key, file);
        amazonS3.putObject(putObjectRequest);
        return s3Key;
    }

}
