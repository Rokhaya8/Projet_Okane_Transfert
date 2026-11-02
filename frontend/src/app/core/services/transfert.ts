import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, of, throwError, timeout } from 'rxjs';
import {
  AgentContext,
  CashAdjustmentRequest,
  CashOperation,
  CashSessionResponse,
  CloseCashSessionRequest,
  OpenCashSessionRequest,
  PayTransferRequest,
  SearchTransferResponse,
  TransferPayment,
} from '../../shared/models/transfert';

const API_BASE = '/api';
const REQUEST_TIMEOUT_MS = 10000;
const TEST_AGENT_ID = 100;
const TEST_AGENCY_ID = 100;
const TEST_AGENT_NAME = 'Agent Test Caisse';
const TEST_AGENCY_NAME = 'Agence Test Casablanca';

@Injectable({
  providedIn: 'root',
})
export class Transfert {
  private fallbackSession: CashSessionResponse | null = null;
  private fallbackTransfers: SearchTransferResponse[] = [
    {
      id: 1,
      referenceCode: 'TESTPAY1',
      amountSent: 1000,
      amountReceived: 65000,
      status: 'EN_ATTENTE',
      createdAt: new Date(Date.now() - 86400000).toISOString(),
      paidAt: null,
      expiryDate: new Date(Date.now() + 6 * 86400000).toISOString(),
      beneficiaryName: 'Moussa Diallo',
      beneficiaryPhone: '+221771112233',
      beneficiaryCountry: 'Senegal',
      payable: true,
    },
    {
      id: 2,
      referenceCode: 'TESTPAID',
      amountSent: 500,
      amountReceived: 32500,
      status: 'PAYE',
      createdAt: new Date(Date.now() - 2 * 86400000).toISOString(),
      paidAt: new Date(Date.now() - 3600000).toISOString(),
      expiryDate: new Date(Date.now() + 5 * 86400000).toISOString(),
      beneficiaryName: 'Fatou Sow',
      beneficiaryPhone: '+221776667788',
      beneficiaryCountry: 'Senegal',
      payable: false,
    },
    {
      id: 3,
      referenceCode: 'TESTPAY2',
      amountSent: 2500,
      amountReceived: 162500,
      status: 'EN_ATTENTE',
      createdAt: new Date(Date.now() - 3 * 3600000).toISOString(),
      paidAt: null,
      expiryDate: new Date(Date.now() + 7 * 86400000).toISOString(),
      beneficiaryName: 'Aminata Diop',
      beneficiaryPhone: '+221770001122',
      beneficiaryCountry: 'Senegal',
      payable: true,
    },
  ];
  private fallbackPayments: TransferPayment[] = [
    {
      id: 1,
      transferId: 2,
      referenceCode: 'TESTPAID',
      agentId: TEST_AGENT_ID,
      agentName: TEST_AGENT_NAME,
      agencyId: TEST_AGENCY_ID,
      agencyName: TEST_AGENCY_NAME,
      beneficiaryName: 'Fatou Sow',
      beneficiaryIdentityNumber: 'SN998877',
      paidAmount: 32500,
      paidAt: new Date(Date.now() - 3600000).toISOString(),
      receiptNumber: 'RCT-DEMO-001',
    },
  ];
  private fallbackOperations: CashOperation[] = [
    {
      id: 1,
      cashSessionId: 1,
      operationType: 'OPENING',
      amount: 10000,
      balanceBefore: 0,
      balanceAfter: 10000,
      operationDate: new Date(Date.now() - 2 * 3600000).toISOString(),
      reference: 'OPEN-1',
      transferId: null,
    },
    {
      id: 2,
      cashSessionId: 1,
      operationType: 'ADJUSTMENT',
      amount: 40000,
      balanceBefore: 10000,
      balanceAfter: 50000,
      operationDate: new Date(Date.now() - 90 * 60000).toISOString(),
      reference: 'Alimentation caisse',
      transferId: null,
    },
    {
      id: 3,
      cashSessionId: 1,
      operationType: 'TRANSFER_PAID',
      amount: 32500,
      balanceBefore: 50000,
      balanceAfter: 17500,
      operationDate: new Date(Date.now() - 3600000).toISOString(),
      reference: 'TESTPAID',
      transferId: 2,
    },
  ];

