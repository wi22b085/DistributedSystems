package com.example.pdfgenerator.services;

import com.example.pdfgenerator.config.RabbitMQConfig;
import com.example.pdfgenerator.entity.CustomerEntity;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeneratePdfTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    @Qualifier("Db")
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private GeneratePdf generatePdf;

    @Test
    public void testCreatePdf() throws SQLException, IOException {

        // Arrange
        String message = "datetime:01.01.2000 10:10,datetime_invoice:2000-01-01T10_10_10,sumString1:14.0,sumString2:4.18,sumString3:0.0,totalSumString:18.18,cost1:28.0,cost2:8.36,cost3:0.0,totalCost:36.36,customerId:2";
        String sql = """
                    SELECT id, first_name,last_name
                    FROM customer 
                    Where id=?
                    """;
        String customerId = "2";
        String datetime_invoice = "2000-01-01T10_10_10";
        CustomerEntity mockCustomer = new CustomerEntity(2, "Fritzi", "Fratzi");
        when(jdbcTemplate.queryForObject(eq(sql), any(Object[].class), any(RowMapper.class))).thenReturn(mockCustomer);



        // Act
        generatePdf.generatePdfMethod(message);

        // Assert
        verify(jdbcTemplate, times(1)).queryForObject(eq(sql), any(Object[].class), any(RowMapper.class));


        Path path = Paths.get("./../File Storage/" + customerId + "_" + datetime_invoice + ".pdf");

        assertThat(Files.exists(path)).isTrue();

        assertPdfContent(path, "Customer-ID: 2", "Customer: Fritzi Fratzi", "Invoice Date: 01.01.2000 10:10", "14.0", "4.18", "18.18", "28.0", "8.36", "36.36");

        // .pdf nach Test wieder löschen:
        Files.deleteIfExists(path);

    }

    private void assertPdfContent(Path pdfPath, String... expectedContents) throws IOException {
        try (PdfReader reader = new PdfReader(pdfPath.toString());
             PdfDocument pdfDoc = new PdfDocument(reader)) {

            StringBuilder text = new StringBuilder();
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                text.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
            }

            String pdfText = text.toString();
            for (String expectedContent : expectedContents) {
                assertThat(pdfText).contains(expectedContent);
            }
        }
    }

}
