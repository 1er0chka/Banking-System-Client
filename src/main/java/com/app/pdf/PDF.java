package com.app.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

import com.app.DTO.OperationDTO;
import com.itextpdf.text.*;
import io.nayuki.qrcodegen.QrCode;
import com.itextpdf.text.pdf.PdfWriter;

public class PDF implements Runnable {
    public static final String FONT = "/font/arial.ttf";
    private OperationDTO operation;
    private String fileName;

    public void GeneratePdf(OperationDTO operation) {
        this.operation = operation;
        checkDir();
        fileName = "/pdf/recipient_" + operation.getDate() + "_" + operation.getTime().getHours() + "-" + operation.getTime().getMinutes() + "-" + operation.getTime().getSeconds() + "_" + operation.getName() + ".pdf";
    }

    private void checkDir() {
        File file = new File("/pdf");
        file.mkdir();
    }

    public File generateQRCode(String text) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String fileName = "/pdf".concat(uuid).concat(".png");
        QrCode qr0 = QrCode.encodeText(text, QrCode.Ecc.MEDIUM);
        BufferedImage img = toImage(qr0, 4, 0);
        File file = new File(fileName);
        ImageIO.write(img, "png", file);
        return file;
    }

    private BufferedImage toImage(QrCode qr, int scale, int border) {
        return toImage(qr, scale, border, 0xFFFFFF, 0x000000);
    }

    private BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr);
        if (scale < 0 || border < 0) {
            throw new IllegalArgumentException("Value out of range");
        }
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale) {
            throw new IllegalArgumentException("Scale or border too large");
        }

        BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                boolean color = qr.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, color ? darkColor : lightColor);
            }
        }
        return result;
    }

    @Override
    public void run() {
        Document document = new Document(PageSize.A7);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            URL font_path = Thread.currentThread().getContextClassLoader().getResource("arial.ttf");
            FontFactory.register(font_path.toString(), "arial");
            Font arialFont = FontFactory.getFont("arial", "Cp1251", true);

            String text = "     *** Чек операции ***\n\n " + operation.getDate() + " " + operation.getTime() + "\n " + operation.getName() + "\n Сумма операции: \n   " + operation.getSum() + " руб.\n\n";
            document.add(new Paragraph(text, arialFont));
            File imgFile = generateQRCode(text);
            // добавим QR код
            Image image = Image.getInstance(imgFile.getAbsolutePath());
            // установим размеры
            image.scaleAbsolute(80, 80);
            // добавим к документу
            document.add(image);
            document.close();
            writer.close();
            // удалим изображение
            imgFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
