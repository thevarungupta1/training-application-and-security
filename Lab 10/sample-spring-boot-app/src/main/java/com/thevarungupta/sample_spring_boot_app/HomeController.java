package com.thevarungupta.sample_spring_boot_app;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public String hello(){
        return "Hello World!";
    }

    @RequestMapping(path = "/hello/{name}", method = RequestMethod.GET)
    public String helloWithName(@PathVariable("name") String name){
        return "Hello " + name + "!";
    }

    @RequestMapping(path = "/hello/{fname}/{lname}", method = RequestMethod.GET)
    public String helloWithFullName(@PathVariable("fname") String firstName,
                                    @PathVariable("lname") String lastName){
        return "Hello " + firstName + " " + lastName + "!";
    }
}
