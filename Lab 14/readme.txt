# JWT - JSON Web Token

- it is open standard that defines a compact and self contained way for securely transmitting information between the parties as a json object

- JWT or JSON web token is a standard that is mostly used for securing APIs

- JWT follows stateless authentication mechanism


# What is JSON web token structure

xxx.yyy.zzz

xxx - header
yyy - payload
zzz - signature





1. create JWTAuthenticationEntryPoint
2. add JWT properties in application.properties file
3. create JWTTokenProvider - utility class
4. create JWTAuthenticationFilter
5. Create JWTAuthResponse DTO
6. Configure JWT in spring security
7. change the login/sign rest api to return JWT token







<!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>

<!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>

<!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-jackson -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>




Authorization: Bearer asjmapsjmaOPSMaxmaoxMAXOK
