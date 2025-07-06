package com.upc.campusflow.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
