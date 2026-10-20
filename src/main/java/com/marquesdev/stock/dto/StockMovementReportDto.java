package com.marquesdev.stock.dto;

import com.marquesdev.stock.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementReportDto {

    private String codigoProduto;
    private String nomeProduto;
    private String categoria;
    private Type tipoMovimentacao;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal valorTotal;
    private String codigoMovimentacao;
    private String observacao;
    private String fornecedor;
    private String cliente;
    private LocalDateTime dataMovimentacao;
    private String criadoPor;
}
