package br.com.pagamentos.kafka;

import br.com.pagamentos.dto.StatusPagamentoRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PagamentoStatusProducer {

    public static final String TOPIC = "pagamento-status";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PagamentoStatusProducer(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicar(StatusPagamentoRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(TOPIC, String.valueOf(request.idPagamento()), payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Não foi possível serializar a mensagem Kafka.", e);
        }
    }
}
