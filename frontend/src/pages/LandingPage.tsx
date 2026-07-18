import {useNavigate} from "react-router-dom";

function LandingPage() {

    const navigate = useNavigate();

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-4xl font-bold mb-8">Welcome to VaultGuard</h1>
            <div className="space-x-4">
                <button
                    onClick={() => navigate("/login")}
                    className="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
                >
                    Login
                </button>
                <button
                    onClick={() => navigate("/register")}
                    className="px-6 py-3 bg-green-600 text-white rounded-md hover:bg-green-700 transition"
                >
                    Register
                </button>
            </div>
        </div>
    );
}

export default LandingPage;