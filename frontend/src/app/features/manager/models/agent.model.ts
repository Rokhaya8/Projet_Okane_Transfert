export interface Agent {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  active: boolean;
  role?: string;
  agencyId?: number;
  createdAt?: string;
  lastLogin?: string;
  totalTransfers?: number;
}

export interface AgentDetail {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  active: boolean;
  createdAt?: string;
  lastLogin?: string;
  totalTransfers: number;
  paidTransfers: number;
  pendingTransfers: number;
  totalAmountProcessed: number;
}

export interface CreateAgentRequest {
  fullName: string;
  email: string;
  password: string;
  phone: string;
}

export interface UpdateAgentRequest {
  fullName: string;
  email: string;
  phone: string;
}
