package com.thevarungupta.sample_spring_boot_app;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/employees")
@RestController
public class EmployeeController {

//    @RequestMapping(path = "/employees", method = RequestMethod.GET)
//    @GetMapping("/employees")
    @GetMapping
    public String getEmployees(){
        return "Get Method";
    }

//    @RequestMapping(path = "/employees", method = RequestMethod.POST)
//    @PostMapping("/employees")
    @PostMapping
    public String createEmployees(){
        return "Post Method";
    }

//    @RequestMapping(path = "/employees", method = RequestMethod.PUT)
//    @PutMapping("/employees")
    @PutMapping
    public String updateEmployees(){
        return "Put Method";
    }

//    @RequestMapping(path = "/employees", method = RequestMethod.DELETE)
//    @DeleteMapping("/employees")
    @DeleteMapping
    public String deleteEmployees(){
        return "Delete Method";
    }
}
