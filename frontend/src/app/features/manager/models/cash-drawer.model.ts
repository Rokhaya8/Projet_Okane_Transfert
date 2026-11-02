export type CashDrawerStatus = 'OPEN' | 'CLOSED';

export interface CashDrawer {
  id: number;
  agentId: number;
  agentName: string;
  agencyId: number;
  openingBalance: number;
  currentBalance: number;
  closingBalance?: number;
  status: CashDrawerStatus;
  openedAt: string;
  closedAt?: string;
}
