import axios from "axios";

const BASE_URL = "http://localhost:8080/DigitalLibrary/api";

export const endpoints = {
    login: "/auth/login",
    register: "/auth/register",
    profile: "/secure/profile",
    changePassword: "/secure/change-password",
    logout: "/auth/logout",
    notifications: "/secure/notifications",
    readNotification: (notificationId) => `/secure/notifications/${notificationId}/read`,
    
    categories: "/categories",

    myBookmarks: "/secure/bookmarks/me",

    myBorrows: "/secure/borrows/me",

    addReview: "/secure/reviews",
    editReview: (reviewId) => `/secure/reviews/${reviewId}`,

    documents: "/documents",
    documentDetails: (documentId) => `/documents/${documentId}`,
    documentFiles: (documentId) => `/documents/${documentId}/files`,
    documentReviews: (documentId) => `/documents/${documentId}/reviews`,
    documentAccess: (documentId) => `/secure/documents/${documentId}/access`,
    documentContent: (documentId) => `/secure/documents/${documentId}/content`,

    borrowDocument: (documentId) => `/secure/documents/${documentId}/borrow`,
    returnDocument: (borrowId) => `/secure/borrows/${borrowId}/return`,
    compareDocument: (documentId) => `/documents/${documentId}/compare`,
    
    bookmark: (documentId) => `/secure/bookmarks/${documentId}`,

    payments: "/secure/payments",
    paymentByDocument: (documentId) => `/secure/payments/${documentId}`,
    

    librarianDocuments: "/secure/librarian/documents",
    librarianDocumentDetails: (documentId) => `/secure/librarian/documents/${documentId}`,
    librarianDocumentFiles: (documentId) => `/secure/librarian/documents/${documentId}/files`,
    librarianDeleteDocumentFiles: (documentId, fileId) => `/secure/librarian/documents/${documentId}/files/${fileId}`,
    librarianDocumentBorrowers: (documentId) => `/secure/librarian/documents/${documentId}/borrowers`,

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