package miniauthorizer.vr.api.infra;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> tratarErroSaldoNegativo() {
        return ResponseEntity.unprocessableEntity().body("SALDO_INSUFICIENTE");
    }
	 
	 @ExceptionHandler(TransactionCardNotFoundException.class)
    public ResponseEntity<String> tratarErroCartaoInexistente() {
        return ResponseEntity.unprocessableEntity().body("CARTAO_INEXISTENTE");
    }
	 
	 @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> tratarErroSenhaInvalida() {
        return ResponseEntity.unprocessableEntity().body("SENHA_INVALIDA");
    }
	 
	 @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<BigDecimal> tratarErroCartaoInexistenteConsulta() {
        return ResponseEntity.notFound().build();
    }
}
