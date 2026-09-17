package br.com.pagamentos.dto;

import br.com.pagamentos.domain.MetodoPagamento;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CriarPagamentoRequest(
        @NotNull(message = "idPagamento é obrigatório")
        Long idPagamento,

        @NotBlank(message = "cpfCnpj é obrigatório")
        @Size(max = 20, message = "cpfCnpj deve ter no máximo 20 caracteres")
        String cpfCnpj,

        @NotNull(message = "metodoPagamento é obrigatório")
        MetodoPagamento metodoPagamento,

        @Size(max = 30, message = "numeroCartao deve ter no máximo 30 caracteres")
        String numeroCartao,

        @NotNull(message = "valor é obrigatório")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal valor
) {}
