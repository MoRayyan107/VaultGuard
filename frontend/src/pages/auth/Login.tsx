import {useState} from "react";
import api from "../../api/axios.ts";
import * as React from "react";
import { useNavigate } from 'react-router-dom';
import {useAuth} from "../../context/AuthContext.tsx";
import axios from "axios";

function Login() {
    const navigate = useNavigate();
    const {login} = useAuth();
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [error, setError] = useState<string>('');

    const handleSubmit = async (event: React.SubmitEvent<HTMLFormElement>) => {
        // prevent default browser behavior for Single Page Applications (SPA)
        event.preventDefault();

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
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div className="login-container">
                <h1>Login</h1>
                {error && <p className="error">{error}</p>}
                <div className="form-group">
                    <label htmlFor="username">Username:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Login</button>
                <button onClick={() => navigate("/register")}>
                    Don't have an account? Register
                </button>
            </div>
        </form>
    );
}

export default Login;
