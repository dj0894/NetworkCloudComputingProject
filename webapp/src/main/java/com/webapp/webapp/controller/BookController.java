package com.webapp.webapp.controller;

import com.timgroup.statsd.StatsDClient;
import com.webapp.webapp.convertors.BookConvertor;
import com.webapp.webapp.convertors.ImageConvertor;
import com.webapp.webapp.dto.Book;
import com.webapp.webapp.dto.Image;
import com.webapp.webapp.exception.BadRequestException;
import com.webapp.webapp.exception.ResourceNotFoundException;
import com.webapp.webapp.exception.UnauthorizedError;
import com.webapp.webapp.model.BookSNSEvent;
import com.webapp.webapp.publisher.SnsPublisher;
import com.webapp.webapp.repository.ImageRepository;
import com.webapp.webapp.repository.model.BookEntity;
import com.webapp.webapp.repository.model.ImageEntity;
import com.webapp.webapp.repository.model.User;
import com.webapp.webapp.model.UserCredentials;
import com.webapp.webapp.repository.BookRepository;
import com.webapp.webapp.repository.UserRepository;
import com.webapp.webapp.security.AuthenticationProvider;
import com.webapp.webapp.security.PasswordEncoder;
import com.webapp.webapp.uploader.BookImageUploader;
import com.webapp.webapp.validator.BookDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/books")
public class BookController {

    Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    BookImageUploader bookImageUploader;

    @Autowired
    StatsDClient statsDClient;

