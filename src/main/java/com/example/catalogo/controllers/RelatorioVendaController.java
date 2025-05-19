package com.example.catalogo.controllers;

import com.example.catalogo.models.Venda;
import com.example.catalogo.services.RelatorioVendasPdf;
import com.example.catalogo.services.VendaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class RelatorioVendaController {

    @Autowired
    private VendaService vendaService;

    @GetMapping("/relatorios/vendas/pdf")
    public void gerarRelatorioVendasPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            HttpServletResponse response) throws IOException {

        List<Venda> vendas;

        if (inicio != null && fim != null) {
            vendas = vendaService.buscarVendasConfirmadasNoPeriodo(inicio, fim);
        } else {
            vendas = vendaService.listarVendasConfirmadas();
        }

        var pdfStream = RelatorioVendasPdf.gerar(vendas);

        // Define o tipo do conteúdo como PDF
        response.setContentType("application/pdf");

        // Define o cabeçalho para download com nome do arquivo
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_vendas.pdf");

        // Escreve o PDF na resposta
        org.apache.commons.io.IOUtils.copy(pdfStream, response.getOutputStream());

        response.flushBuffer();
    }
}
