package com.example.pp.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ClientInfo из OpenApi")
public class ClientInfo {
    @Schema(description = "Уникальный идентификатор", example = "123-EWQ")
    private String clientId;
    @Schema(description = "Имя клиента", example = "Петр")
    private String name;
    @Schema(description = "Фамилия", example = "Петров")
    private String middleName;
    @Schema(description = "Отчество", example = "Петрович")
    private String surname;
    @Schema(description = "Возраст", example = "10")
    private Long age;
    @Schema(description = "Дата рождения", example = "12-10-2015")
    private LocalDate birthday;
    @Schema(description = "Номер телефона", example = "89374564456")
    private String phone;
}
