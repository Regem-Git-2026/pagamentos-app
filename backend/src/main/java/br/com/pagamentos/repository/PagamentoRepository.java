package br.com.pagamentos.repository;

import br.com.pagamentos.domain.Pagamento;
import br.com.pagamentos.domain.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PagamentoRepository
        extends JpaRepository<Pagamento, Long>, JpaSpecificationExecutor<Pagamento> {

    Optional<Pagamento> findByIdPagamento(Long idPagamento);
}
