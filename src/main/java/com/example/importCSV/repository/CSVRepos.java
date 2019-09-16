package com.example.importCSV.repository;

import com.example.importCSV.model.CSVUsers;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Log4j2
@Service
public class CSVRepos {
    private final String InsertToCSVUsers = "" +
            "INSERT INTO csvusers (username, userbday, active) " +
            "VALUES (:username, :userbday, :active)" +
            "RETURNING id";
    private final String InsertToCSVUserData = "" +
            "INSERT INTO csvuserdata (userid, phone, email, oper) " +
            "VALUES (:userid, :phone, :email, :oper)";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CSVRepos(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(CSVUsers csvUsers) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            params.addValue("userbday", new SimpleDateFormat("dd.MM.yyyy").parse(csvUsers.getUserbday()));
        } catch (ParseException e) {
            log.error("Ошибка парсинга даты");
            return;
        }




        params.addValue("username", csvUsers.getUsername());
        params.addValue("active", csvUsers.getActive());
        params.addValue("phone", csvUsers.getPhone());
        params.addValue("email", csvUsers.getEmail());


        UUID userid = jdbcTemplate.queryForObject(InsertToCSVUsers, params, UUID.class);

        params.addValue("userid", userid);

        jdbcTemplate.update(InsertToCSVUserData, params);

    }
}
