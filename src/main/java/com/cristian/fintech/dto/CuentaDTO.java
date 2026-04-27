package com.cristian.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.cristian.fintech.enums.EstadoCuenta;
import com.cristian.fintech.enums.TipoCuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaDTO {

    private Long id;

    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Pattern(regexp = "\\d{1,20}", message = "El número de cuenta debe tener hasta 20 dígitos")
    private String numeroCuenta;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El saldo no puede ser nulo")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    @NotNull(message = "El estado de la cuenta es obligatorio")
    private EstadoCuenta estado;

    private LocalDateTime fechaApertura;

    private List<MovimientoDTO> movimientos;

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
}