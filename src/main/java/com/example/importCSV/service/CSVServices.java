package com.example.importCSV.service;

import com.example.importCSV.model.CSVUsers;
import com.example.importCSV.model.ValidationMSG;
import com.example.importCSV.repository.CSVRepos;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
public class CSVServices {
    @Autowired
    CSVRepos csvRepos;

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    public ResponseEntity savefile(InputStream stream) {
        List<CSVUsers> csvUsers = fileToList(stream);
        if (csvUsers.size() == 0)
            return ResponseEntity.badRequest().body(new ValidationMSG(-1L, "", "Файл пуст или ошибка парсинга файла"));

        // Массив для хранения ошибок
        List<ValidationMSG> errors_in_rows = new ArrayList<>();

        log.info("---Аналитика файла---");
        for (int i = 0; i < csvUsers.size(); i++) {
            try {
                log.info("Analysis [{}] row", i);
                Set<ConstraintViolation<CSVUsers>> violations = validator.validate(csvUsers.get(i));
                if (violations.size() > 0) {
                    for (ConstraintViolation constraintViolation : violations) {
                        errors_in_rows.add(
                                new ValidationMSG(
                                        (long) i,
                                        constraintViolation.getPropertyPath().toString(),
                                        constraintViolation.getMessage()
                                )
                        );
                        log.info("Поле [{}] не соответствует условию [{}]", constraintViolation.getPropertyPath(), constraintViolation.getMessage());
                    }
                } else {
                    log.info("Can save to DB");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        log.info("--- Finish analysis. Count errors [{}] ---", errors_in_rows.size());
        if (errors_in_rows.size() == 0) {
            log.info("--- Start save to DB ---");
            for (int i = 0; i < csvUsers.size(); i++) {
                try {

                    csvRepos.save(csvUsers.get(i));
                    log.info("--- Writing a string [{}] to the database ---", i);
                } catch (Exception e) {
                    errors_in_rows.add(new ValidationMSG((long) i, "", e.getMessage()));
                    log.error(e.getMessage());
                }
            }
            log.info("--- Finish save to DB ---");
            if (errors_in_rows.size() == 0)
                return ResponseEntity.ok(new ValidationMSG(-1L, "", "Данных записано успешно [" + csvUsers.size() + "]"));
            else return ResponseEntity.status(500).body(errors_in_rows);
        } else {
            return ResponseEntity.badRequest().body(errors_in_rows);

        }
    }


    private List<CSVUsers> fileToList(InputStream stream) {

        CsvMapper mapper = new CsvMapper();

        CsvSchema schema = mapper.schemaFor(CSVUsers.class).withHeader().withColumnReordering(true).withColumnSeparator(';');

        ObjectReader reader = mapper.readerFor(CSVUsers.class).with(schema);

        try {

            return reader.<CSVUsers>readValues(stream).readAll();

        } catch (IOException e) {

            log.error("Ошибка парсинга файла");

            return new ArrayList<>();

        }
    }
}
