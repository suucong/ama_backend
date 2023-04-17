package com.example.ama_backend.controller;


import com.example.ama_backend.dto.ResponseDTO;
import com.example.ama_backend.dto.TestRequestBodyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping
    public String testController(){
        return "This is AMA testController";
    }
    @GetMapping("/{id}")
    public String testControllerWithPathVariables(@PathVariable(required = false) int id){
        return "This is AMA testController ID"+ id;
    }
    @GetMapping("/testRequestParam")
    public String testControllerWithRequestParam(@RequestParam(required = false) int id){
        return "This is AMA testController ID" + id;
    }
    @GetMapping("/testRequestBody")
    public String testControllerWithRequestBody(@RequestBody TestRequestBodyDTO testRequestBodyDTO){
        return "This is AMA testController ID"+ testRequestBodyDTO.getId() +
                "message: "+ testRequestBodyDTO.getMessage();
    }
    @GetMapping("/testResponseBody")
    public ResponseDTO<String> testControllerWithResponseBody(){
        List<String> list=new ArrayList<>();
        list.add("This is AMA testController ResponseDTO");
        ResponseDTO<String> responseDTO=ResponseDTO.<String>builder().data(list).build();
        return responseDTO;
    }
    @GetMapping("/testResponseEntity")
    public ResponseEntity<?> testControllerWithResponseEntity(){
        List<String> list=new ArrayList<>();
        list.add("This is AMA testController ResponseDTO");
        ResponseDTO<String> responseDTO=ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.ok().body(responseDTO);
    }
}
