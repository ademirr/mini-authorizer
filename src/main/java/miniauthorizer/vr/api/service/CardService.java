package miniauthorizer.vr.api.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import miniauthorizer.vr.api.domain.Card;
import miniauthorizer.vr.api.domain.CardRepository;

@Service
public class CardService {
	
	@Value("${saldo.inicial}")
    private BigDecimal saldoInicial;
	
	@Autowired
	private CardRepository repository;

	public Card createCard(String numeroCartao, String senha) {
		Card cartao = new Card(numeroCartao, senha, saldoInicial);
		return repository.save(cartao);
	}

	public Card findCard(String numeroCartao) {
		return repository.findByNumeroCartao(numeroCartao);
	}
}
