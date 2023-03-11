package com.example.demo.contorller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.TestRequestBodyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test") // URI 경로 매핑
public class TestController {
    @GetMapping // HTTP 메소드에 매핑
    public String testController(){
        return "Hello World!";
    }

    @GetMapping("/testGetMapping") // @GetMapping 으로도 URI 경로 지정 가능 !
    public String testControllerWithPath(){
        return "Hello World testGetMapping ";
    }

    @GetMapping("/{id}")
    public String testControllerWithPathVariables(@PathVariable(required = false) int id){
        return "Hello World! ID "+ id;
    }

    @GetMapping("/testRequestParam")
    public String testControllerRequestParam(@RequestParam(required = false) int id){
        return "Hello World! ID "+ id;
    }
    @GetMapping("/testRequestBody")
    public String testControllerRequestBody(@RequestBody TestRequestBodyDTO testRequestBodyDTO)
    {
        return "Hello world! ID "+ testRequestBodyDTO.getId()+ "Message: "+ testRequestBodyDTO.getMessage();
    }

    @GetMapping("/testResponseBody")
    public ResponseDTO<String> testControllerResponseBody()
    {
        List<String> list=new ArrayList<>();
        list.add("Hello world I'm ResponseDTO");
        ResponseDTO<String> response=ResponseDTO.<String>builder().data(list).build();
        return response;
    }

    @GetMapping("/testResponseEntity")
    public ResponseEntity<?> testControllerResponseEntity()
    {
        List<String> list=new ArrayList<>();
        list.add("Hello World! I'm ResponseEntity. And You got 400!");
        ResponseDTO<String> response=ResponseDTO.<String>builder().data(list).build();
        // http status 를 400으로 설정
        return ResponseEntity.badRequest().body(response);
    }


}

