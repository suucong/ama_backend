package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * TodoDTO 뿐만 아니라 이후 다른 모델의 DTO 도 ResponseDTO 를 이용해 리턴할 수 있도록 자바 Generic 이용
 */


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO<T> {
    private String error;
    private List<T> data;
}
