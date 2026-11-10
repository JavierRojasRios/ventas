package org.iesvdm.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.iesvdm.modelo.PedidoDetalle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstadisticasPedidosDTO {

    private BigDecimal importeTotal;
    private BigDecimal importeMedio;
    private BigDecimal importeMaximo;
    private BigDecimal importeMinimo;

    public static EstadisticasPedidosDTO fromPedidos(List<PedidoDetalle> pedidos) {
        BigDecimal importeTotal = pedidos.stream()
                .map(pedido -> pedido.getPedido().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal importeMedio = pedidos.isEmpty()
                ? BigDecimal.ZERO
                : importeTotal.divide(BigDecimal.valueOf(pedidos.size()), 2, RoundingMode.HALF_UP);

        BigDecimal importeMaximo = pedidos.stream()
                .map(pedido -> pedido.getPedido().getTotal())
                .max(BigDecimal::compareTo)
                .orElse(null);
        BigDecimal importeMinimo = pedidos.stream()
                .map(pedido -> pedido.getPedido().getTotal())
                .min(BigDecimal::compareTo)
                .orElse(null);

        return new EstadisticasPedidosDTO(importeTotal, importeMedio, importeMaximo, importeMinimo);
    }
}
