package com.webapp.webapp.validator;

import com.webapp.webapp.exception.BadRequestException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BookDataValidator {

    private static final Set<String> VALID_IMAGE_EXTENSIONS = new HashSet<String>() {{
        add("jpg");
        add("jpeg");
        add("png");
    }};

    public static void checkBookData(String title, String author, String isbn, String published_date){
        if(title==null||title.trim().length()==0){
            throw new BadRequestException("Invalid title",title);
        }

        if(author==null||author.trim().length()==0){
            throw new BadRequestException("Invalid author",author);
        }


        if(isbn==null||isbn.trim().length()==0||isbn.length() != 13){
            throw new BadRequestException("Invalid isbn.Please refer documentation at https://www.isbn-international.org/content/what-isbn for valid ISBN number",isbn);
        }

        if(published_date==null||published_date.trim().length()==0){
            throw new BadRequestException("Invalid published_date",published_date);
        }

    }

    private static boolean isAValidISBN(long isbn) {
        return getSum(isbn) % 10 == 0;
    }

    private static int getSum(long isbn) {
        int count = 0;
        int sum = 0;
        do {
            sum += count % 2 == 0 ? isbn % 10 : 3 * (isbn % 10);
            count++;
            isbn /= 10;
        } while (isbn > 0);
        return sum;
    }

    public static void checkImageFileExtension(String imageFileName){

        String extension = imageFileName.substring(imageFileName.lastIndexOf(".")+1);
        if (!VALID_IMAGE_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Supported File Extensions are  jpeg/jpg/png", "imageFile");
        }
    }
}
