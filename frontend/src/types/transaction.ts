export interface Transaction {
    transactionId: string;
    senderAccountNumber: string;
    recipientAccountNumber: string;
    transactionAmount: number;
    transactionType: "TRANSFER" | "WITHDRAWAL" | "DEPOSIT";
    transactionStatus: "PENDING" | "COMPLETED" | "FAILED" | "FLAGGED";
    transactionDate: string;
    riskScore: number | null;
    resolvedAt: string | null;
}