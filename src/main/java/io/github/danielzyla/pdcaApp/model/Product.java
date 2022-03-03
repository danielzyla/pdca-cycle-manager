package io.github.danielzyla.pdcaApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "products")
public class Product implements Serializable {
    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String productName, productCode;

    @NotBlank
    @Column(unique = true)
    private String serialNo;

    @ManyToMany(mappedBy = "products")
    @JsonBackReference
    private Set<Project> projects = new HashSet<>();

    public Product(String productName, String productCode, String serialNo) {
        this.productName = productName;
        this.productCode = productCode;
        this.serialNo = serialNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Objects.equals(productName, product.productName) && Objects.equals(productCode, product.productCode) && Objects.equals(serialNo, product.serialNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productName, productCode, serialNo);
    }

    @Override
    public String toString() {
        return "" + productName;
    }
}
