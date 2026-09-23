package Personal.Finance.Manager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Personal.Finance.Manager.dto.request.CategoryRequest;
import Personal.Finance.Manager.dto.response.CategoryResponse;
import Personal.Finance.Manager.model.CategoryType;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // =========================
    // CREATE
    // =========================

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestAttribute("user") User user,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(
                categoryService.createCategory(
                        request,
                        user));
    }

    // =========================
    // GET ALL
    // =========================

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                categoryService.getAllCategories(user));
    }

    // =========================
    // GET BY TYPE
    // =========================

    @GetMapping("/type")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByType(
            @RequestParam CategoryType type,
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                categoryService.getCategoriesByType(
                        type,
                        user));
    }

    // =========================
    // GET BY ID
    // =========================

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long categoryId,
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(
                        categoryId,
                        user));
    }

    // =========================
    // UPDATE
    // =========================

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @RequestAttribute("user") User user,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(
                categoryService.updateCategory(
                        categoryId,
                        request,
                        user));
    }

    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Long categoryId,
            @RequestAttribute("user") User user) {

        categoryService.deleteCategory(
                categoryId,
                user);

        return ResponseEntity.ok(
                "Category deleted successfully");
    }
}