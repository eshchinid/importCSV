package com.example.importCSV.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationMSG {
    Long rowNumber;
    String field;
    String message;
}