    @Autowired
    SnsPublisher snsPublisher;

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody Book book){

        long currentApiExecution = System.currentTimeMillis();
        BookDataValidator.checkBookData(book.getTitle(),book.getAuthor(), book.getIsbn(), book.getPublishedDate());
        UserCredentials userCredentials=AuthenticationProvider.fetchCredentialsFromHeader(authHeader);
        logger.info("Getting User with Email {}", userCredentials.getEmail());
        User user=userRepository.findByEmail(userCredentials.getEmail()).
                    orElseThrow(()-> new ResourceNotFoundException("User not found " + userCredentials.getEmail()));
        String password=user.getPassword();
        Boolean authenticUser=PasswordEncoder.checkPassword(userCredentials.getPassword(),password);

        BookEntity bookEntity = BookConvertor.convertBookToEntity(book, user);
        long databaseStart = System.currentTimeMillis();
        logger.info("Creating Book with Title {}", bookEntity.getTitle());
        BookEntity updatedEntity = bookRepository.save(bookEntity);
        long databaseEnd = System.currentTimeMillis();
        Book updated  = BookConvertor.convertEntityToBook(updatedEntity, new ArrayList<>());
        long endApiExecution = System.currentTimeMillis();
        statsDClient.recordExecutionTime("APICreateBookTime", endApiExecution-currentApiExecution);
        statsDClient.recordExecutionTime("DatabaseCreateBookTime", databaseEnd-databaseStart);
        statsDClient.increment("APICreateBookCount");
        snsPublisher.publishMessage(new BookSNSEvent(bookEntity.getId().toString(),
                userCredentials.getEmail(), "CREATE"));
        return ResponseEntity.ok().body(updated);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable(value="id") UUID id){

        long currentApiExecution = System.currentTimeMillis();
        Optional<BookEntity> bookEntity= (bookRepository.findById(id));
        if(!bookEntity.isPresent()){
            throw new ResourceNotFoundException("Book not found with book id : "+ id);
        }
        List<ImageEntity> imageEntities = imageRepository.findAllByBookId(bookEntity.get());
        List<Image> bookImages = getBookImages(imageEntities);
        Book book=BookConvertor.convertEntityToBook(bookEntity.get(), bookImages);
        statsDClient.increment("APIGetBookByIdCount");
        long endApiExecution = System.currentTimeMillis();
        statsDClient.recordExecutionTime("APIGetBookByIdTime", endApiExecution-currentApiExecution);
        return  ResponseEntity.ok().body(book);
    }

    @DeleteMapping("/{id}")
    public String deleteBookById(@RequestHeader("Authorization")String authHeader,@PathVariable(value = "id") UUID id){
        long currentApiExecution = System.currentTimeMillis();
        UserCredentials userCredentials=AuthenticationProvider.fetchCredentialsFromHeader(authHeader);
        Optional<User> user =userRepository.findByEmail(userCredentials.getEmail());
        if(!user.isPresent()){
            throw new ResourceNotFoundException("User does not exist : "+userCredentials.getEmail());
        }
        Boolean authenticUser=PasswordEncoder.checkPassword(userCredentials.getPassword(),user.get().getPassword());
        if(!authenticUser){
            throw new UnauthorizedError("Credentials Invalid");
        }

        Optional<BookEntity> bookEntity= (bookRepository.findById(id));
        if(!bookEntity.isPresent()){
            throw new ResourceNotFoundException("Book not found with book id : "+ id);
        }
        //check if the book created by the user
        if(!user.get().equals(bookEntity.get().getUser())){
            throw new UnauthorizedError("User is not authorized to delete");
        }
        List<ImageEntity> imageEntities = imageRepository.findAllByBookId(bookEntity.get());
        // remove this if you don't want to delete image automatically.
        imageEntities.forEach(imageEntity -> deleteBookImage(imageEntity));
        snsPublisher.publishMessage(new BookSNSEvent(bookEntity.get().getId().toString(),
                userCredentials.getEmail(), "DELETE"));
        long databaseStart = System.currentTimeMillis();
        bookRepository.delete(bookEntity.get());
        long databaseEnd = System.currentTimeMillis();
        statsDClient.recordExecutionTime("DatabaseDeleteBookCountTime", databaseEnd-databaseStart);

        long endApiExecution = System.currentTimeMillis();
        statsDClient.increment("APIDeleteBookCount");
        statsDClient.recordExecutionTime("APIDeleteBookTime", endApiExecution-currentApiExecution);
        String response="Book deleted";
        return response;
    }


    @GetMapping
    public List<Book> getAllBooks(){
        long currentApiExecution = System.currentTimeMillis();
        List<BookEntity> bookEntityList=new ArrayList<>(bookRepository.findAll());

        //Getting images of all books and mapping it to respective book. Returning as List
        List<Book> books = bookEntityList.stream().map(bookEntity -> {
            List<ImageEntity> imageEntities = imageRepository.findAllByBookId(bookEntity);
            List<Image> bookImages = getBookImages(imageEntities);
            Book book = BookConvertor.convertEntityToBook(bookEntity, bookImages);
            return book;
        }).collect(Collectors.toList());
        long endApiExecution = System.currentTimeMillis();
        statsDClient.increment("APIGetAllBooksCount");
        statsDClient.recordExecutionTime("APIGetAllBooksTime", endApiExecution-currentApiExecution);
        return books;
    }


    @PostMapping("/{book_id}/image")
    public ResponseEntity<Image> uploadImage(@RequestHeader("Authorization")String authHeader, @RequestParam("imageFile") MultipartFile file,
                                                   @PathVariable(value = "book_id") UUID id ) throws IOException {

        long currentApiExecution = System.currentTimeMillis();
        UserCredentials userCredentials= AuthenticationProvider.fetchCredentialsFromHeader(authHeader);
        Optional<User> user =userRepository.findByEmail(userCredentials.getEmail());
        //check if user is present in repository
        if(!user.isPresent()){
            throw new ResourceNotFoundException("User does not exist with email : "+userCredentials.getEmail());
        }
        //checking if user credentials matches with the credentials in respository
        Boolean authenticUser= PasswordEncoder.checkPassword(userCredentials.getPassword(),user.get().getPassword());
        if(!authenticUser){
            throw new UnauthorizedError("Credentials Invalid");
        }
        //check if book is created by authenticated user
        String userId=user.get().getId().toString();
        Optional<BookEntity> bookEntity= (bookRepository.findById(id));
        if(!bookEntity.isPresent()){
            throw new ResourceNotFoundException("Book not found with book id provided : "+ id);
        }
        String bookUserId=bookEntity.get().getUser().getId().toString();
        if(!userId.equals(bookUserId)){
            throw new UnauthorizedError(" User has not created book so user is not authorised to upload image : "+ id);
        }

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setFileName(file.getOriginalFilename());
        String imageFileName=file.getOriginalFilename();
        BookDataValidator.checkImageFileExtension(imageFileName);

        imageEntity.setUserId(user.get());
        imageEntity.setBookId(bookEntity.get());

        Optional<ImageEntity> existingImageEntity = imageRepository.findByS3ObjectName(buildS3Key(bookEntity
                        .get().getId().toString(),
                file.getOriginalFilename()));

        if (existingImageEntity.isPresent()) {
            throw new BadRequestException("Image with name already exists", "imageFile");
        }


        String s3ObjectName = bookImageUploader.uploadBookImageToS3(buildS3Key(bookEntity.get().getId().toString(),
                file.getOriginalFilename()), file);

        imageEntity.setS3ObjectName(s3ObjectName);
        long databaseStart = System.currentTimeMillis();
        ImageEntity savedEntity = imageRepository.save(imageEntity);
        long databaseEnd = System.currentTimeMillis();
        statsDClient.recordExecutionTime("DatabaseCreateImageCount", databaseEnd-databaseStart);

        Image image = ImageConvertor.convertEntityToImage(savedEntity);
        statsDClient.increment("APICreateImageCount");
        long endApiExecution = System.currentTimeMillis();
        statsDClient.recordExecutionTime("APICreateImageTime", endApiExecution-currentApiExecution);
        return  ResponseEntity.ok().body(image);
    }

    //Get book images
    private List<Image> getBookImages(List<ImageEntity> imageEntities) {
        List<Image> bookImages = imageEntities.stream().map(imageEntity -> ImageConvertor
                .convertEntityToImage(imageEntity))
                .collect(Collectors.toList());
        return bookImages;
    }


    @DeleteMapping("/{book_id}/image/{image_id}")
    public String deleteImageById(@RequestHeader("Authorization")String authHeader,@PathVariable(value = "book_id") UUID bookId,
                                  @PathVariable(value="image_id") UUID imageId){
        UserCredentials userCredentials=AuthenticationProvider.fetchCredentialsFromHeader(authHeader);
        Optional<User> user =userRepository.findByEmail(userCredentials.getEmail());
        if(!user.isPresent()){
            throw new ResourceNotFoundException("User does not exist : "+userCredentials.getEmail());
        }
        Boolean authenticUser=PasswordEncoder.checkPassword(userCredentials.getPassword(),user.get().getPassword());
        if(!authenticUser){
            throw new UnauthorizedError("Credentials Invalid");
        }

        Optional<BookEntity> bookEntity= (bookRepository.findById(bookId));
        if(!bookEntity.isPresent()){
            throw new ResourceNotFoundException("Book not found with book id : "+ bookId);
        }
        //check if the book created by the user
        if(!user.get().equals(bookEntity.get().getUser())){
            throw new UnauthorizedError("User is not authorized to delete");
        }

        //check if the book created by the user
        if(!user.get().equals(bookEntity.get().getUser())){
            throw new UnauthorizedError("User is not authorized to delete");
        }

        //find image by id in image Repository
        Optional<ImageEntity> imageEntity=imageRepository.findById(imageId);
        if(!imageEntity.isPresent()){
            throw new ResourceNotFoundException("Image not found with id : "+ imageId);
        }

        deleteBookImage(imageEntity.get());
        statsDClient.increment("APIDeleteImageCount");
        String response="Book image deleted";
        return response;

    }

    private void deleteBookImage(ImageEntity imageEntity) {
        bookImageUploader.deleteBookImageFromS3(imageEntity.getS3ObjectName());
        //delete Image
        imageRepository.delete(imageEntity);

    }


    private String buildS3Key(String bookId, String fileName) {
        return  bookId + "/" + fileName;
    }

}
