package Personal.Finance.Manager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Personal.Finance.Manager.dto.request.CategoryRequest;
import Personal.Finance.Manager.dto.response.CategoryResponse;
import Personal.Finance.Manager.model.Category;
import Personal.Finance.Manager.model.CategoryType;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.CategoryRepository;
import Personal.Finance.Manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    // =========================
    // CREATE
    // =========================

    @Transactional
    public CategoryResponse createCategory(
            CategoryRequest request,
            User user) {

        validateUser(user);

        if (categoryRepository.existsByUserAndCategoryName(
                user,
                request.getCategoryName())) {

            throw new RuntimeException(
                    "Category name already exists");
        }

        Category category = new Category();

        category.setCategoryName(
                request.getCategoryName());

        category.setType(
                request.getType());

        category.setIcon(
                request.getIcon());

        category.setUser(user);

        Category savedCategory =
                categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    // =========================
    // GET ALL
    // =========================

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(
            User user) {

        validateUser(user);

        return categoryRepository
                .findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================
    // GET BY TYPE
    // =========================

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(
            CategoryType type,
            User user) {

        validateUser(user);

        return categoryRepository
                .findByUserAndType(user, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================
    // GET BY ID
    // =========================

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(
            Long categoryId,
            User user) {

        validateUser(user);

        Category category =
                getUserCategory(categoryId, user);

        return mapToResponse(category);
    }

    // =========================
    // UPDATE
    // =========================

    @Transactional
    public CategoryResponse updateCategory(
            Long categoryId,
            CategoryRequest request,
            User user) {

        validateUser(user);

        Category category =
                getUserCategory(categoryId, user);

        if (!category.getCategoryName()
                .equalsIgnoreCase(request.getCategoryName())
                && categoryRepository.existsByUserAndCategoryName(
                        user,
                        request.getCategoryName())) {

            throw new RuntimeException(
                    "Category name already exists");
        }

        category.setCategoryName(
                request.getCategoryName());

        category.setType(
                request.getType());

        category.setIcon(
                request.getIcon());

        Category updatedCategory =
                categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    // =========================
    // DELETE
    // =========================

    @Transactional
    public void deleteCategory(
            Long categoryId,
            User user) {

        validateUser(user);

        Category category =
                getUserCategory(categoryId, user);

        List<?> transactions =
                transactionRepository
                        .findByUserAndCategory_CategoryId(
                                user,
                                categoryId);

        if (!transactions.isEmpty()) {

            throw new RuntimeException(
                    "Cannot delete category because it has transactions");
        }

        categoryRepository.delete(category);
    }

    // =========================
    // VALIDATE USER
    // =========================

    private void validateUser(User user) {

        if (user == null) {
            throw new RuntimeException(
                    "User is required");
        }

        if (user.getUserId() == null) {
            throw new RuntimeException(
                    "Invalid user");
        }
    }

    // =========================
    // GET USER CATEGORY
    // =========================

    private Category getUserCategory(
            Long categoryId,
            User user) {

        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found"));

        if (category.getUser() == null
                || !category.getUser()
                    .getUserId()
                    .equals(user.getUserId())) {

            throw new RuntimeException(
                    "You do not have permission to access this category");
        }

        return category;
    }

    // =========================
    // MAP RESPONSE
    // =========================

    private CategoryResponse mapToResponse(
            Category category) {

        CategoryResponse response =
                new CategoryResponse();

        response.setCategoryId(
                category.getCategoryId());

        response.setCategoryName(
                category.getCategoryName());

        response.setType(
                category.getType());
        
        response.setIcon(
                category.getIcon());

        return response;
    }
}