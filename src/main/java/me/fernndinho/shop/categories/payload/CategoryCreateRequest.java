package me.fernndinho.shop.categories.payload;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data @Builder(toBuilder = true)
@NoArgsConstructor @AllArgsConstructor
public class CategoryCreateRequest {
    private String name;
    private String slug;
    private String description;

    private String father;
}
