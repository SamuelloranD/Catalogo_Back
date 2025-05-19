package com.example.catalogo.services;

import com.example.catalogo.models.ItemVenda;
import com.example.catalogo.models.Venda;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioVendasPdf {

    public static ByteArrayInputStream gerar(List<Venda> vendas) {
        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(documento, out);
            documento.open();

            Font tituloFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titulo = new Paragraph("Relatório de Vendas Confirmadas\nRH Kosmetic", tituloFonte);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(Chunk.NEWLINE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Venda venda : vendas) {
                documento.add(new Paragraph("Venda ID: " + venda.getId()));
                documento.add(new Paragraph("Usuário: " + venda.getUsuario().getUsername()));
                documento.add(new Paragraph("Data/Hora: " + venda.getDataHora().format(formatter)+"\n\n"));

                PdfPTable tabela = new PdfPTable(4);
                tabela.setWidthPercentage(100);
                tabela.setWidths(new int[]{4, 1, 2, 2});

                adicionarCelulaCabecalho(tabela, "Produto");
                adicionarCelulaCabecalho(tabela, "Qtd");
                adicionarCelulaCabecalho(tabela, "Preço Unit.");
                adicionarCelulaCabecalho(tabela, "Subtotal");

                for (ItemVenda item : venda.getItens()) {
                    tabela.addCell(item.getProduto().getNome());
                    tabela.addCell(String.valueOf(item.getQuantidade()));
                    tabela.addCell(String.format("R$ %.2f", item.getPrecoUnitario()));
                    BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
                    tabela.addCell(String.format("R$ %.2f", subtotal));

                }

                documento.add(tabela);
                documento.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
                documento.add(Chunk.NEWLINE);
            }

            documento.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void adicionarCelulaCabecalho(PdfPTable tabela, String texto) {
        PdfPCell cabecalho = new PdfPCell(new Phrase(texto));
        cabecalho.setBackgroundColor(Color.LIGHT_GRAY);
        cabecalho.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabela.addCell(cabecalho);
    }
}
