package io.github.danielzyla.pdcaApp.controller;

import io.github.danielzyla.pdcaApp.dto.ProductReadDto;
import io.github.danielzyla.pdcaApp.dto.ProductWriteDto;
import io.github.danielzyla.pdcaApp.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/products")
class ProductViewController {
    private static final int PAGE_SIZE = 5;

    private final ProductService productService;

    ProductViewController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String showProductsPaginated(
            Model model,
            @RequestParam(value = "productNamePhrase") Optional<String> givenPhrase,
            @RequestParam(defaultValue = "0") String currentPage
    ) {
        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), PAGE_SIZE);
        Page<ProductReadDto> searchResult;

        if(givenPhrase.isPresent()) {
            searchResult = productService.searchByName(givenPhrase.get(), pageable);
            model.addAttribute("givenPhrase", givenPhrase.get());
        } else {
            searchResult = productService.getAllPaged(pageable);
        }
        model.addAttribute("foundProducts", searchResult);
        model.addAttribute("pageNumbers", searchResult.getTotalPages());
        return "products";
    }

    @PostMapping(params = "addProduct")
    public String initDeptForm(Model model) {
        ProductWriteDto toSave = new ProductWriteDto();
        model.addAttribute("product", toSave);
        model.addAttribute("info", "newForm");
        return "products";
    }

    @PostMapping
    public String createProduct(
            Model model,
            @Valid @ModelAttribute("product") ProductWriteDto toSave,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("info", "keepForm");
            return "products";
        }
        productService.create(toSave);
        return "products";
    }

    @RequestMapping("/editProduct/{id}")
    public String editProduct(
            Model model,
            @PathVariable("id") Long id,
            @Valid final ProductWriteDto update,
            BindingResult bindingResult
    ) {
        ProductReadDto current = productService.getById(id);
        model.addAttribute("current", current);

        if(bindingResult.hasErrors()) {
            return "editProduct";
        }
        productService.update(update);
        return "redirect:/products/editProduct/" + id;
    }

    @GetMapping("/{id}")
    public String removeProduct(Model model, @PathVariable("id") Long id) {
        try {
            productService.removeProduct(id);
        } catch(Exception e) {
            String message;
            if(e.getCause() != null) {
                message = "Produkt może być przypisany do projektu; " + e.getCause();
            } else message = "Nie znaleziono produktu dla podanego id";
            model.addAttribute("error", message);
            return "error";
        }
        return "redirect:";
    }

    @ExceptionHandler
    public String errorHandler(Model model, Exception e) {
        model.addAttribute("error", e);
        return "error";
    }
}
