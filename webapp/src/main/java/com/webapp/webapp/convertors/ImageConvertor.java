package com.webapp.webapp.convertors;

import com.webapp.webapp.dto.Image;
import com.webapp.webapp.repository.model.BookEntity;
import com.webapp.webapp.repository.model.ImageEntity;
import com.webapp.webapp.repository.model.User;



public class ImageConvertor {

    public static ImageEntity convertImageToImageEntity(BookEntity book, User user, Image image) {
        ImageEntity imageEntity=new ImageEntity();
        imageEntity.setFileName(image.getFileName());
        imageEntity.setUserId(user);
        imageEntity.setS3ObjectName(image.getS3ObjectName());
        return imageEntity;
    }

    public static Image convertEntityToImage(ImageEntity imageEntity) {
        Image image = new Image();
        image.setCreatedDate(imageEntity.getCreatedDate());
        image.setUserId(imageEntity.getUserId().getId().toString());
        image.setFileId(imageEntity.getId().toString());
        image.setFileName(imageEntity.getFileName());
        image.setS3ObjectName(imageEntity.getS3ObjectName());
        return image;
    }



}


