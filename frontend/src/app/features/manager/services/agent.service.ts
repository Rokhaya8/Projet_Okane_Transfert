import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import {
  Agent,
  AgentDetail,
  CreateAgentRequest,
  UpdateAgentRequest,
} from '../models/agent.model';

@Injectable({ providedIn: 'root' })
export class AgentService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/manager/agents`;

  getAgents(): Observable<Agent[]> {
    return this.http.get<Agent[]>(this.base);
  }

  getAgent(id: number): Observable<AgentDetail> {
    return this.http.get<AgentDetail>(`${this.base}/${id}`);
  }

  createAgent(request: CreateAgentRequest): Observable<Agent> {
    return this.http.post<Agent>(this.base, request);
  }

  updateAgent(id: number, request: UpdateAgentRequest): Observable<Agent> {
    return this.http.put<Agent>(`${this.base}/${id}`, request);
  }

  activateAgent(id: number): Observable<Agent> {
    return this.http.patch<Agent>(`${this.base}/${id}/activate`, {});
  }

  suspendAgent(id: number): Observable<Agent> {
    return this.http.patch<Agent>(`${this.base}/${id}/suspend`, {});
  }
}
