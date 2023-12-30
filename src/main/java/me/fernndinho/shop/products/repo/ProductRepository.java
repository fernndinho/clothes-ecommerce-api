package me.fernndinho.shop.products.repo;

import me.fernndinho.shop.categories.models.CategoryEntity;
import me.fernndinho.shop.products.models.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findBySlug(String slug);

    List<ProductEntity> findByCategoriesIn(List<CategoryEntity> category);
}
