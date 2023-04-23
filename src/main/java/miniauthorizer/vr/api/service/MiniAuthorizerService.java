package miniauthorizer.vr.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import miniauthorizer.vr.api.domain.Card;
import miniauthorizer.vr.api.domain.CardRepository;
import miniauthorizer.vr.api.domain.DetalhesCartaoDTO;
import miniauthorizer.vr.api.domain.TransactionDTO;

@Service
public class MiniAuthorizerService {
	
	@Autowired
	private CardRepository repository;
	
	public DetalhesCartaoDTO validateTransactionOK(TransactionDTO dto) {
		DetalhesCartaoDTO card = validateCard(dto);
		return card;
	}
	
	public DetalhesCartaoDTO validateCard(TransactionDTO dto) {
		Card card = consultCard(dto.numeroCartao());
		if (card != null) {
			if (validatePassword(card.getSenha(), dto.senha())){
				BigDecimal valor = new BigDecimal(dto.valor()).setScale(2, RoundingMode.UP);
				if (validateBalance(card.getSaldo(), valor)){
					card.setSaldo(card.getSaldo().subtract(valor));
					return new DetalhesCartaoDTO(card.getNumeroCartao(), card.getSaldo(), "OK");
				}
				return new DetalhesCartaoDTO(card.getNumeroCartao(), card.getSaldo(), "SALDO_INSUFICIENTE");
			}
			return new DetalhesCartaoDTO(card.getNumeroCartao(), card.getSaldo(), "SENHA_INVALIDA");
		}
		return new DetalhesCartaoDTO(null, null, "CARTAO_INEXISTENTE");
	}

	public Card consultCard(String numeroCartao) {
		Card cartao = repository.findByNumeroCartao(numeroCartao);
		return cartao;
	}

	public boolean validatePassword(String passwordCard, String passwordAPI) {
		if (passwordCard.equals(passwordAPI)) {
			return true;
		}
		return false;
	}

	public boolean validateBalance(BigDecimal saldoCartao, BigDecimal valorTransacao) {
		if (saldoCartao.compareTo(valorTransacao) >= 0) {
			return true;
		}
		return false;
	}

}
