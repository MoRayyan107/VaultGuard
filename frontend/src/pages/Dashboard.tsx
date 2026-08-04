import {useEffect, useState} from "react";
import {useTransactions} from "../context/TransactionContext.tsx";
import TransactionTable from "../components/TransactionTable.tsx";
import type {Transaction} from "../types/Transaction.ts";

function Dashboard() {
    const {transactions, transactionError, transactionLoading, fetchTransactions} =  useTransactions();

    type dashBoardFilterStatus = Transaction['transactionStatus'];
    type dashboardFilterType = Transaction['transactionType'];

    const [statusFilter, setStatusFilter] = useState<dashBoardFilterStatus | "ALL">("ALL");
    const [typeFilter, setTypeFilter] = useState<dashboardFilterType | "ALL">("ALL");

    useEffect(() => {
        fetchTransactions();
    }, [fetchTransactions]);

    return (
        <div>
            <button onClick={() => {fetchTransactions(true);}}>Refresh</button>

            <select
                value={typeFilter}
                onChange={(e) => setTypeFilter(e.target.value as dashboardFilterType | "ALL")}
            >
                <option value="ALL">All Types</option>
                <option value="DEPOSIT">Deposit</option>
                <option value="WITHDRAWAL">Withdrawal</option>
                <option value="TRANSFER">Transfer</option>
            </select>

            <TransactionTable title="All Transactions"
                            transaction={transactions}
                            error={transactionError}
                            loading={transactionLoading}
            />
        </div>
    );
}

export default Dashboard;