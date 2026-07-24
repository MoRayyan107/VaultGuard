import axios from 'axios'

// get the base URL from env
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
    withCredentials: true,
    xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-XSRF-TOKEN',
});

// responses from the backedn server
api.interceptors.response.use(
    (response) => response,
    (error) => {
        return Promise.reject(error);
    }
);

export default api;