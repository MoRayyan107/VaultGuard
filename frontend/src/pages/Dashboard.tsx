import {useEffect, useState} from "react";
import type {Transaction} from "../types/transaction.ts";
import api from "../api/axios.ts";


function Dashboard() {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [loading,  setLoading] = useState<boolean>(true);
    const [error, setError] =  useState<string>('');

    useEffect(() => {
        const fetchedResponse = async () => {
            try {
                const response = await api.get("api/v1/fraudDetect/fetch/allTransactions");
                setTransactions(response.data);
            } catch (error: any) {
                if (error.response && error.response.data && error.response.data.message) {
                    setError(error.response.data.message);
                } else {
                    setError("An error occurred. Please try again.");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchedResponse();
    }, []);

    if (loading) return <p>Loading Transactions...</p>
    if (error) return <p className="error">{error}</p>

    return (
        <div>
            <h1>Dashboard</h1>
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