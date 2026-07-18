import type {ReactNode} from 'react';
import styles from "../styles/AuthLayer.module.css";

interface AuthLayerProps {
    title: string;
    children: ReactNode;
}

function AuthLayer({title, children}: AuthLayerProps) {
    return (
        <div className={`${styles.AuthLayer} min-h-screen flex items-center justify-center bg-[#F5F5F5] px-4`}>
            <div className="w-full max-w-sm">

                {/* Display logo*/}
                <div className="text-center mb-8">
                    <h1 className="font-mono text-2xl font-semibold text-[#1A1A1A] tracking-tight">
                        Vault<span className="text-[#1E3A5F]">Guard</span>
                    </h1>
                </div>

                {/* Display card with its title*/}
                <div className={`${styles.customCard} bg-white border border-[#E5E5E0] rounded-md p-8`}>
                    <h2 className="text-lg font-semibold text-[#1A1A1A] mb-6">{title}</h2>
                    {children}
                </div>
            </div>
        </div>
    );
}
export default AuthLayer;