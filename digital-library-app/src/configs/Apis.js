import axios from "axios";

const BASE_URL = "http://localhost:8080/DigitalLibrary/api";

export const endpoints = {
    login: "/auth/login",
    register: "/auth/register",
    profile: "/secure/profile",
    changePassword: "/secure/change-password",
    logout: "/auth/logout",
    
    categories: "/categories",

    documents: "/documents",
    documentDetails: (documentId) => `/documents/${documentId}`,
    documentFiles: (documentId) => `/documents/${documentId}/files`,
    documentReviews: (documentId) => `/documents/${documentId}/reviews`,

    librarianDocuments: "/secure/librarian/documents",
    librarianDocumentDetails: (documentId) => `/secure/librarian/documents/${documentId}`,

};

const Api = axios.create({
    baseURL: BASE_URL,
    withCredentials: true
});

export const authApi = () => {
    return axios.create({
        baseURL: BASE_URL,
        withCredentials: true
    });
};

export default Api;