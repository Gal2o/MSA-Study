package com.example.userservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Greeting {
    // @Value를 사용해서 message 변수에 ${} 값을 넣어준다.
    @Value("${greeting.message}")
    private String message;
}
