## Web Application Project 

   This project contains a Web Application which hosts REST API
to do operations on user and books data. 

## Technologies Used for this Project

- Spring Boot, Spring MVC Framework 
- Hibernate, JPA

## Requirements

- JDK1.8 
- SQL Server 

## Steps to Build , Install and Deploy the Application Locally

1. Clone the application

	`git clone https://github.com/dj0894/webapp.git`

2. Install MySQL Database. Follow this [link](https://dev.mysql.com/doc/mysql-osx-excerpt/5.7/en/osx-installation-pkg.html) to install

3. Create the database 
   
	`create database userDB`

3. Update mysql username and password as per your installation

	open `src/main/resources/application.properties`

	change `spring.datasource.username` and `spring.datasource.password` as per your mysql installation

4. Build and run the app using maven 
	
- If Using CLI

```        
1. Go to project folder and run below command.

2 ./mvnw clean

3 ./mvnw install

4 ./mvnw spring-boot:run

5. ./mvnw package  or ./mvnw package -Dmaven.test.skip=true 
```     
 	
- If using IntelliJ IDE Directly s

```	   
1. Open project using IDE

2. Run src/main/java/com.webapp.webapp.WebApplication
```

## Explore Rest APIs

The app defines following REST APIs.

`GET  /v1/user/self`

`POST /v1/user`

`PUT /v1/user/self`


## Test the REST API
You can test them using POSTMAN or through CURL

###Examples

- Create User

```
curl -X POST -H "Content-Type: application/json" -d '{"firstName": "Deepika", "lastName": "Jha", "email": "deepikajha08@gmail.com", "password": "Deepikajha@123"}' http://localhost:8080/v1/user
```

- Get User

```
curl --user "deepikajha08@gmail.com:Deepikajha@123" http://localhost:8080/v1/user/self
```

- Put User

```
curl --user "deepikajha08@gmail.com:Deepikajha@123" -X PUT -H "Content-Type: application/json"  -d '{"firstName": "Deepika Kumari", "lastName": "Jha", "password": "Deepikajha@123"}' http://localhost:8080/v1/user/self
```


##Commands to import the SSL certificate

``aws acm import-certificate --certificate fileb://Certificate.pem \
        --certificate-chain fileb://CertificateChain.pem \
        --private-key fileb://PrivateKey.pem ``
        
 `` aws acm import-certificate --certificate fileb://prod_deepikajha_me.crt \
         --certificate-chain fileb://prod_deepikajha_me.ca-bundle \
         --private-key fileb://private.pem ``