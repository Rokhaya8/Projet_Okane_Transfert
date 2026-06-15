
export interface AuditLog {
  id: number;
  username: string;   
  action: string;
  details?: string;
  ipAddress: string;
  timestamp: string;  
}