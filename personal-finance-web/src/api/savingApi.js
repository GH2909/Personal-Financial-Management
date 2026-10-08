import { apiRequest } from "./apiClient";

export async function getSavings() {
    return apiRequest("/savings");
}

export async function createSaving(data) {
    return apiRequest("/savings", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export async function getSavingById(id) {
    return apiRequest(`/savings/${id}`);
}

export async function updateSaving(id, data) {
    return apiRequest(`/savings/${id}`, {
        method: "PUT",
        body: JSON.stringify(data),
    });
}

export async function deleteSaving(id) {
    return apiRequest(`/savings/${id}`, {
        method: "DELETE",
    });
}

export async function depositSaving(id, data) {
    return apiRequest(`/savings/${id}/deposit`, {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export async function withdrawSaving(id, data) {
    return apiRequest(`/savings/${id}/withdraw`, {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export async function getSavingRecords(id) {
    return apiRequest(`/savings/${id}/records`);
}