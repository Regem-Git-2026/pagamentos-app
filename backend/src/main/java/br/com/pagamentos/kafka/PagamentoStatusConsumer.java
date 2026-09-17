package br.com.pagamentos.kafka;

import br.com.pagamentos.dto.StatusPagamentoRequest;
import br.com.pagamentos.service.PagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PagamentoStatusConsumer {

    private final ObjectMapper objectMapper;
    private final PagamentoService service;

    public PagamentoStatusConsumer(ObjectMapper objectMapper, PagamentoService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @KafkaListener(topics = PagamentoStatusProducer.TOPIC)
    public void consumir(String message) {
        try {
            StatusPagamentoRequest request =
                    objectMapper.readValue(message, StatusPagamentoRequest.class);
            service.processarStatus(request.idPagamento(), request.novoStatus());
        } catch (Exception e) {
            // Em produção, usar DLT/retry e observabilidade.
            System.err.println("Falha ao processar mensagem Kafka: " + e.getMessage());
        }
    }
}
