package io.github.danielzyla.pdcaApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ProjectWriteApiDto {
    private Long id;
    private String projectName, projectCode;
    private final List<Integer> departmentIds = new ArrayList<>();
    private final List<Long> productIds = new ArrayList<>();
}
