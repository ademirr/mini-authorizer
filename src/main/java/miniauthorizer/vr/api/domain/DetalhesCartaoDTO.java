package miniauthorizer.vr.api.domain;

import java.math.BigDecimal;

public record DetalhesCartaoDTO(String numeroCartao, BigDecimal saldo, String mensagem) {

}
