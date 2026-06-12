export type ReportPeriod = 'DAY' | 'WEEK' | 'MONTH';

export interface AgencyReport {
  agencyId: number;
  agencyName: string;
  period: ReportPeriod;
  periodStart: string;
  periodEnd: string;
  transactionCount: number;
  paidCount: number;
  pendingCount: number;
  cancelledCount: number;
  totalVolume: number;
  totalFees: number;
  totalCommissionAgency: number;
  totalRevenue: number;
}

export interface AgencyPerformance {
  agencyId: number;
  agencyName: string;
  activeAgents: number;
  openCashDrawers: number;
  pendingValidations: number;
  transactionsThisMonth: number;
  averageTransactionAmount: number;
  successRate: number;
  monthlyVolume: number;
  monthlyFees: number;
}

export interface AgentPerformance {
  agentId: number;
  agentName: string;
  totalOperations: number;
  paidOperations: number;
  pendingOperations: number;
  totalAmount: number;
}
