import axios from 'axios'

// get the base URL from env
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
});

// for all send request add a bearer header
api.interceptors.request.use((config) => {
    const authData = localStorage.getItem("authData");
    if (authData) {
        const { token } = JSON.parse(authData);
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;