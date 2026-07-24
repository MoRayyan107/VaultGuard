import { useNavigate } from "react-router-dom";
import styles from "../styles/AuthLayer.module.css";

function LandingPage() {
    const navigate = useNavigate();

    return (
        <div className={`${styles.AuthLayer} min-h-screen bg-[#F5F5F5] flex flex-col`}>
            <header className="sticky top-0 z-50 bg-[#FFFFFF] w-full px-8 py-6 flex justify-between items-center border-solid border-b-[3px] border-b-black">
                <span className="font-mono text-[25px] font-semibold text-[#1A1A1A]">
                    <a href="/">Vault<span className="text-[#1E3A5F]">Guard</span></a>
                </span>

                <div className={"flex items-center gap-6"}>
                    <a href="/features" className="text-[#1E3A5F] hover:text-[#16293F] text-[16px] font-medium mr-6 hover:underline transition-colors">
                        Features
                    </a>

                    <button
                        onClick={() => navigate("/login")}
                        className="px-4 py-2 text-sm font-medium text-[#1E3A5F] border border-[#1E3A5F]/30 rounded-md hover:bg-[#1E3A5F]/10 transition-colors"
                    >
                        Log in
                    </button>
                </div>
            </header>

            {/* Hero */}
            <main className="flex-1 flex flex-col items-center justify-center text-center px-6 max-w-3xl mx-auto">
                <div className="min-h-screen flex flex-col items-center justify-center text-center max-w-3xl">
                    <h1 className="font-mono text-5xl font-bold text-[#1A1A1A] tracking-tight mb-4">
                        Real-time fraud detection,<br />built for scale.
                    </h1>
                    <p className="text-[#6B6B6B] text-lg mb-10 max-w-xl">
                        VaultGuard scores every transaction in real time using event-driven
                        architecture — flagging risk before it settles, not after.
                    </p>
                    <button
                        onClick={() => document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' })}
                        className="px-8 py-3 bg-[#1E3A5F] text-white font-medium rounded-md hover:bg-[#16293F] transition-colors"
                    >
                        Learn More
                    </button>
                </div>

                {/* Three key features */}
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-8 mt-10 text-center max-w-3xl mx-auto w-full justify-items-center">
                    <div className="max-w-[240px] sm:text-left">
                        <div className="font-mono text-sm text-[#1E3A5F] mb-1">01 — Streaming</div>
                        <p className="text-sm text-[#6B6B6B]">
                            Kafka-backed event pipeline processes transactions as they happen, not in batch.
                        </p>
                    </div>
                    <div className="max-w-[240px] sm:text-left">
                        <div className="font-mono text-sm text-[#1E3A5F] mb-1">02 — Risk Scoring</div>
                        <p className="text-sm text-[#6B6B6B]">
                            Every transaction is scored 0–1 on risk, with high-confidence cases flagged instantly.
                        </p>
                    </div>
                    <div className="max-w-[240px] sm:text-left">
                        <div className="font-mono text-sm text-[#1E3A5F] mb-1">03 — Rate-Limited</div>
                        <p className="text-sm text-[#6B6B6B]">
                            Redis-backed limits protect the system under load, per-user and per-IP.
                        </p>
                    </div>
                </div>

                {/* Learn More features */}
                <div id="features" className="w-full max-w-3xl pt-24 mb-20">
                    <h2 className="font-mono text-3xl font-bold text-[#1A1A1A] tracking-tight mb-4 text-center">
                        Features
                    </h2>
                    <p className="text-[#6B6B6B] text-lg mb-12 max-w-xl mx-auto text-center">
                        VaultGuard is designed to be fast, reliable, and easy to integrate into your existing systems.
                    </p>

                    {/* FIXED: Balanced out the grid items layout spacing */}
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-12 gap-y-8 text-left max-w-2xl mx-auto">
                        <div>
                            <h3 className="font-mono text-xl font-semibold text-[#1A1A1A] mb-2">Event-driven Architecture</h3>
                            <p className="text-sm text-[#6B6B6B] leading-relaxed">
                                Built on Kafka, VaultGuard processes transactions in real time, ensuring immediate risk assessment.
                            </p>
                        </div>
                        <div>
                            <h3 className="font-mono text-xl font-semibold text-[#1A1A1A] mb-2">Real-time Risk Scoring</h3>
                            <p className="text-sm text-[#6B6B6B] leading-relaxed">
                                Each transaction is scored instantly, allowing for immediate action on high-risk transactions.
                            </p>
                        </div>
                        <div>
                            <h3 className="font-mono text-xl font-semibold text-[#1A1A1A] mb-2">Rate Limiting</h3>
                            <p className="text-sm text-[#6B6B6B] leading-relaxed">
                                Protect your system from overload with per-user and per-IP rate limiting, backed by Redis.
                            </p>
                        </div>
                        <div>
                            <h3 className="font-mono text-xl font-semibold text-[#1A1A1A] mb-2">Easy Integration</h3>
                            <p className="text-sm text-[#6B6B6B] leading-relaxed">
                                VaultGuard is designed to be easily integrated into your existing systems with minimal effort.
                            </p>
                        </div>
                    </div>
                </div>

            </main>

            <footer className="py-6 text-center text-xs text-[#6B6B6B]">
                VaultGuard — internal fraud detection system
            </footer>
        </div>
    );
}

export default LandingPage;