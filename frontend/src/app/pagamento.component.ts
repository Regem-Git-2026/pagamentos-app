import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CriarPagamento, MetodoPagamento } from './pagamento.model';
import { PagamentoService } from './pagamento.service';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-pagamento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagamento.component.html'
})
export class PagamentoComponent {
  private service = inject(PagamentoService);
  private toast = inject(ToastService);

  @Output() salvo = new EventEmitter<void>();

  form: CriarPagamento = {
    idPagamento: 0,
    cpfCnpj: '',
    metodoPagamento: 'pix',
    numeroCartao: null,
    valor: 0
  };

  metodos: MetodoPagamento[] = ['boleto', 'pix', 'cartao_credito', 'cartao_debito'];
  enviando = false;

  get isCartao(): boolean {
    return this.form.metodoPagamento === 'cartao_credito'
      || this.form.metodoPagamento === 'cartao_debito';
  }

  salvar() {
    if (!this.form.idPagamento || !this.form.cpfCnpj || this.form.valor <= 0) {
      this.toast.show('Preencha ID do pagamento, CPF/CNPJ e valor.', 'err');
      return;
    }

    if (this.isCartao && !this.form.numeroCartao) {
      this.toast.show('Informe o número do cartão.', 'err');
      return;
    }

    if (!this.isCartao) this.form.numeroCartao = null;

    this.enviando = true;
    this.service.adicionar(this.form).subscribe({
      next: () => {
        this.toast.show('Pagamento cadastrado com status PENDENTE_PROCESSAMENTO.');
        this.form = {
          idPagamento: 0, cpfCnpj: '', metodoPagamento: 'pix',
          numeroCartao: null, valor: 0
        };
        this.salvo.emit();
      },
      error: err => this.toast.show(err?.error?.message ?? 'Erro ao cadastrar pagamento.', 'err'),
      complete: () => this.enviando = false
    });
  }
}
