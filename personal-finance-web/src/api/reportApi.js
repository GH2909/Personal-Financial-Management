import { apiRequest } from "./apiClient";

export async function getDashboardReport() {
    return apiRequest("/reports/dashboard");
}

export async function getMonthlyReport() {
    return apiRequest("/reports/monthly");
}

export async function getCategoryReport() {
    return apiRequest("/reports/categories");
}

export async function getSavingReport() {
    return apiRequest("/reports/savings");
}