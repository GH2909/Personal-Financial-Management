import { apiRequest } from "./apiClient";

export async function getBudgets() {
    return apiRequest("/budgets");
}

export async function createBudget(data) {
    return apiRequest("/budgets", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export async function getBudgetById(id) {
    return apiRequest(`/budgets/${id}`);
}

export async function updateBudget(id, data) {
    return apiRequest(`/budgets/${id}`, {
        method: "PUT",
        body: JSON.stringify(data),
    });
}

export async function deleteBudget(id) {
    return apiRequest(`/budgets/${id}`, {
        method: "DELETE",
    });
}

export async function addBudgetCategory(budgetId, data) {
    return apiRequest(`/budgets/${budgetId}/categories`, {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export async function updateBudgetCategory(budgetId, categoryId, data) {
    return apiRequest(
        `/budgets/${budgetId}/categories/${categoryId}`,
        {
            method: "PUT",
            body: JSON.stringify(data),
        }
    );
}

export async function deleteBudgetCategory(budgetId, categoryId) {
    return apiRequest(
        `/budgets/${budgetId}/categories/${categoryId}`,
        {
            method: "DELETE",
        }
    );
}