package com.example.importCSV.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class CSVUsers {
    private UUID ID;
    private String username;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private String userbday;
    private Boolean active;
    private UUID userid;
    private String phone;
    private String email;
    private String oper;

}
