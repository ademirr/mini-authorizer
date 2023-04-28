package miniauthorizer.vr.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import miniauthorizer.vr.api.domain.Card;
import miniauthorizer.vr.api.domain.TransactionDTO;
import miniauthorizer.vr.api.service.TransactionService;

@RestController
@RequestMapping("transacoes")
public class TransactionController {
	
	@Autowired
	private TransactionService service;
	
	@PostMapping
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public ResponseEntity<String> transaction(@RequestBody @Valid TransactionDTO transactionDTO, UriComponentsBuilder uriBuilder) {
		Card card = service.executeTransaction(transactionDTO);
		return ResponseEntity.created(uriBuilder.path("/cartoes/{numeroCartao}").buildAndExpand(card.getNumeroCartao()).toUri()).body("OK");
	}
}
