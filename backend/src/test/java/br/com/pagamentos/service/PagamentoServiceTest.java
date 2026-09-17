package br.com.pagamentos.service;

import br.com.pagamentos.domain.*;
import br.com.pagamentos.dto.CriarPagamentoRequest;
import br.com.pagamentos.exception.BusinessException;
import br.com.pagamentos.repository.PagamentoRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    @Mock
    PagamentoRepository repository;

    @InjectMocks
    PagamentoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarPagamentoComStatusPendente() {
        CriarPagamentoRequest request = new CriarPagamentoRequest(
                123L, "12345678901", MetodoPagamento.pix, null,
                new BigDecimal("100.00"));

        when(repository.save(any(Pagamento.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Pagamento result = service.adicionar(request);

        assertEquals(StatusPagamento.PENDENTE_PROCESSAMENTO, result.getStatus());
        assertTrue(result.isAtivo());
        verify(repository).save(any(Pagamento.class));
    }

    @Test
    void devePermitirPendenteParaSucesso() {
        Pagamento p = pagamento(123L, StatusPagamento.PENDENTE_PROCESSAMENTO);
        when(repository.findByIdPagamento(123L)).thenReturn(Optional.of(p));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Pagamento result = service.processarStatus(123L, StatusPagamento.PROCESSADO_SUCESSO);

        assertEquals(StatusPagamento.PROCESSADO_SUCESSO, result.getStatus());
    }

    @Test
    void devePermitirFalhaParaPendente() {
        Pagamento p = pagamento(123L, StatusPagamento.PROCESSADO_FALHA);
        when(repository.findByIdPagamento(123L)).thenReturn(Optional.of(p));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Pagamento result = service.processarStatus(123L, StatusPagamento.PENDENTE_PROCESSAMENTO);

        assertEquals(StatusPagamento.PENDENTE_PROCESSAMENTO, result.getStatus());
    }

    @Test
    void naoDeveAlterarSucesso() {
        Pagamento p = pagamento(123L, StatusPagamento.PROCESSADO_SUCESSO);
        when(repository.findByIdPagamento(123L)).thenReturn(Optional.of(p));

        assertThrows(BusinessException.class,
                () -> service.processarStatus(123L, StatusPagamento.PENDENTE_PROCESSAMENTO));

        verify(repository, never()).save(any());
    }

    @Test
    void naoDevePermitirFalhaParaSucesso() {
        Pagamento p = pagamento(123L, StatusPagamento.PROCESSADO_FALHA);
        when(repository.findByIdPagamento(123L)).thenReturn(Optional.of(p));

        assertThrows(BusinessException.class,
                () -> service.processarStatus(123L, StatusPagamento.PROCESSADO_SUCESSO));
    }

    @Test
    void deveInativarSomentePendente() {
        Pagamento p = pagamento(123L, StatusPagamento.PENDENTE_PROCESSAMENTO);
        p.setAtivo(true);
        when(repository.findById(1L)).thenReturn(Optional.of(p));

        service.inativar(1L);

        assertFalse(p.isAtivo());
        verify(repository).save(p);
    }

    @Test
    void naoDeveInativarProcessado() {
        Pagamento p = pagamento(123L, StatusPagamento.PROCESSADO_FALHA);
        when(repository.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(BusinessException.class, () -> service.inativar(1L));
        verify(repository, never()).save(any());
    }

    private Pagamento pagamento(Long idPagamento, StatusPagamento status) {
        Pagamento p = new Pagamento();
        p.setIdPagamento(idPagamento);
        p.setCpfCnpj("123");
        p.setMetodoPagamento(MetodoPagamento.pix);
        p.setValor(new BigDecimal("10"));
        p.setStatus(status);
        p.setAtivo(true);
        return p;
    }
}
