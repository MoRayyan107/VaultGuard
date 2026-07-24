import {useNavigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext.tsx";
import api from "../api/axios.ts";

function Header() {
    const navigate = useNavigate();
    const {authData, logout} = useAuth();

    const handleLogout = async () => {
        try{
            await api.post("/api/auth/logout");

            await logout();
            navigate("/login");
        } catch (error) {
            console.error("Logout failed:", error);
        }
    }

    return (
        <header>
            <span>Logged in as {authData?.username}</span>
            <button onClick={handleLogout}>Logout</button>
        </header>
    );
}

export default Header;