export type TransferStatus = 'PENDING' | 'PAID' | 'CANCELLED' | 'EXPIRED';

export interface Operation {
  id: number;
  referenceCode: string;
  status: TransferStatus;
  createdAt: string;
  paidAt?: string;
  amountSent: number;
  amountReceived: number;
  fees?: number;
  agentId?: number;
  agentName?: string;
  senderId?: number;
  senderName?: string;
  senderPhone?: string;
  beneficiaryId?: number;
  beneficiaryName?: string;
  beneficiaryPhone?: string;
  sentCurrencyCode?: string;
  sentCurrencyName?: string;
  sentCurrencySymbol?: string;
  receivedCurrencyCode?: string;
  receivedCurrencyName?: string;
  receivedCurrencySymbol?: string;
}

export interface OperationDetail extends Operation {
  commissionAgency?: number;
  senderEmail?: string;
  beneficiaryCountry?: string;
  sourceAgencyId?: number;
  sourceAgencyName?: string;
  destinationAgencyId?: number;
  destinationAgencyName?: string;
}

export interface OperationFilters {
  status?: TransferStatus;
  from?: string;
  to?: string;
  agentId?: number;
}
