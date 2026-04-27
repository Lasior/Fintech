package com.cristian.fintech.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import com.cristian.fintech.enums.TipoMovimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@NotNull(message = "El tipo de movimiento es obligatorio")
	@Column(nullable = false)
	private TipoMovimiento tipo;
	
	@NotNull(message = "El importe no puede ser nulo")
	@PositiveOrZero(message = "El importe no puede ser negativo")
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal importe;
	
	@NotNull(message = "El saldo post operación no puede ser nulo")
	@Positive(message = "El saldo post operación debe ser mayor que 0")
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal saldoPostOperacion;

	@Size(max = 500)
	@Column(nullable = false, length = 500)
	private String descripcion;

	@Column(nullable = false, updatable = false)
	@CreatedDate
	private LocalDateTime fechaMovimiento;
	
	@ManyToOne
	@JoinColumn(name = "cuenta_id", nullable = false)
	private Cuenta cuenta;
}