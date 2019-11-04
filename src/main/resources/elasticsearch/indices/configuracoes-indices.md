# Configurações dos índices do Elasticsearch

Colocar nesta pasta as configurações dos índices do Elasticsearch, seguindo o padrão de nome:

```
<NOME-DO-INDICE>.json
```

Para informações sobre o formato do arquivo, consultar a [documentação do Elasticsearch.](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html)

Ao rodar a indexação de um job, o sistema verificará se o índice não existe no servidor. Se não existir e houver
um arquivo de configuração deste índice na pasta, o índice será criado com essas configurações.
