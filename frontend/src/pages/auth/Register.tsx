import {useNavigate} from "react-router-dom";
import {useState} from "react";
import api from "../../api/axios.ts";
import AuthLayer from "../../components/AuthLayer.tsx";
import styles from "../../styles/AuthLayer.module.css";

function Register() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [email, setEmail] = useState<string>("");

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        try {
            setLoading(true);
            const response = await api.post("/api/auth/register", {
                username, password, email
            });
            if (response.status !== 201) {
                throw new Error("Failed to register user");
            }
            navigate("/login");
        } catch (error: any) {
            setError(error.response?.data?.message || "An error occurred. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <AuthLayer title={"Register"}>
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

                <div>
                    <label htmlFor="email" className={`${styles.AuthLabel} block text-sm font-medium text-[#1A1A1A]`}>
                        Email
                    </label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
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
                    {loading ? "Registering..." : "Register"}
                </button>
                <a href="/login" className="text-sm font-medium text-[#1E3A5F] hover:text-[#1E3A5F]/80">
                    Already have an account? Login
                </a>
            </form>
        </AuthLayer>
    );
}

export default Register;