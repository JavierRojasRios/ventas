package org.iesvdm.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientePedidosDTO {

    private int clienteId;
    private String clienteNombreCompleto;
    private BigDecimal importeTotalPedidos;
}
