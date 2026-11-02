export type TransferStatus = 'PENDING' | 'PAID' | 'CANCELLED' | 'PROCESSING';

export interface TransferResponse {
  id: number;
  referenceCode: string;
  amountSent: number;
  amountReceived: number;
  fees: number;
  status: TransferStatus;
  createdAt: string;
  agentName: string;
  agencyName: string;
  beneficiaryName: string;
}

export interface TransferCorridorRequest {
  sourceCountryCode: string;
  destinationCountryCode: string;
  sourceCurrencyId: number;
  destinationCurrencyId: number;
  fixedFee: number;
  percentageFee: number;
  exchangeRate: number;
  active: boolean;
}

export interface TransferCorridorResponse {
  id: number;
  sourceCountryCode: string;
  destinationCountryCode: string;
  sourceCurrencyCode: string;
  destinationCurrencyCode: string;
  fixedFee: number;
  percentageFee: number;
  exchangeRate: number;
  active: boolean;
}

export interface CurrencyRequest {
  code: string;
  name: string;
  symbol: string;
}

export interface CurrencyResponse {
  id: number;
  code: string;
  name: string;
  symbol: string;
}

export interface AgencyRequest {
  name: string;
  city: string;
  address: string;
  phone: string;
  active: boolean;
}

export interface AgencyResponse {
  id: number;
  name: string;
  city: string;
  address: string;
  phone: string;
  active: boolean;
}