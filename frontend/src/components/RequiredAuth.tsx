import {useAuth} from "../context/AuthContext.tsx";
import {Navigate, Outlet} from "react-router-dom";

export function RequiredAuth({allowedRoles}: {allowedRoles: string[]}) {
    const { authData } = useAuth();

    if (!authData) return <Navigate to="/login" />;
    if (!allowedRoles.includes(authData.role)) return <Navigate to="/unauthorized" />;

    // Outlet -> renders the child components of the route that is protected by this RequiredAuth component
    return <Outlet />
}