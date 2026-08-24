import {useEffect, useState, useMemo} from "react";
import {useTransactions} from "../context/TransactionContext.tsx";
import TransactionTable from "../components/TransactionTable.tsx";
import type {Transaction} from "../types/transaction";

function Dashboard() {
    const {transactions, transactionError, transactionLoading, fetchTransactions} =  useTransactions();

    type dashBoardFilterStatus = Transaction['transactionStatus'];
    type dashboardFilterType = Transaction['transactionType'];

    const [statusFilter, setStatusFilter] = useState<dashBoardFilterStatus | "ALL">("ALL");
    const [typeFilter, setTypeFilter] = useState<dashboardFilterType | "ALL">("ALL");

    useEffect(() => {
        fetchTransactions();
    }, [fetchTransactions]);

    const filteredTransactions = useMemo(() => {
        return transactions.filter(t => {
            if (typeFilter !== "ALL" && t.transactionType !== typeFilter) return false;
            if (statusFilter !== "ALL" && t.transactionStatus !== statusFilter) return false;
            return true;
        });
    }, [transactions, typeFilter, statusFilter]);

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

            <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value as dashBoardFilterStatus | "ALL")}
            >
                <option value="ALL">All Statuses</option>
                <option value="PENDING">Pending</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="FAILED">FAILED</option>
                <option value="FLAGGED">FLAGGED</option>
            </select>

            <TransactionTable title="All Transactions"
                              transaction={filteredTransactions}
                              error={transactionError}
                              loading={transactionLoading}
            />
        </div>
    );
}

export default Dashboard;