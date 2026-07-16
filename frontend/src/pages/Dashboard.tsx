import {useEffect} from "react";
import {useTransactions} from "../context/TransactionContext.tsx";

function Dashboard() {
    const {transactions, transactionError, transactionLoading, fetchTransactions} =  useTransactions();
    const {flaggedTransaction, flaggedError, flaggedLoading, fetchTransactionFlagged} = useTransactions();

    useEffect(() => {
        fetchTransactions();
        fetchTransactionFlagged();
    }, [fetchTransactionFlagged, fetchTransactions]);

    return (
        <div>
            <div>
                <h1>Dashboard</h1>
                <button onClick={() => fetchTransactions(true)}>Refresh Transactions</button>
                {transactionError && <p className="error">{transactionError}</p>}
                {transactionLoading && <p>Loading Transactions...</p>}
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

            <div>
                <h1>Flagged Transactions</h1>
                {flaggedError && <p className="error">{flaggedError}</p>}
                {flaggedLoading && <p>Loading Flagged Transactions...</p>}
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
                    {flaggedTransaction.map((t) => (
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
        </div>
    );
}

export default Dashboard;