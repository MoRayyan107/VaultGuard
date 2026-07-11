import {useNavigate} from "react-router-dom";

function LandingPage() {

    const navigate = useNavigate();

    return (
        <div className={"landing-page"}>
            <h1>Welcome to Vaultguard</h1>
            <p>
                VaultGuard is a real-time fraud detection engine. It ingests
                financial transactions, scores them for fraud risk using
                velocity and location anomaly checks, and flags suspicious
                activity for review by authorized analysts and admins.
            </p>
            <button onClick={() => navigate("/login")}>Login</button>
        </div>
    );
}

export default LandingPage;