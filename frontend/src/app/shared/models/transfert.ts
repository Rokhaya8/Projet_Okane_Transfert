export interface AgentContext {
  agentId: number;
  agencyId: number;
  agencyName: string;
  agentName: string;
}

export interface SearchTransferResponse {
  id: number;
  referenceCode: string;
  amountSent: number;
  amountReceived: number;
  status: string;
  createdAt: string;
  paidAt: string | null;
  expiryDate: string | null;
  beneficiaryName: string | null;
  beneficiaryPhone: string | null;
  beneficiaryCountry: string | null;
  payable: boolean;
}

export interface TransferPayment {
  id: number;
  transferId: number;
  referenceCode: string;
  agentId: number;
  agentName: string;
  agencyId: number;
  agencyName: string;
  beneficiaryName: string;
  beneficiaryIdentityNumber: string;
  paidAmount: number;
  paidAt: string;
  receiptNumber: string;
}

export interface CashOperation {
  id: number;
  cashSessionId: number;
  operationType: 'OPENING' | 'TRANSFER_SENT' | 'TRANSFER_PAID' | 'ADJUSTMENT' | 'CLOSING' | string;
  amount: number;
  balanceBefore: number;
  balanceAfter: number;
  operationDate: string;
  reference: string | null;
  transferId: number | null;
}

export interface CashSessionResponse {
  id: number;
  agentId: number;
  agentName: string;
  agencyId: number;
  agencyName: string;
  openingBalance: number;
  currentBalance: number;
  closingBalance: number | null;
  countedAmount: number | null;
  discrepancyAmount: number | null;
  openedAt: string;
  closedAt: string | null;
  status: 'OPEN' | 'CLOSED' | string;
}

export interface PayTransferRequest {
  agentId: number;
  referenceCode: string;
  beneficiaryIdentityNumber: string;
}

export interface OpenCashSessionRequest {
  agentId: number;
  agencyId: number;
  openingBalance: number;
}

export interface CloseCashSessionRequest {
  countedAmount: number;
}

export interface CashAdjustmentRequest {
  amount: number;
  reference: string;
}
