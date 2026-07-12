import axios from 'axios'

// get the base URL from env
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
});

// for all send request add a bearer header
api.interceptors.request.use((config) => {
    const authData = localStorage.getItem("authData");
    if (authData) {
        const { token } = JSON.parse(authData);
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// responses from the backedn server
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401){
            localStorage.removeItem("authData");
            window.localStorage.href("/login");
        }
        return Promise.reject(error);
    }
);

export default api;