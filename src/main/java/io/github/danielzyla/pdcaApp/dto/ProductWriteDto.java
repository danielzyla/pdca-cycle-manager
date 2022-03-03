package io.github.danielzyla.pdcaApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.model.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class ProductWriteDto {
    private long id;
    @NotBlank(message = "pole wymagane")
    private String productName, productCode, serialNo;
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();

    public Product toSave() {
        var product = new Product();
        product.setProductName(productName);
        product.setProductCode(productCode);
        product.setSerialNo(serialNo);
        product.setProjects(new HashSet<>(projects));
        return product;
    }
}
