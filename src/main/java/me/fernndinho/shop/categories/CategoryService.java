package me.fernndinho.shop.categories;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import me.fernndinho.shop.categories.models.CategoryEntity;
import me.fernndinho.shop.categories.repo.CategoryRepository;
import me.fernndinho.shop.categories.payload.CategoryResponse;
import me.fernndinho.shop.categories.payload.CategoryCreateRequest;
import me.fernndinho.shop.products.models.ProductEntity;
import me.fernndinho.shop.products.repo.ProductRepository;
import me.fernndinho.shop.shared.error.exceptions.BadRequestException;
import me.fernndinho.shop.shared.error.exceptions.ConflictException;
import me.fernndinho.shop.shared.error.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {
    private CategoryRepository categoryRepo;
    private ProductRepository productRepo;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepo.findAll().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        return categoryRepo.findBySlug(slug)
                .map(CategoryResponse::new)
                .orElseThrow(() -> new NotFoundException("category not found"));
    }

    public CategoryResponse createCategory(CategoryCreateRequest categoryPayload) {
        if(categoryRepo.existsBySlug(categoryPayload.getSlug()))
            throw new ConflictException("category provided already exist");

        CategoryEntity entity = new CategoryEntity();
        entity.setName(categoryPayload.getName());
        entity.setSlug(categoryPayload.getSlug());
        entity.setDescription(categoryPayload.getDescription());

        CategoryEntity categoryCreated = categoryRepo.save(entity);

        if(categoryPayload.getFather() != null) {
            CategoryEntity father = categoryRepo.findBySlug(categoryPayload.getFather())
                    .orElseThrow(() -> new BadRequestException("father provided does not exist"));

            if(father.hasChilds()) {
                father.getChilds().add(entity);
            } else {
                father.setChilds(Lists.newArrayList(entity));
            }

            categoryRepo.save(father);
            categoryCreated.setFather(father);
        }

        CategoryEntity finalCategory = categoryRepo.save(categoryCreated);

        return new CategoryResponse(finalCategory);
    }

    public CategoryResponse updateCategory(String id, CategoryCreateRequest categoryResponse) { //TODO: implement this
        throw new UnsupportedOperationException();
    }

    public void deleteBySlug(String slug, boolean removeChilds) {
        CategoryEntity category = categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("category can not be deleted if does not exist"));

        if(category.hasFather()) {
            category.getFather().getChilds().remove(category);

            categoryRepo.save(category.getFather());
        }

        if(category.hasChilds()) {
            if(removeChilds) {
                categoryRepo.deleteAll(category.getChilds());
            } else {
                category.getChilds().forEach(ce -> ce.setFather(null));
                categoryRepo.saveAll(category.getChilds());
            }
        }

        List<ProductEntity> products = productRepo.findByCategoriesIn(Lists.newArrayList(category));

        if(products.isEmpty()) return;

        products.forEach(productEntity -> {
            productEntity.getCategories().remove(category);
        });
        
        productRepo.saveAll(products);
        
        categoryRepo.delete(category);
    }
}