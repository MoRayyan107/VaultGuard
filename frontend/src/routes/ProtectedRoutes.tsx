import type {ReactNode} from "react";
import {Navigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext.tsx";
import Header from "../components/Header.tsx";

interface ProtectedRouteProps {
    allowedRoles: string[];
    children: ReactNode;
}

function ProtectedRoutes({allowedRoles, children}: ProtectedRouteProps) {
    const {authData} = useAuth();
    // get the authData from the AuthContext [stoed in LocalStorage BAD thing]
    console.log("ProtectedRoutes authData:", authData);
    if (!authData){
        return <Navigate to="/login" replace />;
    }

    // check if the user role is in the allowed roles
    if (!allowedRoles.includes(authData.role)) {
        return <Navigate to="/unauthorized" replace />;
    }

    // if the user is authenticated and has the correct role, render the children components
    return (
        <>
            <Header />
            {children}
        </>
    );
}

export default ProtectedRoutes;