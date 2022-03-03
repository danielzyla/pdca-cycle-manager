package io.github.danielzyla.pdcaApp.repository.jpa;

import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.repository.ProductRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface JpaProductRepository extends ProductRepository, JpaRepository<Product, Long> {
    @Override
    Product save(Product entity);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"projects"}
    )
    List<Product> findAll();

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"projects"}
    )
    List<Product> findByProductNameContains(String phrase);

    @Override
    void deleteById(Long id);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"projects"}
    )
    Optional<Product> findById(Long id);
}
