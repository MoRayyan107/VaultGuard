import {createBrowserRouter} from "react-router-dom";
import Login from '../pages/auth/Login.tsx'
import Dashboard  from "../pages/Dashboard.tsx";
import LandingPage from "../pages/LandingPage.tsx";
import ProtectedRoutes from "./ProtectedRoutes.tsx";
import Unauthorized from "../pages/Unauthorized.tsx";
import Register from "../pages/auth/Register.tsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <LandingPage />
    },
    {
        path: "/login",
        element: <Login />
    },
    {
        path: "/register",
        element: <Register />
    },
    {
        path: "/dashboard",
        element: 
            <ProtectedRoutes allowedRoles={["ROLE_ANALYST", "ROLE_MANAGER"]}>
                <Dashboard />
            </ProtectedRoutes>
    },
    {
        path: "/unauthorized",
        element: <Unauthorized />
    }
]);

export default router;