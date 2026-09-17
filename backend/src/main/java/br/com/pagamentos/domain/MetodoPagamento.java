package br.com.pagamentos.domain;

public enum MetodoPagamento {
    boleto,
    pix,
    cartao_credito,
    cartao_debito;

    public boolean isCartao() {
        return this == cartao_credito || this == cartao_debito;
    }
}
