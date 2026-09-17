package br.com.pagamentos.service;

import br.com.pagamentos.domain.*;
import br.com.pagamentos.dto.CriarPagamentoRequest;
import br.com.pagamentos.exception.*;
import br.com.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;

    public PagamentoService(PagamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Pagamento adicionar(CriarPagamentoRequest request) {
        if (!request.metodoPagamento().isCartao() && request.numeroCartao() != null
                && !request.numeroCartao().isBlank()) {
            throw new BusinessException("numeroCartao só deve ser informado para métodos de cartão.");
        }

        if (request.metodoPagamento().isCartao()
                && (request.numeroCartao() == null || request.numeroCartao().isBlank())) {
            throw new BusinessException("numeroCartao é obrigatório para pagamento com cartão.");
        }

        Pagamento p = new Pagamento();
        p.setIdPagamento(request.idPagamento());
        p.setCpfCnpj(request.cpfCnpj());
        p.setMetodoPagamento(request.metodoPagamento());
        p.setNumeroCartao(request.metodoPagamento().isCartao() ? request.numeroCartao() : null);
        p.setValor(request.valor());
        p.setStatus(StatusPagamento.PENDENTE_PROCESSAMENTO);
        p.setAtivo(true);

        return repository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> listar(Long idPagamento, String cpfCnpj, StatusPagamento status) {
        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("ativo")));

            if (idPagamento != null) {
                predicates.add(cb.equal(root.get("idPagamento"), idPagamento));
            }
            if (cpfCnpj != null && !cpfCnpj.isBlank()) {
                predicates.add(cb.equal(root.get("cpfCnpj"), cpfCnpj));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Transactional
    public Pagamento processarStatus(Long idPagamento, StatusPagamento novoStatus) {
        Pagamento p = repository.findByIdPagamento(idPagamento)
                .orElseThrow(() -> new NotFoundException("Pagamento não encontrado: " + idPagamento));

        StatusPagamento atual = p.getStatus();

        if (atual == StatusPagamento.PROCESSADO_SUCESSO) {
            throw new BusinessException(
                    "Pagamento PROCESSADO_SUCESSO não pode ter o status alterado.");
        }

        boolean permitido =
                (atual == StatusPagamento.PENDENTE_PROCESSAMENTO &&
                        (novoStatus == StatusPagamento.PROCESSADO_SUCESSO
                                || novoStatus == StatusPagamento.PROCESSADO_FALHA))
                || (atual == StatusPagamento.PROCESSADO_FALHA
                        && novoStatus == StatusPagamento.PENDENTE_PROCESSAMENTO);

        if (!permitido) {
            throw new BusinessException(
                    "Transição de status inválida: " + atual + " -> " + novoStatus);
        }

        p.setStatus(novoStatus);
        return repository.save(p);
    }

    @Transactional
    public void inativar(Long id) {
        Pagamento p = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pagamento não encontrado: " + id));

        if (p.getStatus() != StatusPagamento.PENDENTE_PROCESSAMENTO) {
            throw new BusinessException(
                    "Somente pagamentos PENDENTE_PROCESSAMENTO podem ser excluídos.");
        }

        p.setAtivo(false);
        repository.save(p);
    }
}
