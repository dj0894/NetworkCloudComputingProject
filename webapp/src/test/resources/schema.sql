CREATE TABLE users (
     id varchar(255) PRIMARY KEY NOT NULL,
     account_created datetime DEFAULT NULL,
     email_address varchar(255) NOT NULL,
     first_name varchar(255) NOT NULL,
     last_name varchar(255) NOT NULL,
     password varchar(255) NOT NULL,
     account_updated datetime DEFAULT NULL,
     UNIQUE KEY email_primary_key (email_address)
);

CREATE TABLE book (
      id varchar(255) PRIMARY KEY NOT NULL,
      author varchar(255) NOT NULL,
      book_created datetime NOT NULL,
      isbn varchar(255) NOT NULL,
      published_date varchar(255) NOT NULL,
      title varchar(255) NOT NULL,
      user_id varchar(255) DEFAULT NULL,
      FOREIGN KEY (user_id) references users(id)
);

CREATE TABLE file (
                      file_id varchar(255) PRIMARY KEY NOT NULL,
                      created_date datetime NOT NULL,
                      file_name varchar(255) NOT NULL,
                      s3_object_name varchar(255) NOT NULL,
                      book_id varchar(255) NOT NULL,
                      user_id varchar(255) NOT NULL,
                      FOREIGN KEY (user_id) references users(id),
                      FOREIGN KEY (book_id) references book(id)
);


