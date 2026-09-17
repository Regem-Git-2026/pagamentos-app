import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CriarPagamento, Pagamento, StatusPagamento } from './pagamento.model';

@Injectable({ providedIn: 'root' })
export class PagamentoService {
  private http = inject(HttpClient);
  private readonly api = 'http://localhost:8080';

  adicionar(payload: CriarPagamento): Observable<Pagamento> {
    return this.http.post<Pagamento>(`${this.api}/pagamentos`, payload);
  }

  listar(filtro: {
    idPagamento?: number | null;
    cpfCnpj?: string;
    status?: StatusPagamento | '';
  }): Observable<Pagamento[]> {
    let params = new HttpParams();
    if (filtro.idPagamento) params = params.set('idPagamento', filtro.idPagamento);
    if (filtro.cpfCnpj) params = params.set('cpfCnpj', filtro.cpfCnpj);
    if (filtro.status) params = params.set('status', filtro.status);
    return this.http.get<Pagamento[]>(`${this.api}/lista-pagamentos`, { params });
  }

  processar(idPagamento: number, novoStatus: StatusPagamento): Observable<Pagamento> {
    return this.http.put<Pagamento>(`${this.api}/pagamentos/status`, {
      idPagamento, novoStatus
    });
  }

  processarKafka(idPagamento: number, novoStatus: StatusPagamento): Observable<void> {
    return this.http.post<void>(`${this.api}/pagamentos/status/kafka`, {
      idPagamento, novoStatus
    });
  }

  inativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/pagamentos/${id}`);
  }
}
