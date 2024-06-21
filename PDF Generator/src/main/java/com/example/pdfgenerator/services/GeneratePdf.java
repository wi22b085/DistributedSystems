package com.example.pdfgenerator.services;

import com.example.pdfgenerator.config.DataSourceConfig;
import com.example.pdfgenerator.config.RabbitMQConfig;
import com.example.pdfgenerator.entity.CustomerEntity;
import com.itextpdf.kernel.colors.DeviceRgb;
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
        System.out.println(message);
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
        String datetime = "";
        String datetime_invoice = "";

        for (String part : parts) {
            if (part.startsWith("customerId:")) {
                customerId = part.substring("customerId:".length()); // Direkte Verwendung von substring, um den Wert zu extrahieren
            } else if (part.startsWith("sumString1:")) {
                sumString1 = part.substring("sumString1:".length());
            } else if (part.startsWith("sumString2:")) {
                sumString2 = part.substring("sumString2:".length());
            } else if (part.startsWith("sumString3:")) {
                sumString3 = part.substring("sumString3:".length());
            } else if (part.startsWith("totalSumString:")) {
                totalSumString = part.substring("totalSumString:".length());
            } else if (part.startsWith("cost1:")) {
                cost1 = part.substring("cost1:".length());
            } else if (part.startsWith("cost2:")) {
                cost2 = part.substring("cost2:".length());
            } else if (part.startsWith("cost3:")) {
                cost3 = part.substring("cost3:".length());
            } else if (part.startsWith("totalCost:")) {
                totalCost = part.substring("totalCost:".length());
            } else if (part.startsWith("datetime:")) {
                datetime = part.substring("datetime:".length());
            } else if (part.startsWith("datetime_invoice:")) {
                datetime_invoice = part.substring("datetime_invoice:".length());
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

            try {
                createSpecificPdf(datetime_invoice, customerId, firstname, lastname, datetime, sumString1, sumString2, sumString3, totalSumString, cost1, cost2, cost3, totalCost);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void createSpecificPdf(String datetime_invoice, String customerId, String firstname, String lastname, String datetime, String sumString1, String sumString2, String sumString3, String totalSumString, String cost1, String cost2, String cost3, String totalCost) throws IOException {
        String car_pic = "./car-photo.jpg"; // Photo from https://media.istockphoto.com/id/1357793078/vector/electric-plug-icon-electrical-plug-with-lighting-symbol-green-energy-logo-or-icon-vector.jpg?s=612x612&w=0&k=20&c=0U_z7e5tLDI29X7zesUckLKoMp_mfbsCEWtL4ub6rCo=
        String TARGET_PDF = "./../File Storage/" + customerId + "_" + datetime_invoice + ".pdf";

        PdfWriter writer = new PdfWriter(TARGET_PDF);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        ImageData imageData = ImageDataFactory.create(car_pic);
        Image image = new Image(imageData).scaleToFit(350, 350).setFixedPosition(pdf.getDefaultPageSize().getWidth() - 430, pdf.getDefaultPageSize().getHeight() - 180);
        document.add(image);

        document.add(new Paragraph("\n\n\n\n\n\n\n\n\n"));

        Paragraph invoiceHeader = new Paragraph("Invoice")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(18)
                .setBold()
                .setFontColor(ColorConstants.BLACK);
        document.add(invoiceHeader);

        Paragraph customerDetails = new Paragraph("Customer-ID: " + customerId + "\nCustomer: " + firstname + " " + lastname + "\nInvoice Date: " + datetime)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setFontColor(ColorConstants.BLACK);
        document.add(customerDetails);

        document.add(new Paragraph("\n\n\n\n"));


        Paragraph tableHeader = new Paragraph("Details")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.BLACK);
        document.add(tableHeader);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1})).useAllAvailableWidth();
        table.addHeaderCell(getHeaderCell("Charging Station Nr."));
        table.addHeaderCell(getHeaderCell("kWh"));
        table.addHeaderCell(getHeaderCell("Cost"));

        if (!sumString1.equals("0.0") && !cost1.equals("0.0")) {
            table.addCell("1");
            table.addCell(sumString1);
            table.addCell(cost1);
        }
        if (!sumString2.equals("0.0") && !cost2.equals("0.0")) {
            table.addCell("2");
            table.addCell(sumString2);
            table.addCell(cost2);
        }
        if (!sumString3.equals("0.0") && !cost3.equals("0.0")) {
            table.addCell("3");
            table.addCell(sumString3);
            table.addCell(cost3);
        }

        // Adding total sum and cost row
        table.addCell(getTotalCell("Total"));
        table.addCell(getTotalCell(totalSumString));
        table.addCell(getTotalCell(totalCost));

        document.add(table);

        document.close();
    }

    private static Cell getHeaderCell(String s) {
        return new Cell().add(new Paragraph(s)).setBold().setBackgroundColor(new DeviceRgb(47, 171, 47));
    }

    private static Cell getTotalCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setBold()
                .setBackgroundColor(new DeviceRgb(47, 171, 47));
    }
}