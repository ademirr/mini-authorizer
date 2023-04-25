create table card(
	id bigint not null auto_increment, 
    numero_cartao varchar(100) not null unique, 
    senha varchar(100) not null,
	saldo decimal(6,2),
    primary key(id)
);