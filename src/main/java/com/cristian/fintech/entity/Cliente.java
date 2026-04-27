package com.cristian.fintech.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El DNI no puede estar vacío")
	@Column(nullable = false, length = 9, unique = true)
	@Pattern(regexp = "\\d{8}[A-Za-z]", message = "DNI inválido")
	private String dni;

	@NotBlank(message = "El nombre no puede estar vacío")
	@Column(nullable = false, length = 100)
	private String nombre;
	
	@NotBlank(message = "Los apellidos no puede estar vacío")
	@Column(nullable = false, length = 100)
	private String apellidos;
	
	@NotBlank(message = "El email no puede estar vacío")
	@Column(nullable = false, length = 100, unique = true)
	@Email(message = "El email no tiene un formato válido")
	private String email;
	
	@NotBlank(message = "El teléfono no puede estar vacío")
	@Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
	private String telefono;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(nullable = false, updatable = false)
	@CreatedDate
	private LocalDateTime fechaAlta;
	
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuenta> cuentas = new ArrayList<>();
}