package me.fernndinho.shop.categories;

import com.google.common.collect.Lists;
import me.fernndinho.shop.categories.models.CategoryEntity;
import me.fernndinho.shop.categories.payload.CategoryCreateRequest;
import me.fernndinho.shop.categories.payload.CategoryResponse;
import me.fernndinho.shop.categories.repo.CategoryRepository;
import me.fernndinho.shop.products.repo.ProductRepository;
import me.fernndinho.shop.shared.error.exceptions.BadRequestException;
import me.fernndinho.shop.shared.error.exceptions.ConflictException;
import me.fernndinho.shop.shared.error.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {
    private CategoryRepository categoryRepo;
    private ProductRepository productRepo;
    private CategoryService service;

    private CategoryEntity categoryEntity1;
    private CategoryEntity categoryEntity2;
    private CategoryEntity categoryEntity3;

    private CategoryResponse categoryResponse1;
    private CategoryResponse categoryResponse2;
    private CategoryResponse categoryResponse3;

    @BeforeEach
    public void beforeEach() {
        this.categoryRepo = mock(CategoryRepository.class);
        this.productRepo = mock(ProductRepository.class);
        this.service = new CategoryService(categoryRepo, productRepo);

        this.categoryEntity1 = CategoryEntity.builder()
                .name("Shoes").slug("shoes").description("a description about Shoes!")
                .build();
        this.categoryEntity2 = CategoryEntity.builder()
                .name("Hoodies").slug("hoodies").description("a description about Hoodies!")
                .build();
        this.categoryEntity3 = CategoryEntity.builder()
                .name("Sport shoes").slug("sport-shoes").description("desc for sport shoes")
                .build();


        this.categoryResponse1 = CategoryResponse.builder()
                .name("Shoes").slug("shoes").description("a description about Shoes!")
                .build();
        this.categoryResponse2 = CategoryResponse.builder()
                .name("Hoodies").slug("hoodies").description("a description about Hoodies!")
                .build();
        this.categoryResponse3 = CategoryResponse.builder()
                .name("Sport shoes").slug("sport-shoes").description("desc for sport shoes")
                .build();
    }

    @Test
    public void shouldSuccessOnFindAllCategories() {
        List<CategoryResponse> expected = Lists.newArrayList(categoryResponse1, categoryResponse2, categoryResponse3);

        when(categoryRepo.findAll())
                .thenReturn(Lists.newArrayList(categoryEntity1, categoryEntity2, categoryEntity3));

        List<CategoryResponse> result = service.getAllCategories();

        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertIterableEquals(expected, result);
    }

    @Test
    public void shouldReturnAnEmptyListIfCategoriesIsEmpty() {
        when(categoryRepo.findAll())
                .thenReturn(Lists.newArrayList());

        List<CategoryResponse> result = service.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldSuccessOnFindASpecificCategory() {
        CategoryResponse expected = categoryResponse1;

        when(categoryRepo.findBySlug("shoes"))
                .thenReturn(Optional.of(categoryEntity1));

        CategoryResponse result = service.getCategoryBySlug("shoes");

        assertEquals(expected, result);
    }

    @Test
    public void shouldFailOnTryFindANonExistentCategory() {
        when(categoryRepo.findBySlug("shoes"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            service.getCategoryBySlug("shoes");
        });
    }

    @Test
    public void shouldSuccessOnCreateCategory() {
        CategoryResponse expected = categoryResponse1;

        when(categoryRepo.existsBySlug("shoes"))
                .thenReturn(false);
        when(categoryRepo.save(any(CategoryEntity.class)))
                .thenReturn(categoryEntity1);

        CategoryResponse result = service.createCategory(
                CategoryCreateRequest.builder()
                        .name("Shoes")
                        .slug("shoes")
                        .description("a description about Shoes!")
                        .build()
        );

        assertEquals(expected, result);
    }

    @Test
    public void shouldFailOnTryCreateAExistentCategory() {
        when(categoryRepo.existsBySlug("shoes"))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            service.createCategory(
                    CategoryCreateRequest.builder()
                            .name("Shoes")
                            .slug("shoes")
                            .description("a description about Shoes!")
                            .build()
            );
        });
    }

    @Test
    public void shouldSuccessOnCreateANewCategoryWithFather() {
        CategoryResponse expected = CategoryResponse.builder()
                .name("Sport shoes")
                .slug("sport-shoes")
                .description("desc for sport shoes")
                .father("shoes")
                .build();

        when(categoryRepo.findBySlug("shoes"))
                .thenReturn(Optional.of(categoryEntity1));

        when(categoryRepo.save(any(CategoryEntity.class)))
                .thenReturn(
                        categoryEntity3,
                        categoryEntity1.toBuilder()
                                .childs(Lists.newArrayList(categoryEntity3))
                                .build(),
                        categoryEntity3.toBuilder()
                                .father(categoryEntity1)
                                .build()
                );

        CategoryResponse result = service.createCategory(
                CategoryCreateRequest.builder()
                        .name("Sport shoes")
                        .slug("sport-shoes")
                        .description("desc for sport shoes")
                        .father("shoes")
                        .build()
        );

        assertEquals(expected, result);
    }

    @Test
    public void shouldFailOnTryCreateACategoryWithNonExistentFather() {
        when(categoryRepo.save(any(CategoryEntity.class)))
                .thenReturn(categoryEntity3);

        when(categoryRepo.findBySlug("shoes"))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            service.createCategory(
                    CategoryCreateRequest.builder()
                            .name("Sport shoes")
                            .slug("sport-shoes")
                            .description("desc for sport shoes")
                            .father("shoes")
                            .build()
            );
        });
    }

    @Test
    public void shouldFailOnDeleteANonExistentCategory() {
        when(categoryRepo.existsBySlug("shoes"))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            service.deleteBySlug("shoes", false);
        });
    }
}

