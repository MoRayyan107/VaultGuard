import type {AuthData} from "../types/auth.ts";
import {createContext, useState} from "react";
import * as React from "react";

// Define the AuthContextType interface
interface AuthContextType {
    authData: AuthData | null;
    logout: () => Promise<void>;
    login: (data: AuthData) => void;
}

// create AuthContext variable
const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({children}: {children: React.ReactNode}) {
    // set the authData from local storage
    const [authData, setAuthData] = useState<AuthData | null>(
        () => {
            const rawData = localStorage.getItem("authData");
            return rawData ? JSON.parse(rawData) : null;
        });

    // login function
    const login = (data: AuthData) => {
        if (!data || !data.role || !data.username) {
            console.error("Invalid auth data provided to login function");
            return;
        }

        try{
            localStorage.setItem("authData", JSON.stringify(data));
            setAuthData(data);
        } catch (error) {
            throw new Error(`Failed to save auth data to localStorage: ${error}`);
        }
    }

    // logout function to clear the local storage and set authData to null
    const logout = async () => {
        try {
            localStorage.removeItem("authData");
            setAuthData(null);
        } catch (error) {
            throw new Error("Failed to remove auth data from localStorage");
        }
    };

    return (
        <AuthContext.Provider value={{authData, login, logout}}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = React.useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within an AuthProvider");
    return context;
}