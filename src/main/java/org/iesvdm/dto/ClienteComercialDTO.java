package org.iesvdm.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClienteComercialDTO {

    private int comercialId;
    private String comercialNombre;
    private LocalDate fecha;
    private Integer prioridad;
}
