package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product entity);
    List<Product> findAll();
    List<Product> findByProductNameContains(String phrase);
    void deleteById(Long id);
    Optional<Product> findById(Long id);
}
