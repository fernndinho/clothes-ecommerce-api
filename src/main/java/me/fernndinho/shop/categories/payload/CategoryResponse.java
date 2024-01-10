package me.fernndinho.shop.categories.payload;

import lombok.*;
import me.fernndinho.shop.categories.models.CategoryEntity;

import java.util.List;
import java.util.stream.Collectors;

@Data @Builder
@AllArgsConstructor
public class CategoryResponse {
    private String name;
    private String slug;
    private String description;

    private String father;
    private List<String> childs;

    public CategoryResponse(CategoryEntity entity) {
        name = entity.getName();
        slug = entity.getSlug();
        description = entity.getDescription();

        if(entity.getFather() != null) {
            father = entity.getFather().getSlug();
        }

        if(entity.getChilds() != null && !entity.getChilds().isEmpty()) {
            childs = entity.getChilds().stream()
                    .map(CategoryEntity::getSlug)
                    .collect(Collectors.toList());
        }

    }
}
