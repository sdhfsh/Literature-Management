package com.mcy.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserVO {

    private String username;

    private String password;

    private String email;

    private String phone;
}
