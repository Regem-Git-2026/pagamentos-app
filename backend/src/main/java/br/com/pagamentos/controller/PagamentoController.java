package br.com.pagamentos.controller;

import br.com.pagamentos.domain.StatusPagamento;
import br.com.pagamentos.dto.*;
import br.com.pagamentos.kafka.PagamentoStatusProducer;
import br.com.pagamentos.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin(origins = {"http://localhost:4200"})
public class PagamentoController {

    private final PagamentoService service;
    private final PagamentoStatusProducer producer;

    public PagamentoController(PagamentoService service, PagamentoStatusProducer producer) {
        this.service = service;
        this.producer = producer;
    }

    @PostMapping("/pagamentos")
    public ResponseEntity<PagamentoResponse> criar(
            @Valid @RequestBody CriarPagamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PagamentoResponse.from(service.adicionar(request)));
    }

    @GetMapping("/lista-pagamentos")
    public List<PagamentoResponse> listar(
            @RequestParam(required = false) Long idPagamento,
            @RequestParam(required = false) String cpfCnpj,
            @RequestParam(required = false) StatusPagamento status) {
        return service.listar(idPagamento, cpfCnpj, status)
                .stream().map(PagamentoResponse::from).toList();
    }

    @PutMapping("/pagamentos/status")
    public PagamentoResponse atualizarStatus(
            @Valid @RequestBody StatusPagamentoRequest request) {
        return PagamentoResponse.from(
                service.processarStatus(request.idPagamento(), request.novoStatus()));
    }

    @PostMapping("/pagamentos/status/kafka")
    public ResponseEntity<Void> enviarKafka(
            @Valid @RequestBody StatusPagamentoRequest request) {
        producer.publicar(request);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/pagamentos/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
