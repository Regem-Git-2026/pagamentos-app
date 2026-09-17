export type MetodoPagamento = 'boleto' | 'pix' | 'cartao_credito' | 'cartao_debito';
export type StatusPagamento = 'PENDENTE_PROCESSAMENTO' | 'PROCESSADO_SUCESSO' | 'PROCESSADO_FALHA';

export interface Pagamento {
  id: number;
  idPagamento: number;
  cpfCnpj: string;
  metodoPagamento: MetodoPagamento;
  numeroCartao?: string | null;
  valor: number;
  status: StatusPagamento;
  ativo: boolean;
}

export interface CriarPagamento {
  idPagamento: number;
  cpfCnpj: string;
  metodoPagamento: MetodoPagamento;
  numeroCartao?: string | null;
  valor: number;
}
