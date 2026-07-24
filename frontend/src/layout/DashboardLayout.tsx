import { Outlet } from 'react-router-dom';
import Header from "../components/Header.tsx";

export const DashboardLayout = () => {
    return (
        <div className="flex h-screen w-screen overflow-hidden bg-gray-50">
            <div className="flex flex-col flex-1 min-w-0">
                <Header />
                <main className="flex-1 overflow-y-auto p-4 md:p-6">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};