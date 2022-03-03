package io.github.danielzyla.pdcaApp.dto;

import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.model.Project;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductReadDto {
    private final long id;
    private final String productName;
    private final String productCode;
    private final String serialNo;
    private final List<Project> projects;

    public ProductReadDto(Product source) {
        this.id = source.getId();
        this.productName = source.getProductName();
        this.productCode = source.getProductCode();
        this.serialNo = source.getSerialNo();
        this.projects = new ArrayList<>(source.getProjects());
    }
}
