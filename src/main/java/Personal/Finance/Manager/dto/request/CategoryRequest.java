package Personal.Finance.Manager.dto.request;

import Personal.Finance.Manager.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(
        max = 100,
        message = "Category name must not exceed 100 characters"
    )
    private String categoryName;

    @NotNull(message = "Category type is required")
    private CategoryType type;

    private String icon;
}