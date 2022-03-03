package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.ProductReadDto;
import io.github.danielzyla.pdcaApp.dto.ProductWriteDto;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    public static final ResourceNotFoundException RESOURCE_NOT_FOUND_EXCEPTION = new ResourceNotFoundException(
            "Nie istnieje rekord dla podanego id"
    );
    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductReadDto create(final ProductWriteDto productWriteDto) {
        Product saved = productRepository.save(productWriteDto.toSave());
        return new ProductReadDto(saved);
    }

    public List<ProductReadDto> getAll() {
        return productRepository.findAll().stream()
                .map(ProductReadDto::new)
                .collect(Collectors.toList());
    }

    public Page<ProductReadDto> getAllPaged(Pageable pageable) {
        List<Product> found = productRepository.findAll();
        Sublist sublist = new Sublist(pageable);
        return new PageImpl<>(
                sublist.getSublist(found).stream().map(ProductReadDto::new).collect(Collectors.toList()),
                pageable,
                found.size()
        );
    }

    public Page<ProductReadDto> searchByName(final String phrase, Pageable pageable) {
        List<Product> foundByName = productRepository.findByProductNameContains(phrase);
        Sublist sublist = new Sublist(pageable);
        return new PageImpl<>(
                sublist.getSublist(foundByName).stream().map(ProductReadDto::new).collect(Collectors.toList()),
                pageable,
                foundByName.size()
        );
    }

    public ProductReadDto getById(Long id) {
        Optional<Product> found = productRepository.findById(id);

        if (found.isPresent()) {
            return new ProductReadDto(found.get());
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }

    public void update(ProductWriteDto update) {
        Optional<Product> found = productRepository.findById(update.getId());

        if (found.isPresent()) {
            Product toSave = found.get();
            toSave.setProductName(update.getProductName());
            toSave.setProductCode(update.getProductCode());
            toSave.setSerialNo(update.getSerialNo());
            productRepository.save(toSave);
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }

    public List<Product> getProductList(List<Long> productsIds) {
        List<Product> productList = new ArrayList<>();
        for (Long id : productsIds) {
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isPresent()) {
                productList.add(optionalProduct.get());
            } else throw RESOURCE_NOT_FOUND_EXCEPTION;
        }
        return productList;
    }

    public void removeProduct(Long id) {
        Optional<Product> found = productRepository.findById(id);

        if (found.isPresent()) {
            productRepository.deleteById(id);
        } else throw RESOURCE_NOT_FOUND_EXCEPTION;
    }
}
