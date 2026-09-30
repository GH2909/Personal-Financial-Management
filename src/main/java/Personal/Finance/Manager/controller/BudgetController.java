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
import org.springframework.web.bind.annotation.RestController;

import Personal.Finance.Manager.dto.request.BudgetCategoryRequest;
import Personal.Finance.Manager.dto.request.BudgetRequest;
import Personal.Finance.Manager.dto.response.BudgetCategoryResponse;
import Personal.Finance.Manager.dto.response.BudgetResponse;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    // ==============================
    // CREATE BUDGET
    // ==============================

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @RequestAttribute("user") User user,
            @Valid @RequestBody BudgetRequest request) {

        return ResponseEntity.ok(
                budgetService.createBudget(request, user));
    }

    // ==============================
    // GET ALL BUDGETS
    // ==============================

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                budgetService.getAllBudgets(user));
    }

    // ==============================
    // GET BUDGET BY ID
    // ==============================

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long budgetId,
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                budgetService.getBudgetById(budgetId, user));
    }

    // ==============================
    // UPDATE BUDGET
    // ==============================

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long budgetId,
            @RequestAttribute("user") User user,
            @Valid @RequestBody BudgetRequest request) {

        return ResponseEntity.ok(
                budgetService.updateBudget(
                        budgetId,
                        request,
                        user));
    }

    // ==============================
    // ADD BUDGET CATEGORY
    // ==============================

    @PostMapping("/{budgetId}/categories")
    public ResponseEntity<BudgetCategoryResponse> addBudgetCategory(
            @PathVariable Long budgetId,
            @RequestAttribute("user") User user,
            @Valid @RequestBody BudgetCategoryRequest request) {

        return ResponseEntity.ok(
                budgetService.addBudgetCategory(
                        budgetId,
                        request,
                        user));
    }

    // ==============================
    // UPDATE BUDGET CATEGORY
    // ==============================

    @PutMapping("/{budgetId}/categories/{categoryId}")
    public ResponseEntity<BudgetCategoryResponse> updateBudgetCategory(
            @PathVariable Long budgetId,
            @PathVariable Long categoryId,
            @RequestAttribute("user") User user,
            @Valid @RequestBody BudgetCategoryRequest request) {

        return ResponseEntity.ok(
                budgetService.updateBudgetCategory(
                        budgetId,
                        categoryId,
                        request,
                        user));
    }

    // ==============================
    // DELETE BUDGET CATEGORY
    // ==============================

    @DeleteMapping("/{budgetId}/categories/{categoryId}")
    public ResponseEntity<?> deleteBudgetCategory(
            @PathVariable Long budgetId,
            @PathVariable Long categoryId,
            @RequestAttribute("user") User user) {

        budgetService.deleteBudgetCategory(
                budgetId,
                categoryId,
                user);

        return ResponseEntity.ok(
                "Budget category deleted successfully");
    }
}