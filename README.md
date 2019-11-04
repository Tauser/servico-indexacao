# Serviço de Indexação

Serviço responsável por agendar e permitir execução manual de jobs de indexação de informações da Câmara no ElasticSearch. 
Os dados indexados podem ser pesquisados no portal institucional. Provê interface web e API REST de administração. 

## Instruções

### Em desenvolvimento, seguir os seguintes passos:

1. `docker volume create dados-elastic-search` (Somente na primeira vez, para criar o volume onde serão salvos dados do índice)

2. `docker-compose up` (Sobe um ElasticSearch e MongoDB locais)

3. Rodar direto da IDE a classe App, lembrando de informar o profile para a jvm com `-Dspring.profiles.active=dev`

4. (Alternativa) `mvn spring-boot:run -Drun.profiles=dev`

5. Acessar [http://localhost:8080/servico-indexacao](http://localhost:8080/servico-indexacao)

6. Caso queira apagar os dados do MongoDB local, execute `docker-compose rm`


## API
 - Listar jobs disponíveis: http://localhost:8080/api/jobs
 - Listar execuções de jobs encerradas no dia: http://localhost:8080/api/execucoes/por-dia/dd-MM-YYYY
 - Listar agendamentos de execuções: http://localhost:8080/api/agendamentos
 - Listar índices presentes no ElasticSearch: http://localhost:8080/api/indices
 - Enviar um documento para ser indexado por todos os jobs que manipulam o índice NOME:
   - enviar HTTP POST para http://localhost:8080/api/execucoes/por-indice/NOME
   - os parâmetros de execução podem ser passados via url (query string) ou no corpo da requisição, usando o Content-Type (application/x-www-form-urlencoded; charset=utf-8)
 - Enviar um documento para ser indexado pelo job de nome NOME_JOB:
    - enviar HTTP POST para http://localhost:8080/api/execucoes/por-job/NOME_JOB
    - os parâmetros de execução podem ser passados via url (query string) ou no corpo da requisição, usando o Content-Type (application/x-www-form-urlencoded; charset=utf-8)
 - Excluir um documento de id ID do índice NOME_INDICE: enviar HTTP DELETE para http://localhost:8080/api/indices/NOME_INDICE/documentos/ID

Para ver os outros serviços, consultar a classe [ApiRestController](src/main/java/br/leg/camara/indexacao/adaptadores/rest/ApiRestController.java)


## Criação de jobs de indexação

Para criar um job de indexação, consulte [a documentação da biblioteca cliente](https://git.camara.gov.br/sepop/indexacao-cliente)

## Configuração automática dos índices

Para que o sistema configure automaticamente um índice a partir de um arquivo de configuração do classpath, 
[consulte a documentação aqui](src/main/resources/elasticsearch/indices/configuracoes-indices.md).

Se preferir, é possível criar manualmente um índice, acessando [http://localhost:8080/servico-indexacao/indices/novo](http://localhost:8080/servico-indexacao/indices/novo)
 