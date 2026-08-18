import { useNavigate } from "react-router-dom";
import styles from "../styles/AuthLayer.module.css";

function LandingPage() {
    const navigate = useNavigate();
    return (
        <div
            className={`${styles.AuthLayer} min-h-screen flex flex-col`}
            style={{
                backgroundColor: '#121212',
                backgroundImage: 'linear-gradient(rgba(255, 255, 255, 0.18) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.18) 1px, transparent 1px)',
                backgroundSize: '20px 20px'
                }}
        >
            <header className="sticky top-0 z-50 bg-[#121212]/90 backdrop-blur-sm w-full px-8 py-6 flex justify-between items-center border-solid border-b-[3px] border-b-white">
                <span className="font-mono text-[25px] font-semibold text-white">
                    <a href="/">Vault<span className="text-[#4A90E2]">Guard</span></a>
                </span>

                <div className={"flex items-center gap-6"}>
                    <a href="/features" className="text-[#4A90E2] hover:text-[#6BA4E8] text-[16px] font-medium mr-6 hover:underline transition-colors">
                        Features
                    </a>
                    <button
                        onClick={() => navigate("/login")}
                        className="px-4 py-2 text-sm font-medium text-white border border-white/30 rounded-md hover:bg-white/10 transition-colors"
                    >
                        Log in
                    </button>
                </div>
            </header>

            {/* Hero */}
            <main className="flex-1 flex flex-col items-center justify-center text-center px-6 max-w-3xl mx-auto W-full">
                <div className="min-h-screen flex flex-col items-center justify-center text-center max-w-3xl">
                    <h1 className="font-mono text-5xl font-bold text-white tracking-tight mb-4">
                        Real-time fraud detection,<br />built for scale.
                    </h1>
                    <p className="text-gray-400 text-lg mb-10 max-w-xl">
                        VaultGuard scores every transaction in real time using event-driven
                        architecture — flagging risk before it settles, not after.
                    </p>
                    <button
                        onClick={() => document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' })}
                        className="px-8 py-3 bg-[#4A90E2] text-white font-medium rounded-md hover:bg-[#357ABD] transition-colors"
                    >
                        Learn More
                    </button>
                </div>

                {/* Three key features */}
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-8 mt-10 text-center max-w-3xl mx-auto w-full justify-items-center">
                    <div className="max-w-[240px] sm:text-left">
                        <div className="font-mono text-sm text-[#4A90E2] mb-1">01 — Streaming</div>
                        <p className="text-sm text-gray-400">
                            Kafka-backed event pipeline processes transactions as they happen, not in batch.
                        </p>
                    </div>
                    <div className="max-w-[240px] sm:text-left">
                        <div className="font-mono text-sm text-[#4A90E2] mb-1">02 — Risk Scoring</div>
                        <p className="text-sm text-gray-400">
                            Every transaction is scored 0–1 on risk, with high-confidence cases flagged instantly.
                        </p>
                    </div>
                    <div className="max-w-[240px] sm:text-left">
                        <div className="font-mono text-sm text-[#4A90E2] mb-1">03 — Rate-Limited</div>
                        <p className="text-sm text-gray-400">
                            Redis-backed limits protect the system under load, per-user and per-IP.
                        </p>
                    </div>
                </div>

                {/* Learn More features */}
                <div id="features" className="w-full max-w-3xl pt-24 mb-20">
                    <h2 className="font-mono text-3xl font-bold text-white tracking-tight mb-4 text-center">
                        Features
                    </h2>
                    <p className="text-gray-400 text-lg mb-12 max-w-xl mx-auto text-center">
                        VaultGuard is designed to be fast, reliable, and easy to integrate into your existing systems.
                    </p>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-12 gap-y-8 text-left max-w-2xl mx-auto">
                        <div>
                            <h3 className="font-mono text-xl font-semibold text-white mb-2">Event-driven Architecture</h3>
                            <p className="text-sm text-gray-400 leading-relaxed">
                                Built on Kafka, VaultGuard processes transactions in real time, ensuring immediate risk assessment.
                            </p>
                        </div>
                        <div>
                            <h3 className="font-mono text-xl font-semibold text-white mb-2">Real-time Risk Scoring</h3>
                            <p className="text-sm text-gray-400 leading-relaxed">
                                Each transaction is scored instantly, allowing for immediate action on high-risk transactions.
                            </p>
                        </div>
                        <div>
                            <h3 className="font-mono text-xl font-semibold text-white mb-2">Rate Limiting</h3>
                            <p className="text-sm text-gray-400 leading-relaxed">
                                Protect your system from overload with per-user and per-IP rate limiting, backed by Redis.
                            </p>
                        </div>
                        <div>
                            <h3 className="font-mono text-xl font-semibold text-white mb-2">Easy Integration</h3>
                            <p className="text-sm text-gray-400 leading-relaxed">
                                VaultGuard is designed to be easily integrated into your existing systems with minimal effort.
                            </p>
                        </div>
                    </div>
                </div>
            </main>

            <footer className="py-6 text-center text-xs text-gray-500">
                VaultGuard — internal fraud detection system
            </footer>
        </div>
    );
}

export default LandingPage;