import { apiRequest } from "./apiClient";

export async function getCategories() {
    return apiRequest("/categories");
}

export async function createCategory(data) {
    return apiRequest("/categories", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export async function updateCategory(id, data) {
    return apiRequest(`/categories/${id}`, {
        method: "PUT",
        body: JSON.stringify(data),
    });
}

export async function deleteCategory(id) {
    return apiRequest(`/categories/${id}`, {
        method: "DELETE",
    });
}