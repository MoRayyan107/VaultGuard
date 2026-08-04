import type {Transaction} from "../types/transaction.ts";

interface TransactionTableProps {
    title: string;
    transaction: Transaction[];
    error: string;
    loading: boolean;
    emptyMsg?: string;
}

function TransactionTable({title, transaction, error, loading, emptyMsg}: TransactionTableProps) {
    return (
        <div>
            <h1>{title}</h1>
            {loading && <p>Loading...</p>}
            {!loading && transaction.length === 0 && <p>{emptyMsg || "No transactions found."}</p>}
            {!loading && transaction.length > 0 && (
                <table>
                    {error && <p style={{color: 'red'}}>{error}</p>}
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Sender</th>
                            <th>Recipient</th>
                            <th>Sender Location</th>
                            <th>Amount</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Risk Score</th>
                        </tr>
                    </thead>
                    <tbody>
                        {transaction.map((t) => (
                            <tr key={t.transactionId}>
                                <td>{t.transactionId}</td>
                                <td>{t.senderAccountNumber}</td>
                                <td>{t.recipientAccountNumber}</td>
                                <td>{t.senderLocation}</td>
                                <td>{t.transactionAmount}</td>
                                <td>{t.transactionType}</td>
                                <td style={{ color: t.transactionStatus === 'FLAGGED' ? 'indianred' : 'darkseagreen' }}>
                                    {t.transactionStatus}
                                </td>
                                <td style={{ color: t.riskScore && t.riskScore >= 0.7 ? 'indianred' : 'darkseagreen' }}>
                                    {t.riskScore ?? "N/A"}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default TransactionTable;