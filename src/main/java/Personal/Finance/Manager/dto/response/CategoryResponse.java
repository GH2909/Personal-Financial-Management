package Personal.Finance.Manager.dto.response;

import Personal.Finance.Manager.model.CategoryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {

    private Long categoryId;

    private String categoryName;

    private CategoryType type;

    private String icon;
}