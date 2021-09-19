package com.webapp.webapp.publisher;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.webapp.model.BookSNSEvent;
import com.webapp.webapp.uploader.BookImageUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnsPublisher {
    Logger logger = LoggerFactory.getLogger(BookImageUploader.class);

    public AmazonSNS amazonSNS;
    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${topic.arn}")
    private String topicArn;

    public SnsPublisher() {
        amazonSNS = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    }

    public void publishMessage(BookSNSEvent bookSNSEvent) {
        try {
            String message = objectMapper.writeValueAsString(bookSNSEvent);
            amazonSNS.publish(topicArn, message);
            logger.info("Published Message to SNS {}", message);
        } catch (JsonProcessingException e) {
            logger.warn("Failed in Sending the SNS Event", e);
        }
    }
}
