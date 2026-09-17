package br.com.pagamentos.dto;

import br.com.pagamentos.domain.MetodoPagamento;
import br.com.pagamentos.domain.Pagamento;
import br.com.pagamentos.domain.StatusPagamento;
import java.math.BigDecimal;

public record PagamentoResponse(
        Long id,
        Long idPagamento,
        String cpfCnpj,
        MetodoPagamento metodoPagamento,
        String numeroCartao,
        BigDecimal valor,
        StatusPagamento status,
        boolean ativo
) {
    public static PagamentoResponse from(Pagamento p) {
        return new PagamentoResponse(
                p.getId(), p.getIdPagamento(), p.getCpfCnpj(),
                p.getMetodoPagamento(), p.getNumeroCartao(),
                p.getValor(), p.getStatus(), p.isAtivo()
        );
    }
}
