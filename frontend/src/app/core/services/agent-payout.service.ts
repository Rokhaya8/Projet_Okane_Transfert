import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TransferSearchDTO {
  id: number;
  codeRetrait: string;
  montantEnvoye: number;
  deviseSource: string;
  montantRecu: number;
  deviseCible: string;
  nomBeneficiaire: string;
  telephoneBeneficiaire: string;
  statut: string;
  dateEnvoi: string;
  dateExpiration: string;
}

export interface PayoutReceiptDTO {
  codeRetrait: string;
  montantPaye: number;
  devise: string;
  nomBeneficiaire: string;
  telephoneBeneficiaire: string;
  datePaiement: string;
  frais: number;
  nomAgentPaiement: string;
  agenceNom: string;
}

export interface TransferHistoryDTO {
  id: number;
  codeRetrait: string;
  montantEnvoye: number;
  deviseSource: string;
  montantRecu: number;
  deviseCible: string;
  frais: number;
  statut: string;
  dateEnvoi: string;
  datePaiement: string;
  nomBeneficiaire: string;
  telephoneBeneficiaire: string;
  paysBeneficiaire: string;
  nomAgentSaisie: string;
  nomAgentPaiement: string;
}

export interface CashBalanceDTO {
  solde: number;
  devise: string;
}

export interface OperationCaisseDTO {
  id: number;
  type: string;
  montant: number;
  soldeApres: number;
  date: string;
  referenceTransfertId: number;
  description: string;
}

export interface CashCloseResponseDTO {
  soldeTheorique: number;
  soldeReel: number;
  ecart: number;
  message: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

const API = 'http://localhost:8080/api/agent';

@Injectable({ providedIn: 'root' })
export class AgentPayoutService {
  constructor(private http: HttpClient) {}

  searchTransfer(code?: string, telephone?: string): Observable<TransferSearchDTO[]> {
    let params = new HttpParams();
    if (code) params = params.set('code', code);
    if (telephone) params = params.set('telephoneBeneficiaire', telephone);
    return this.http.get<TransferSearchDTO[]>(`${API}/payouts/search`, { params });
  }

  payoutTransfer(transferId: number, pieceIdentite: string): Observable<PayoutReceiptDTO> {
    return this.http.post<PayoutReceiptDTO>(
      `${API}/payouts/${transferId}/payout`,
      { pieceIdentiteBeneficiaire: pieceIdentite }
    );
  }

  getHistory(page: number, size: number, status?: string, startDate?: string, endDate?: string, search?: string): Observable<Page<TransferHistoryDTO>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    if (search) params = params.set('search', search);
    return this.http.get<Page<TransferHistoryDTO>>(`${API}/payouts/history`, { params });
  }

  getBalance(): Observable<CashBalanceDTO> {
    return this.http.get<CashBalanceDTO>(`${API}/cash/balance`);
  }

  getOperations(date?: string): Observable<OperationCaisseDTO[]> {
    let params = new HttpParams();
    if (date) params = params.set('date', date);
    return this.http.get<OperationCaisseDTO[]>(`${API}/cash/operations`, { params });
  }

  closeCash(soldeReel: number): Observable<CashCloseResponseDTO> {
    return this.http.post<CashCloseResponseDTO>(`${API}/cash/close`, { soldeReelSaisi: soldeReel });
  }
}
