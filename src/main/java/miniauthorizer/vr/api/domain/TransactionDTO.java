package miniauthorizer.vr.api.domain;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionDTO(@NotBlank String numeroCartao, @NotBlank String senha, @NotNull @Positive BigDecimal valor) {

}
