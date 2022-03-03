package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.ProductReadDto;
import io.github.danielzyla.pdcaApp.dto.ProductWriteDto;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepositoryMock;

    @InjectMocks
    private ProductService productService;

    @Test
    void create_ShouldReturnSavedProductAsProductReadDto() throws NoSuchFieldException, IllegalAccessException {
        //given
        ProductWriteDto productDto = new ProductWriteDto();
        productDto.setProductName("product-1");
        productDto.setProductCode("123-ABC");
        productDto.setSerialNo("ABC:1234567890");
        Product product = new Product();
        Field id = Product.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(product, 1L);
        product.setProductName(productDto.getProductName());
        product.setProductCode(productDto.getProductCode());
        product.setSerialNo(productDto.getSerialNo());

        given(productRepositoryMock.save(argThat(product1 -> product1.getProductName().equals(product.getProductName())
                && product1.getProductCode().equals(product.getProductCode())
                && product1.getSerialNo().equals(product.getSerialNo())
        ))).willReturn(product);

        //when
        ProductReadDto productReadDto = productService.create(productDto);

        //then
        assertThat(productReadDto.getProductName(), is("product-1"));
        assertThat(productReadDto.getProductCode(), is("123-ABC"));
        assertThat(productReadDto.getSerialNo(), is("ABC:1234567890"));
        assertThat(productReadDto.getId(), is(1L));
    }

    @Test
    void getAll_ShouldReturnAllProductsAsReadDtoList() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Product> productList = getProductListStub();
        given(productRepositoryMock.findAll()).willReturn(productList);

        //when
        List<ProductReadDto> allProducts = productService.getAll();

        //then
        assertAll(
                () -> assertThat(allProducts, hasSize(105)),
                () -> assertThat(allProducts.get(0), isA(ProductReadDto.class)),
                () -> assertThat(allProducts.get(1).getId(), equalTo(2L)),
                () -> assertThat(allProducts.get(2).getProductName(), equalTo("product-3")),
                () -> assertThat(allProducts.get(2).getProductCode(), equalTo("product-code-3")),
                () -> assertThat(allProducts.get(2).getSerialNo(), equalTo("product-serial-3"))
        );
    }

    private List<Product> getProductListStub() throws NoSuchFieldException, IllegalAccessException {
        Field id = Product.class.getDeclaredField("id");
        id.setAccessible(true);
        List<Product> productListStub = new ArrayList<>();
        for (long i = 1; i <= 105; i++) {
            Product product = new Product("product-" + i, "product-code-" + i, "product-serial-" + i);
            id.set(product, i);
            productListStub.add(product);
        }
        return productListStub;
    }

    @Test
    void getAllPaged_ShouldReturnAllProductsAsReadDtoPagedList() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Product> productList = getProductListStub();
        given(productRepositoryMock.findAll()).willReturn(productList);

        //when
        Pageable pageable1 = PageRequest.of(0, 10);
        Page<ProductReadDto> page1 = productService.getAllPaged(pageable1);
        Pageable pageable2 = PageRequest.of(10, 10);
        Page<ProductReadDto> page11 = productService.getAllPaged(pageable2);

        //then
        assertThat(page1.getTotalPages(), is(11));
        assertThat(page1.getTotalElements(), is(105L));
        assertThat(page1.getContent().get(0), is(instanceOf(ProductReadDto.class)));
        assertThat(page1.getContent().get(0).getProductName(), equalTo("product-1"));
        assertThat(page1.getContent().get(9).getProductName(), equalTo("product-10"));
        assertThat(page1.getContent().size(), is(10));
        assertThat(page11.getContent().get(0).getProductName(), equalTo("product-101"));
        assertThat(page11.getContent().get(4).getProductName(), equalTo("product-105"));
        assertThat(page11.getContent().get(4).getId(), is(105L));
        assertThat(page11.getContent().size(), is(5));
        assertThat(page11.getNumberOfElements(), is(5));
        assertFalse(page11.getTotalElements() > 105L);
    }

    @Test
    void searchByName_ShouldReturnAllProductsMeetingSearchCriteriaAsReadDtoPagedList() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Product> productList = getProductListStub();
        given(productRepositoryMock.findByProductNameContains("1")).willReturn(
                productList.stream()
                        .filter(product -> product.getProductName().contains("1"))
                        .collect(Collectors.toList())
        );

        //when
        Pageable pageable1 = PageRequest.of(0, 10);
        Page<ProductReadDto> page1 = productService.searchByName("1", pageable1);
        Pageable pageable2 = PageRequest.of(2, 10);
        Page<ProductReadDto> page3 = productService.searchByName("1", pageable2);

        //then
        assertAll(
                () -> assertThat(page1.getTotalElements(), is(25L)),
                () -> assertThat(page1.getTotalPages(), is(3)),
                () -> assertThat(page1.getContent().get(4).getProductName(), equalTo("product-13")),
                () -> assertThat(page3.getContent().size(), is(5)),
                () -> assertThat(page3.getNumberOfElements(), is(5)),
                () -> assertFalse(page3.getTotalElements() > 25L)
        );
    }

    @Test
    void getById_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        given(productRepositoryMock.findById(1L)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> productService.getById(1L));
    }

    @Test
    void getById_ShouldReturnProductReadDtoWhenExistingIdIsUsed() throws NoSuchFieldException, IllegalAccessException {
        //given
        long id = 1;
        given(productRepositoryMock.findById(id)).willReturn(Optional.of(getProductListStub().get(0)));

        //when
        ProductReadDto readDto = productService.getById(id);

        //then
        assertAll(
                () -> assertNotNull(readDto),
                () -> assertThat(readDto, isA(ProductReadDto.class)),
                () -> assertEquals(readDto.getProductName(), "product-1"),
                () -> assertEquals(readDto.getProductCode(), "product-code-1"),
                () -> assertEquals(readDto.getSerialNo(), "product-serial-1")
        );
    }

    @Test
    void update_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        ProductWriteDto dto = new ProductWriteDto();
        dto.setId(1);
        given(productRepositoryMock.findById(1L)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> productService.update(dto));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 10, 20, 50, 100, 105})
    void update_ShouldCallRepositorySaveMethodWhenExistingIdIsUsed(long id) throws NoSuchFieldException, IllegalAccessException {
        //given
        ProductWriteDto dto = new ProductWriteDto();
        dto.setId(id);
        dto.setProductName("product-name-update");
        dto.setProductCode("product-code-update");
        dto.setSerialNo("product-serial-update");

        List<Product> productListStub = getProductListStub();
        Product toUpdate = productListStub.stream()
                .filter(product -> product.getId() == id)
                .collect(Collectors.toList())
                .get(0);
        given(productRepositoryMock.findById(id)).willReturn(Optional.ofNullable(toUpdate));

        //when
        productService.update(dto);

        //then
        then(productRepositoryMock).should().save(toUpdate);
        assertEquals(toUpdate.getProductName(), "product-name-update");
        assertEquals(toUpdate.getProductCode(), "product-code-update");
        assertEquals(toUpdate.getSerialNo(), "product-serial-update");
    }

    @Test
    void getProductList_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        List<Long> productIds = List.of(1L, 2L, 3L);
        given(productRepositoryMock.findById(1L)).willReturn(Optional.of(new Product()));
        given(productRepositoryMock.findById(2L)).willReturn(Optional.empty());
        given(productRepositoryMock.findById(2L)).willReturn(Optional.of(new Product()));

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductList(productIds));
    }

    @Test
    void getProductList_ShouldReturnProductListWhenExistingIdsAreUsed() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Product> productListStub = getProductListStub();
        List<Long> productIds = List.of(1L, 50L, 105L);
        for (long id : productIds) {
            given(productRepositoryMock.findById(id)).willReturn(Optional.ofNullable(productListStub.stream()
                    .filter(product -> product.getId() == id)
                    .collect(Collectors.toList())
                    .get(0)));
        }

        //when
        List<Product> productList = productService.getProductList(productIds);

        //then
        assertThat(productList, not(empty()));
        assertEquals(productList, List.of(productListStub.get(0), productListStub.get(49), productListStub.get(104)));
        assertEquals(productList.size(), 3);
        assertTrue(productList.stream().allMatch(product -> product.getProductName().startsWith("product-")));
        assertTrue(productList.stream().allMatch(product -> product.getProductCode().startsWith("product-code-")));
        assertTrue(productList.stream().allMatch(product -> product.getSerialNo().startsWith("product-serial-")));
    }

    @Test
    void removeProduct_ShouldReturnExceptionWhenNonExistingIdIsUsed() {
        //given
        long id = 1;
        given(productRepositoryMock.findById(id)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> productService.removeProduct(id));
    }

    @Test
    void removeProduct_ShouldCallDeleteMethodOnRepositoryWhenExistingIdIsUsed() {
        //given
        long id = 1;
        given(productRepositoryMock.findById(id)).willReturn(Optional.of(new Product()));

        //when
        productService.removeProduct(id);

        //then
        then(productRepositoryMock).should().deleteById(id);
    }
}