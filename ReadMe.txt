Nessa aplicação utilizei Java 17, Maven, Spring Boot 3.0.6 e MySQL.
Utilizei também o Flyway para criar automaticamente a tabela no banco de dados ao subir a aplicação.
Utilizei o springdoc para exibir a documentação da API, que pode ser acessada pelo endereço http://localhost:8080/swagger-ui/index.html quando a aplicação estiver rodando.
Fiz a implementação em camadas (controller, service, domain e infra).
Fiz também testes unitários nas camadas controller, service e na classe CardRepository da domain.
No arquivo application.properties coloquei as configs de banco de dados e o valor do saldo inicial.
No diretório resources/db/migration está o create da tabela.