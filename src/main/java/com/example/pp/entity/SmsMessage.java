package com.example.pp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class SmsMessage {
    private String phone;
    private String message;
}
