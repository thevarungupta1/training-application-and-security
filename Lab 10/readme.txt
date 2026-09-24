
# Spring Boot
- it is a spring module that provides rapid development features to the spring framework
- spring boot makes it easier to create stand alone, production ready spring based app that can JUST run
- it provides as easier and faster way to setup configuration and run both simple and web app


spring framework + embedded server - configuration = spring boot


convention over configurtion software design style
it decreases the effort of the developer
automatic configuration




# Advantages
- it create a stand alone spring app that can be started using java .jar file
- it test web app easily with the help of diffeernt embedded HTTP server such as tomcat, netty etc
- it provides optinated starter POM to simplify the maven configuration
- automatically configure spring and 3rd party libraries whenever possible
- provide production ready features such as metrics, health check and externalize configuration
- absolutely no gneration and no requirement for xml configuration
- it provide various plugin
- it increases productivity and reduce development time




# spring boot framework have more sub project which help to build app that address the business modern need
- spring data
- spring batch
- spring security
- spring cloud



# Disadvantages / limation
spring boot can use dependencies that are not going to be used in the app
these dependencies increase the size of the app



# spring boot annotation
- it if form of meta data that provides information about the progame or annotation are used to provide supplement information about the programme

@Required
@Autowired
@Configuration
@ComponentScan
@RestController
@Service




# Spring boot arcitecture
spring boot follow a layered architecture
there are 4 main layers which spring boot follow
- presentation layer
- business layer
- persistence layer
- database layer

client ---> controller --> service ---> repository ---> entity / data




# hwo to create project in spring boot
1. create a maven project and add starter dependencies
2. using spring initializer (recommaded) - https://start.spring.io
3. using IDE like STS
4. spring boot CLI (command line tool)



CRUD
C - create - post
R - read - get
U - update - put/patch
D - delete - delete
