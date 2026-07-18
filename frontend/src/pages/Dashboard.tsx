import {useEffect} from "react";
import {useTransactions} from "../context/TransactionContext.tsx";
import TransactionTable from "../components/TransactionTable.tsx";

function Dashboard() {
    const {transactions, transactionError, transactionLoading, fetchTransactions} =  useTransactions();
    const {flaggedTransaction, flaggedError, flaggedLoading, fetchTransactionFlagged} = useTransactions();

    useEffect(() => {
        fetchTransactions();
        fetchTransactionFlagged();
    }, [fetchTransactionFlagged, fetchTransactions]);

    return (
        <div>
            <button onClick={() => {fetchTransactions(true); fetchTransactionFlagged(true);}}>Refresh</button>

            <TransactionTable title="All Transactions"
                            transaction={transactions}
                            error={transactionError}
                            loading={transactionLoading}
            />

            <TransactionTable title="Flagged Transactions"
                            transaction={flaggedTransaction}
                            error={flaggedError}
                            loading={flaggedLoading}
            />
        </div>
    );
}

export default Dashboard;