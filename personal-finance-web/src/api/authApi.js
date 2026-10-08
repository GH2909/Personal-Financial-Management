import { apiRequest } from "./apiClient";

export const authApi = {
    login(data) {
        return apiRequest("/auth/login", {
            method: "POST",
            body: JSON.stringify(data),
        });
    },

    register(data) {
        return apiRequest("/auth/register", {
            method: "POST",
            body: JSON.stringify(data),
        });
    },
};