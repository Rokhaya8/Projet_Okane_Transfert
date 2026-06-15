export interface Transfer {
  id: number;
  referenceCode: string;
  amountSent: number;
  amountReceived: number;
  fees: number;
  commissionAgency: number;
  commissionCentral: number;
  status: 'PENDING' | 'PAID' | 'CANCELLED';
  createdAt: any;
  paidAt: any;
  expiryDate: any;
  client?: {
    id: number;
    fullName: string;
    email: string;
    phone: string;
  };
  beneficiary?: {
    id: number;
    fullName: string;
    phone: string;
  };
}

export interface ClientProfile {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
}

export interface ClientDashboardInfo {
  totalMoneySent: number;
  totalTransfersCount: number;
  pendingTransfersCount: number;
  recentTransfers: Transfer[];
}
