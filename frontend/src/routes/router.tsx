import {createBrowserRouter} from "react-router-dom";
import Login from '../pages/auth/Login.tsx'
import Dashboard  from "../pages/Dashboard.tsx";
import LandingPage from "../pages/LandingPage.tsx";
import Unauthorized from "../pages/Unauthorized.tsx";
import Register from "../pages/auth/Register.tsx";
import NotFound from "../pages/NotFound.tsx";
import { RequiredAuth } from "../components/RequiredAuth.tsx";
import {DashboardLayout} from "../layout/DashboardLayout.tsx";

const router = createBrowserRouter([
    { path: '/', element: <LandingPage /> },
    { path: "/login", element: <Login /> },
    { path: "/register", element: <Register /> },
    {
        element: <RequiredAuth allowedRoles={["ROLE_MANAGER", "ROLE_ANALYST"]} />,
        children: [
            {
                element: <DashboardLayout />,
                children: [
                    { path: '/dashboard', element: <Dashboard /> }
                ]
            }
        ]
    },
    { path: "/unauthorized", element: <Unauthorized /> },
    { path: "*", element: <NotFound /> }
]);

export default router;