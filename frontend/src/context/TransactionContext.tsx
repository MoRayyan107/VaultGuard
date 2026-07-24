import type {Transaction} from "../types/transaction.ts";
import {createContext, type ReactNode, useState} from "react";
import api from "../api/axios.ts";
import * as React from "react";

interface TransactionContextType {
    transactions: Transaction[];
    transactionLoading: boolean;
    transactionError: string;
    fetchTransactions: (force?: boolean) => Promise<void>;

    flaggedTransaction: Transaction[];
    flaggedLoading: boolean;
    flaggedError: string;
    fetchTransactionFlagged: (force?: boolean) => Promise<void>;
}

const transactionContext = createContext<TransactionContextType | undefined>(undefined);

const REFRESH_RATE = 60000; // 60sec

export function TransactionProvider({children}: {children: ReactNode}) {
    const allTransactions = useFetch<Transaction>("/api/v1/fraudDetect/fetch/allTransactions");
    const flaggedTransactions = useFetch<Transaction>("/api/v1/fraudDetect/fetch/flaggedTransactions");

    return (
        <transactionContext.Provider value={{
            transactions: allTransactions.data,
            transactionLoading: allTransactions.loading,
            transactionError: allTransactions.error,
            fetchTransactions: allTransactions.fetchData,

            flaggedTransaction: flaggedTransactions.data,
            flaggedLoading: flaggedTransactions.loading,
            flaggedError: flaggedTransactions.error,
            fetchTransactionFlagged: flaggedTransactions.fetchData
        }}>
            {children}
        </transactionContext.Provider>
    );
}

function useFetch<T>(endpoint: string) {
    const [data, setData] =  useState<T[]>([]);
    const [error, setError] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(true);
    const [lastFetchTime, setLastFetchTime] = useState<number>(0);

    const fetchData = async (force = false) => {
        // check if the last fetch time is more than 30 seconds ago
        const checkIfStale = Date.now() - lastFetchTime > REFRESH_RATE;

        if (!force && !checkIfStale) return;

        // if its last etched is more that 30sec then re fetch
        try{
            const response = await api.get(endpoint);
            setData(response.data);
            setLastFetchTime(Date.now());
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

    return {data, loading, error, fetchData};
}

export function useTransactions() {
    const context = React.useContext(transactionContext);
    if (!context) throw new Error("useTransactions must be used within a TransactionProvider");
    return context;
}