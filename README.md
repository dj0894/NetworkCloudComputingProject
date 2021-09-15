## Requirements 

- Creating New User Account  
- Updating the Credentials of the Existing User. 
- Creating Book Metadata for a User Account  
- Delete Book Metadata for a given Book Id Authorized for the User.
- Uploading a Image for Book Metadata. 
- Deleting a Image for a Book Metadata. 
- Retrieving all the Books for the User

## Security 

- Basic Auth is used for Create/Update/Delete APIs
- Password is stored by hashing it using a SALT 

## APIs

**Get User Information** ```GET /v1/user/self```

**Create New User** ```POST /v1/user```

**Upate User Details**  ```PUT /v1/user/self```

**Create a new Book** ```POST /books ```

**Book By Id** ```GET /books/{book_id}```

**Delete Book by Id** ```DELETE /books/{book_id} ```

**Get All Books** ```GET /books```

**Create Image in Book** ```POST /{book_id}/image```

**Delete a Book Image** ```DELETE /{book_id}/image/{image_id}```


### Technology Stack

- SpringBoot and SpringMVC for Service and REST-APIs
- Hosted on AWS EC2 
- Lambda, SES for Sending Emails for Book Creation, Deletion
- Terraform for Creating Infrastructure in AWS




### High Level Diagram

![CloudNetworkingProject](https://user-images.githubusercontent.com/71105442/133380009-7647998f-9a88-47df-9617-745162d5a046.jpeg)