/*
 when(categoryRepo.save(any(CategoryEntity.class)))
                .thenReturn(
                        CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").build(),

                        CategoryEntity.builder().name("Shoes").slug("shoes").description("a description about Shoes!").childs(
                                Lists.newArrayList(CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").build())
                        ).build(),

                        CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").father(
                                CategoryEntity.builder().name("Shoes").slug("shoes").description("a description about Shoes!").childs(
                                        Lists.newArrayList(
                                                CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").build()
                                        )
                                ).build()
                        ).build()
                );
 */
/*@Test
    public void shouldSuccessOnCreateANewCategoryWithFather() {
        CategoryResponse expected = CategoryResponse.builder()
                .name("Sport shoes")
                .slug("sport-shoes")
                .description("desc for sport shoes")
                .father("shoes")
                .build();

        when(categoryRepo.existsBySlug("sport-shoes"))
                .thenReturn(false);
        when(categoryRepo.save(CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").build()))
                .thenReturn(CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").build());

        when(categoryRepo.findBySlug("shoes"))
                .thenReturn(Optional.of(categoryEntity1));
        when(categoryRepo.save(
                CategoryEntity.builder().name("Shoes").slug("shoes").description("a description about Shoes!").childs(
                        Lists.newArrayList(CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").build())
                ).build()
        ))
                .thenReturn(CategoryEntity.builder().name("Shoes").slug("shoes").description("a description about Shoes!").childs(
                        Lists.newArrayList(CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").build())
                ).build());

        when(categoryRepo.save(CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").father(categoryEntity1).build()))
                .thenReturn(CategoryEntity.builder().name("Sport shoes").slug("sport-shoes").description("desc for sport shoes").father(categoryEntity1).build());

        CategoryResponse result = service.createCategory(
                CategoryCreateRequest.builder()
                        .name("Sport shoes")
                        .slug("sport-shoes")
                        .description("desc for sport shoes")
                        .father("shoes")
                        .build()
        );

        assertEquals(expected, result);
    }*/