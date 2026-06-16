import { Injectable, inject } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';
import { AgentService } from './agent.service';
import { OperationService } from './operation.service';
import { ReportService } from './report.service';
import { ValidationService } from './validation.service';
import { DashboardStats } from '../models/dashboard.model';
import { AgentPerformance } from '../models/report.model';
import { Operation } from '../models/operation.model';

@Injectable({ providedIn: 'root' })
export class ManagerService {
  private readonly agentService = inject(AgentService);
  private readonly operationService = inject(OperationService);
  private readonly reportService = inject(ReportService);
  private readonly validationService = inject(ValidationService);

  getDashboardStats(): Observable<DashboardStats> {
    return forkJoin({
      agents: this.agentService.getAgents(),
      performance: this.reportService.getPerformance(),
    }).pipe(
      map(({ agents, performance }) => ({
        totalAgents: agents.length,
        activeAgents: agents.filter((a) => a.active).length,
        transactionsThisMonth: performance.transactionsThisMonth,
        monthlyVolume: performance.monthlyVolume,
        pendingValidations: performance.pendingValidations,
        successRate: performance.successRate,
      })),
    );
  }

  getRecentOperations(): Observable<Operation[]> {
    return this.operationService.getOperations().pipe(
      map((ops) =>
        [...ops].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()).slice(0, 10),
      ),
    );
  }

  getAgentPerformanceChart(from?: string, to?: string): Observable<AgentPerformance[]> {
    return this.reportService.getAgentPerformance(from, to);
  }

  getTransferEvolution(): Observable<{ label: string; count: number }[]> {
    return this.operationService.getOperations().pipe(
      map((ops) => {
        const grouped = new Map<string, number>();
        ops.forEach((op) => {
          const date = op.createdAt?.substring(0, 10) ?? 'N/A';
          grouped.set(date, (grouped.get(date) ?? 0) + 1);
        });
        return Array.from(grouped.entries())
          .sort(([a], [b]) => a.localeCompare(b))
          .slice(-7)
          .map(([label, count]) => ({ label, count }));
      }),
    );
  }
}
