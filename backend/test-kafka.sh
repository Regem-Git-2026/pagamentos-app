#!/bin/sh
curl -i -X POST http://localhost:8080/pagamentos/status/kafka \
  -H 'Content-Type: application/json' \
  -d '{"idPagamento":123,"novoStatus":"PROCESSADO_SUCESSO"}'
