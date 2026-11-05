package org.iesvdm.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDetalle {

    private Pedido pedido;
    private String clienteNombreCompleto;
    private String comercialNombreCompleto;
}