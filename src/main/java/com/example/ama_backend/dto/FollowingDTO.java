package com.example.ama_backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FollowingDTO<T> {
    private String error;
    private T data;

    public void setData(T data) {
        this.data = data;
    }
}
