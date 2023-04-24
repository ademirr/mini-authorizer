package miniauthorizer.vr.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import miniauthorizer.vr.api.domain.Card;
import miniauthorizer.vr.api.domain.CardRepository;
import miniauthorizer.vr.api.domain.TransactionDTO;
import miniauthorizer.vr.api.infra.CardNotFoundException;
import miniauthorizer.vr.api.infra.InvalidPasswordException;

@Service
public class MiniAuthorizerService {
	
	@Autowired
	private CardRepository repository;
	
	public Card executeTransaction(TransactionDTO dto) {
		Card card = validateTransaction(dto);
		BigDecimal valor = new BigDecimal(dto.valor()).setScale(2, RoundingMode.UP);
		card.setSaldo(card.getSaldo().subtract(valor));
		return card;
	}
	
	public Card validateTransaction(TransactionDTO dto) {
		Card card = consultCard(dto.numeroCartao());
		String senhaCartao = validateCard(card);
		validatePassword(senhaCartao, dto.senha());
		return card;
	}

	public Card consultCard(String numeroCartao) {
		Card cartao = repository.findByNumeroCartao(numeroCartao);
		return cartao;
	}
	
	public String validateCard(Card card) {
		try {
			return card.getSenha();
		} catch (NullPointerException e) {
			throw new CardNotFoundException();
		}
	}
	
	public void validatePassword(String senhaCartao, String senhaDTO){
		Optional<String> senhaEntrada = Optional.of(senhaDTO);
		Optional<String> emptyPassword = null;
			emptyPassword = senhaEntrada.filter(g -> g.equals(senhaCartao));
		try {
			emptyPassword.orElseThrow();
		} catch (NoSuchElementException e) {
			throw new InvalidPasswordException();
		}
	}
}
