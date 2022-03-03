package io.github.danielzyla.pdcaApp.dto;

import lombok.Getter;

@Getter
public class EmployeeWriteApiDto {
    private long id;
    private String name, surname;
    private String email;
    private int departmentId;
}
