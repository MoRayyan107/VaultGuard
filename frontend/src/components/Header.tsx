import {useNavigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext.tsx";

function Header() {
    const navigate = useNavigate();
    const {authData, logout} = useAuth();

    const handleLogout = () => {
        logout();
        navigate("/login");
    }

    return (
        <header>
            <span>Logged in as {authData?.username}</span>
            <button onClick={handleLogout}>Logout</button>
        </header>
    );
}

export default Header;