package br.com.pagamentos.dto;

import br.com.pagamentos.domain.StatusPagamento;
import jakarta.validation.constraints.NotNull;

public record StatusPagamentoRequest(
        @NotNull(message = "idPagamento é obrigatório")
        Long idPagamento,

        @NotNull(message = "novoStatus é obrigatório")
        StatusPagamento novoStatus
) {}