  constructor(private http: HttpClient) {}

  getAgentContext(): AgentContext {
    return {
      agentId: TEST_AGENT_ID,
      agencyId: TEST_AGENCY_ID,
      agencyName: TEST_AGENCY_NAME,
      agentName: TEST_AGENT_NAME,
    };
  }

  searchByCode(referenceCode: string): Observable<SearchTransferResponse> {
    return this.http.get<SearchTransferResponse>(
      `${API_BASE}/agent/payouts/search/code/${encodeURIComponent(referenceCode)}`
    ).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => {
        const transfer = this.fallbackTransfers.find((item) => item.referenceCode.toLowerCase() === referenceCode.toLowerCase());
        return transfer ? of(transfer) : throwError(() => new Error('Transfert introuvable'));
      }),
    );
  }

  searchByPhone(phone: string): Observable<SearchTransferResponse[]> {
    return this.http.get<SearchTransferResponse[]>(
      `${API_BASE}/agent/payouts/search/phone/${encodeURIComponent(phone)}`
    ).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => {
        const compactPhone = phone.replace(/\s/g, '');
        const transfers = this.fallbackTransfers.filter((item) => item.beneficiaryPhone?.replace(/\s/g, '') === compactPhone);
        return transfers.length ? of(transfers) : throwError(() => new Error('Transfert introuvable'));
      }),
    );
  }

  payTransfer(request: PayTransferRequest): Observable<TransferPayment> {
    return this.http.post<TransferPayment>(`${API_BASE}/agent/payouts/pay`, request).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => this.payFallbackTransfer(request)),
    );
  }

  getPaymentHistory(agentId: number): Observable<TransferPayment[]> {
    const params = new HttpParams().set('agentId', `${agentId}`);
    return this.http.get<TransferPayment[]>(`${API_BASE}/agent/payouts/history`, { params }).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => of(this.fallbackPayments.filter((payment) => payment.agentId === agentId))),
    );
  }

  getAgentOperations(agentId: number): Observable<CashOperation[]> {
    const params = new HttpParams().set('agentId', `${agentId}`);
    return this.http.get<CashOperation[]>(`${API_BASE}/agent/cash-sessions/operations`, { params }).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => of(this.fallbackOperations)),
    );
  }

  getCurrentCashSession(agentId: number): Observable<CashSessionResponse> {
    const params = new HttpParams().set('agentId', `${agentId}`);
    return this.http.get<CashSessionResponse>(`${API_BASE}/agent/cash-sessions/current`, { params }).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => this.fallbackSession ? of(this.fallbackSession) : throwError(() => new Error('Aucune caisse ouverte'))),
    );
  }

  openCashSession(request: OpenCashSessionRequest): Observable<CashSessionResponse> {
    return this.http.post<CashSessionResponse>(`${API_BASE}/agent/cash-sessions/open`, request).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => of(this.openFallbackSession(request))),
    );
  }

  adjustCurrentCashSession(agentId: number, request: CashAdjustmentRequest): Observable<CashSessionResponse> {
    const params = new HttpParams().set('agentId', `${agentId}`);
    return this.http.post<CashSessionResponse>(`${API_BASE}/agent/cash-sessions/current/adjustments`, request, { params }).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => of(this.adjustFallbackSession(request))),
    );
  }

  closeCurrentCashSession(agentId: number, request: CloseCashSessionRequest): Observable<CashSessionResponse> {
    const params = new HttpParams().set('agentId', `${agentId}`);
    return this.http.post<CashSessionResponse>(`${API_BASE}/agent/cash-sessions/current/close`, request, { params }).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError(() => of(this.closeFallbackSession(request))),
    );
  }

  private openFallbackSession(request: OpenCashSessionRequest): CashSessionResponse {
    this.fallbackSession = {
      id: Date.now(),
      agentId: request.agentId,
      agentName: TEST_AGENT_NAME,
      agencyId: request.agencyId,
      agencyName: TEST_AGENCY_NAME,
      openingBalance: request.openingBalance,
      currentBalance: request.openingBalance,
      closingBalance: null,
      countedAmount: null,
      discrepancyAmount: null,
      openedAt: new Date().toISOString(),
      closedAt: null,
      status: 'OPEN',
    };
    this.addFallbackOperation('OPENING', request.openingBalance, 0, request.openingBalance, `OPEN-${this.fallbackSession.id}`, null);
    return this.fallbackSession;
  }

  private adjustFallbackSession(request: CashAdjustmentRequest): CashSessionResponse {
    const session = this.ensureFallbackSession();
    const before = session.currentBalance;
    const after = before + request.amount;
    session.currentBalance = after;
    this.addFallbackOperation('ADJUSTMENT', request.amount, before, after, request.reference, null);
    return session;
  }

  private closeFallbackSession(request: CloseCashSessionRequest): CashSessionResponse {
    const session = this.ensureFallbackSession();
    session.closingBalance = session.currentBalance;
    session.countedAmount = request.countedAmount;
    session.discrepancyAmount = request.countedAmount - session.currentBalance;
    session.closedAt = new Date().toISOString();
    session.status = 'CLOSED';
    this.addFallbackOperation('CLOSING', request.countedAmount, session.currentBalance, session.currentBalance, `CLOSE-${session.id}`, null);
    this.fallbackSession = null;
    return session;
  }

  private payFallbackTransfer(request: PayTransferRequest): Observable<TransferPayment> {
    const transfer = this.fallbackTransfers.find((item) => item.referenceCode === request.referenceCode);
    if (!transfer || !transfer.payable) {
      return throwError(() => new Error('Ce transfert ne peut pas etre paye.'));
    }

    const session = this.ensureFallbackSession();
    if (session.currentBalance < transfer.amountReceived) {
      return throwError(() => new Error('Solde de caisse insuffisant.'));
    }

    const before = session.currentBalance;
    session.currentBalance -= transfer.amountReceived;
    transfer.status = 'PAYE';
    transfer.payable = false;
    transfer.paidAt = new Date().toISOString();

    const payment: TransferPayment = {
      id: Date.now(),
      transferId: transfer.id,
      referenceCode: transfer.referenceCode,
      agentId: request.agentId,
      agentName: TEST_AGENT_NAME,
      agencyId: TEST_AGENCY_ID,
      agencyName: TEST_AGENCY_NAME,
      beneficiaryName: transfer.beneficiaryName || 'Beneficiaire',
      beneficiaryIdentityNumber: request.beneficiaryIdentityNumber,
      paidAmount: transfer.amountReceived,
      paidAt: transfer.paidAt,
      receiptNumber: `RCT-${transfer.referenceCode}`,
    };

    this.fallbackPayments.unshift(payment);
    this.addFallbackOperation('TRANSFER_PAID', transfer.amountReceived, before, session.currentBalance, transfer.referenceCode, transfer.id);
    return of(payment);
  }

  private ensureFallbackSession(): CashSessionResponse {
    if (!this.fallbackSession) {
      return this.openFallbackSession({ agentId: TEST_AGENT_ID, agencyId: TEST_AGENCY_ID, openingBalance: 50000 });
    }

    return this.fallbackSession;
  }

  private addFallbackOperation(
    operationType: string,
    amount: number,
    balanceBefore: number,
    balanceAfter: number,
    reference: string,
    transferId: number | null,
  ): void {
    this.fallbackOperations.unshift({
      id: Date.now(),
      cashSessionId: this.fallbackSession?.id ?? 1,
      operationType,
      amount,
      balanceBefore,
      balanceAfter,
      operationDate: new Date().toISOString(),
      reference,
      transferId,
    });
  }
}
