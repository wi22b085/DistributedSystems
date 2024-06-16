package com.example.pdfgenerator.services;

import com.example.pdfgenerator.config.DataSourceConfig;
import com.example.pdfgenerator.config.RabbitMQConfig;
import com.example.pdfgenerator.entity.CustomerEntity;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;

import com.itextpdf.layout.Document;

import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;




@Component
@Slf4j
public class GeneratePdf {
    private final RabbitTemplate rabbit;
    @Autowired
    DataSourceConfig dataSourceConfig;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GeneratePdf(RabbitTemplate rabbit, @Qualifier("Db") JdbcTemplate jdbcTemplate) {
        this.rabbit = rabbit;
        this.jdbcTemplate = jdbcTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.ECHO_OUT_DATA_PDF)
    public void generatePdfMethod(String message) {
        String[] parts = message.split(",");

        String customerId = "";
        String sumString1 = "";
        String sumString2 = "";
        String sumString3 = "";
        String totalSumString = "";
        String cost1 = "";
        String cost2 = "";
        String cost3 = "";
        String totalCost = "";




        for (String part : parts) {
            if (part.startsWith("customerId:")) {
                customerId = part.split(":")[1];
            } else if (part.startsWith("sumString1:")) {
                sumString1 = part.split(":")[1];
            } else if (part.startsWith("sumString2:")) {
                sumString2 = part.split(":")[1];
            } else if (part.startsWith("sumString3:")) {
                sumString3 = part.split(":")[1];
            } else if (part.startsWith("totalSumString:")) {
                totalSumString = part.split(":")[1];
            } else if (part.startsWith("cost1:")) {
                cost1 = part.split(":")[1];
            } else if (part.startsWith("cost2:")) {
                cost2 = part.split(":")[1];
            } else if (part.startsWith("cost3:")) {
                cost3 = part.split(":")[1];
            } else if (part.startsWith("totalCost:")) {
                totalCost = part.split(":")[1];
            }
        }


            var sql = """
                    SELECT id, first_name,last_name
                    FROM customer 
                    Where id=?
                    """;

            CustomerEntity output = null;

            output = jdbcTemplate.queryForObject(sql, new Object[]{customerId}, (ResultSet rs, int rowNum) ->
                    new CustomerEntity(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    )
            );

            String firstname = output.getFirst_name();
            String lastname = output.getLast_name();


            LocalDateTime currentDateTime = LocalDateTime.now();
            String datetime = currentDateTime.toString();






            try {
                createSpecificPdf(firstname, lastname, datetime, sumString1, sumString2, sumString3, totalSumString, cost1, cost2, cost3, totalCost);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }








    public void createSpecificPdf(String firstname, String lastname, String datetime, String sumString1, String sumString2, String sumString3, String totalSumString, String cost1, String cost2, String cost3, String totalCost) throws IOException {
        String LOREM_IPSUM_TEXT = firstname + lastname  + datetime + sumString1 + sumString2 + sumString3 + totalSumString + cost1 + cost2 + cost3 + totalCost + "Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        String GOOGLE_MAPS_PNG = "./google_maps.png";
        String TARGET_PDF = "target.pdf";

        PdfWriter writer = new PdfWriter(TARGET_PDF);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        Paragraph loremIpsumHeader = new Paragraph("Lorem Ipsum header...")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.RED);
        document.add(loremIpsumHeader);
        document.add(new Paragraph(LOREM_IPSUM_TEXT));

        Paragraph listHeader = new Paragraph("Lorem Ipsum ...")
                .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD))
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.BLUE);
        List list = new List()
                .setSymbolIndent(12)
                .setListSymbol("\u2022")
                .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD));
        list.add(new ListItem("lorem ipsum 1"))
                .add(new ListItem("lorem ipsum 2"))
                .add(new ListItem("lorem ipsum 3"))
                .add(new ListItem("lorem ipsum 4"))
                .add(new ListItem("lorem ipsum 5"))
                .add(new ListItem("lorem ipsum 6"));
        document.add(listHeader);
        document.add(list);

        Paragraph tableHeader = new Paragraph("Lorem Ipsum Table ...")
                .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN))
                .setFontSize(18)
                .setBold()
                .setFontColor(ColorConstants.GREEN);
        document.add(tableHeader);
        Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
        table.addHeaderCell(getHeaderCell("Ipsum 1"));
        table.addHeaderCell(getHeaderCell("Ipsum 2"));
        table.addHeaderCell(getHeaderCell("Ipsum 3"));
        table.addHeaderCell(getHeaderCell("Ipsum 4"));
        table.setFontSize(14).setBackgroundColor(ColorConstants.WHITE);
        table.addCell("lorem 1");
        table.addCell("lorem 2");
        table.addCell("lorem 3");
        table.addCell("lorem 4");
        document.add(table);

        document.add(new AreaBreak());

        Paragraph imageHeader = new Paragraph("Lorem Ipsum Image ...")
                .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN))
                .setFontSize(18)
                .setBold()
                .setFontColor(ColorConstants.GREEN);
        document.add(imageHeader);
        ImageData imageData = ImageDataFactory.create(GOOGLE_MAPS_PNG);
        document.add(new Image(imageData));

        document.close();
    }

    private static Cell getHeaderCell(String s) {
        return new Cell().add(new Paragraph(s)).setBold().setBackgroundColor(ColorConstants.GRAY);
    }





}