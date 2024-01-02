package me.fernndinho.shop.categories.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String slug;
    private String description;

    @ManyToOne
    @JoinColumn(name = "father_id")
    private CategoryEntity father;

    @OneToMany(mappedBy = "father")
    private List<CategoryEntity> childs;

    private boolean hidden = false;

    public boolean hasFather() {
        return father != null;
    }

    public boolean hasChilds() {
        return childs != null && !childs.isEmpty();
    }
}

