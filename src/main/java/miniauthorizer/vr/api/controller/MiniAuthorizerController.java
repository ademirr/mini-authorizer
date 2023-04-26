package miniauthorizer.vr.api.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import miniauthorizer.vr.api.domain.Card;
import miniauthorizer.vr.api.domain.CardDTO;
import miniauthorizer.vr.api.domain.CardDTOResponse;
import miniauthorizer.vr.api.domain.TransactionDTO;
import miniauthorizer.vr.api.infra.CardNotFoundException;
import miniauthorizer.vr.api.service.MiniAuthorizerService;

@RestController
@RequestMapping("cartoes")
public class MiniAuthorizerController {
	
	@Autowired
	private MiniAuthorizerService service;
	
	@PostMapping
	public ResponseEntity<CardDTOResponse> createCard(@RequestBody @Valid CardDTO cardDTO, UriComponentsBuilder uriBuilder) {
		try {
			service.createCard(cardDTO.numeroCartao(), cardDTO.senha());
			var uri = uriBuilder.path("/cartoes/{numeroCartao}").buildAndExpand(cardDTO.numeroCartao()).toUri();
			return ResponseEntity.created(uri).body(new CardDTOResponse(cardDTO.senha(), cardDTO.numeroCartao()));
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.unprocessableEntity().body(new CardDTOResponse(cardDTO.senha(), cardDTO.numeroCartao()));
		}
	}
	
	@GetMapping("/{numeroCartao}")
	public ResponseEntity<BigDecimal> consultBalance(@PathVariable String numeroCartao) throws CardNotFoundException {
		Card cartao = service.findCard(numeroCartao);
		BigDecimal saldo = null;
		try {
			saldo = cartao.getSaldo();
		} catch (NullPointerException e) {
			throw new CardNotFoundException();
		}
		return ResponseEntity.ok(saldo);
	}
	
	@PostMapping(value = "/transacoes")
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public ResponseEntity<String> transaction(@RequestBody @Valid TransactionDTO transactionDTO, UriComponentsBuilder uriBuilder) {
		Card card = service.executeTransaction(transactionDTO);
		return ResponseEntity.created(uriBuilder.path("/cartoes/{numeroCartao}").buildAndExpand(card.getNumeroCartao()).toUri()).body("OK");
	}
}
