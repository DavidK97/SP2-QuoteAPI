package app.dtos;

import app.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class CategoryDTO {

    private Integer id;

    private String title;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.title = category.getTitle();

    }

    public static List<CategoryDTO> toDTOList(List<Category> categories) {
        return categories.stream().map(CategoryDTO::new).toList();
    }
}