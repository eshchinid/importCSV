package com.example.importCSV.controller;

import com.example.importCSV.model.ValidationMSG;
import com.example.importCSV.repository.CSVRepos;
import com.example.importCSV.service.CSVServices;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Log4j2
@RestController
public class CSVController {
    @Autowired
    private CSVServices csvServices;

    @PostMapping(value = "/csv/import", consumes = "multipart/form-data")
    public ResponseEntity SimCardCSV (@Valid @RequestParam("file") MultipartFile file) {

        try {
            return csvServices.savefile(file.getInputStream());
        }catch (IOException e){
            log.error("Ошибка чтения файла");
            return ResponseEntity.badRequest().body(new ValidationMSG( -1L, "", "Ошибка чтения файла"));
        }
    }
}
