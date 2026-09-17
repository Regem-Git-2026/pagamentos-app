import { Component, OnInit, OnChanges, SimpleChanges, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pagamento, StatusPagamento } from './pagamento.model';
import { PagamentoService } from './pagamento.service';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-pagamento-lista',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagamento-lista.component.html'
})
export class PagamentoListaComponent implements OnInit, OnChanges {
  private service = inject(PagamentoService);
  private toast = inject(ToastService);

  @Input() refreshKey = 0;

  pagamentos: Pagamento[] = [];
  idPagamento: number | null = null;
  cpfCnpj = '';
  status: StatusPagamento | '' = '';

  page = 1;
  pageSize = 6;

  readonly statuses: StatusPagamento[] = [
    'PENDENTE_PROCESSAMENTO', 'PROCESSADO_SUCESSO', 'PROCESSADO_FALHA'
  ];

  ngOnInit() {
    this.carregar();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['refreshKey'] && !changes['refreshKey'].firstChange) {
      this.carregar();
    }
  }

  get pages(): number {
    return Math.max(1, Math.ceil(this.pagamentos.length / this.pageSize));
  }

  get paginaAtual(): Pagamento[] {
    const start = (this.page - 1) * this.pageSize;
    return this.pagamentos.slice(start, start + this.pageSize);
  }

  carregar() {
    this.service.listar({
      idPagamento: this.idPagamento,
      cpfCnpj: this.cpfCnpj,
      status: this.status
    }).subscribe({
      next: data => {
        this.pagamentos = data;
        this.page = 1;
      },
      error: err => this.toast.show(err?.error?.message ?? 'Erro ao listar pagamentos.', 'err')
    });
  }

  limparFiltros() {
    this.idPagamento = null;
    this.cpfCnpj = '';
    this.status = '';
    this.carregar();
  }

  excluir(p: Pagamento) {
    if (p.status !== 'PENDENTE_PROCESSAMENTO') {
      this.toast.show('Somente pagamentos pendentes podem ser excluídos.', 'err');
      return;
    }

    if (!confirm(`Inativar o pagamento ${p.idPagamento}?`)) return;

    this.service.inativar(p.id).subscribe({
      next: () => {
        this.toast.show('Pagamento inativado.');
        this.carregar();
      },
      error: err => this.toast.show(err?.error?.message ?? 'Erro ao inativar.', 'err')
    });
  }

  processar(p: Pagamento, novoStatus: StatusPagamento) {
    this.service.processar(p.idPagamento, novoStatus).subscribe({
      next: updated => {
        p.status = updated.status;
        this.toast.show(`Status atualizado para ${updated.status}.`);
      },
      error: err => this.toast.show(err?.error?.message ?? 'Status inválido.', 'err')
    });
  }

  processarKafka(p: Pagamento, novoStatus: StatusPagamento) {
    this.service.processarKafka(p.idPagamento, novoStatus).subscribe({
      next: () => this.toast.show('Mensagem publicada no Kafka. Atualize a lista em instantes.'),
      error: err => this.toast.show(err?.error?.message ?? 'Kafka indisponível.', 'err')
    });
  }

  proxima() {
    if (this.page < this.pages) this.page++;
  }

  anterior() {
    if (this.page > 1) this.page--;
  }

  badgeClass(status: StatusPagamento): string {
    if (status === 'PROCESSADO_SUCESSO') return 'badge success';
    if (status === 'PROCESSADO_FALHA') return 'badge failure';
    return 'badge pending';
  }
}
