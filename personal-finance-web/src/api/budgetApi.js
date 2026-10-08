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