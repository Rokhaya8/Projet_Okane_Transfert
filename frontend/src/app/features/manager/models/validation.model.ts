export interface SensitiveOperation {
  id: number;
  operationType: string;
  status: string;
  transferId?: number;
  transferReference?: string;
  requestedById?: number;
  requestedByName?: string;
  amount?: number;
  rejectionReason?: string;
  createdAt: string;
  processedAt?: string;
}

export interface RejectValidationRequest {
  reason: string;
}
