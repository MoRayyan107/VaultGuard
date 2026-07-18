import {useState} from "react";
import api from "../../api/axios.ts";
import * as React from "react";
import { useNavigate } from 'react-router-dom';
import {useAuth} from "../../context/AuthContext.tsx";
import axios from "axios";
import AuthLayer from "../../components/AuthLayer.tsx";
import styles from '../../styles/AuthLayer.module.css';

function Login() {
    const navigate = useNavigate();
    const {login} = useAuth();
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [error, setError] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);

    const handleSubmit = async (event: React.SubmitEvent<HTMLFormElement>) => {
        // prevent default browser behavior for Single Page Applications (SPA)
        event.preventDefault();
        setLoading(true);

        try{
            // get the login response from the backend API
            const loginResponse = await api.post("/api/auth/login", {
                username, password
            });

            // set userJwt, role, and username in localStorage for future use
            // used deconstruct username to avoid confusion with the username state variable
            const {role, username: responseUsername} = loginResponse.data.user;

            // create authData Object
            const authData = {role, username: responseUsername}

            login(authData);

            // navigate to dashboard
            navigate("/dashboard");

        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response?.data?.message && error.response.status === 401) {
                    setError(error.response.data.message);
                } else {
                    setError("An error occurred. Please try again.");
                }
            } else {
                setError("An unexpected error occurred. Please try again.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <AuthLayer title={"Login"}>
            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <p className="ext-sm text-[#F43F5E] bg-[#F43F5E]/10 border border-[#F43F5E]/20 rounded px-3 py-2">
                        {error}
                    </p>
                )}

                <div>
                    <label htmlFor="username" className={`${styles.AuthLabel} block text-sm font-medium text-[#1A1A1A]`}>
                        Username
                    </label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        className={`${styles.AuthFields} mt-1.5 block w-full px-4 py-3 rounded-lg border border-[#E5E5E0] text-[#1A1A1A] bg-white text-sm transition-all duration-200 outline-none`}
                    />
                </div>

                <div>
                    <label htmlFor="password" className={`${styles.AuthLabel} block text-sm font-medium text-[#1A1A1A]`}>
                        Password
                    </label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        className={`${styles.AuthFields} mt-1.5 block w-full px-4 py-3 rounded-lg border border-[#E5E5E0] text-[#1A1A1A] bg-white text-sm transition-all duration-200 outline-none`}
                    />
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    className={`${styles.AuthButton} w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white hover:bg-[#1E3A5F]/80 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#1E3A5F]`}
                    style={{ backgroundColor: loading ? "#5e5e5e" : "#1E3A5F" }}
                >
                    {loading ? "Logging in..." : "Login"}
                </button>
                <a href="/register" className="text-sm font-medium text-[#1E3A5F] hover:text-[#1E3A5F]/80">
                    Don't have an account? Register
                </a>
            </form>
        </AuthLayer>
    );
}

export default Login;
