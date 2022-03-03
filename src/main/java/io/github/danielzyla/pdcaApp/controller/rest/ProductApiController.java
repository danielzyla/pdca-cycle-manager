package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.ProductReadDto;
import io.github.danielzyla.pdcaApp.dto.ProductWriteDto;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.repository.ProductRepository;
import io.github.danielzyla.pdcaApp.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
class ProductApiController {

    ProductService productService;
    ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<ProductReadDto> createNewProduct(@RequestBody ProductWriteDto productWriteDto) {
        ProductReadDto saved = productService.create(productWriteDto);
        return ResponseEntity.created(URI.create("/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<ProductReadDto> getAll() {
        return productService.getAll();
    }

    @GetMapping("/id")
    public ResponseEntity<Product> getById(@PathVariable("id") Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Void> updateProduct(@RequestBody ProductWriteDto productWriteDto) {
        try {
            productService.update(productWriteDto);
        } catch (MethodNotAllowedException e) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>((HttpStatus.NO_CONTENT));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeProduct(@RequestParam("id") Long id) {
        try {
            productService.removeProduct(id);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Data integrity violation")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void conflict() {
    }
}
