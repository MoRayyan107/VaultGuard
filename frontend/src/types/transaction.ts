export interface Transaction {
    transactionId: string;
    senderAccountNumber: string;
    senderBankName: string;
    recipientAccountNumber: string | null;
    recipientBankName: string | null;
    transactionAmount: number;
    senderLocation: string;
    transactionType: "TRANSFER" | "WITHDRAWAL" | "DEPOSIT";
    transactionStatus: "PENDING" | "COMPLETED" | "FAILED" | "FLAGGED";
    transactionDate: string;
    riskScore: number | null;
    riskLevel: string;
    reason: string;
    createdAt: string;
}