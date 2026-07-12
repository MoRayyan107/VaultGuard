import {useEffect} from "react";
import {useTransactions} from "../context/TransactionContext.tsx";

function Dashboard() {
    const {transactions, loading, error, fetchTransactions} =  useTransactions();

    useEffect(() => {
        fetchTransactions();
    }, []);

    if (loading) return <p>Loading Transactions...</p>

    return (
        <div>
            <h1>Dashboard</h1>
            <button onClick={() => fetchTransactions(true)}>Refresh Transactions</button>
            {error && <p className="error">{error}</p>}
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Sender</th>
                        <th>Recipient</th>
                        <th>Amount</th>
                        <th>Type</th>
                        <th>Status</th>
                        <th>Risk Score</th>
                    </tr>
                </thead>
                <tbody>
                {transactions.map((t) => (
                    <tr key={t.transactionId}>
                        <td>{t.transactionId}</td>
                        <td>{t.senderAccountNumber}</td>
                        <td>{t.recipientAccountNumber}</td>
                        <td>{t.transactionAmount}</td>
                        <td>{t.transactionType}</td>
                        <td>{t.transactionStatus}</td>
                        <td>{t.riskScore ?? "N/A"}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default Dashboard;