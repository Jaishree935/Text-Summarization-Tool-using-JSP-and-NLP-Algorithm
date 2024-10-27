/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String summary = (String) session.getAttribute("summary"); // Get the summary from session

        if (summary != null) {
            // Sanitize the summary text to remove unsupported characters
            summary = sanitizeText(summary);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"summary.pdf\"");

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    drawBorder(contentStream, page, 30);
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    contentStream.beginText();
                    contentStream.newLineAtOffset((page.getMediaBox().getWidth() - 200) / 2, 700);
                    contentStream.showText("Summary of the Provided Content");
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("");
                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 600);

                    List<String> lines = wrapText(summary, PDType1Font.HELVETICA, 12, 500);
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (i == 0) {
                            contentStream.newLineAtOffset(15, 0);
                        }
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -15);
                    }

                    contentStream.endText();
                }

                try (OutputStream out = response.getOutputStream()) {
                    document.save(out);
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Summary not found.");
        }
    }

    private String sanitizeText(String text) {
        return text.replaceAll("[^\\x20-\\x7E]", " "); // Keep only printable ASCII characters
    }

    private List<String> wrapText(String text, PDType1Font font, float fontSize, float width) throws IOException {
        List<String> wrappedLines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            float lineWidth = font.getStringWidth(line.toString() + word) / 1000 * fontSize;
            if (lineWidth > width) {
                wrappedLines.add(line.toString().trim());
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }
        wrappedLines.add(line.toString().trim());
        return wrappedLines;
    }

    private void drawBorder(PDPageContentStream contentStream, PDPage page, float borderSize) throws IOException {
        float margin = borderSize;
        float width = page.getMediaBox().getWidth();
        float height = page.getMediaBox().getHeight();

        contentStream.setLineWidth(1f);
        contentStream.moveTo(margin, margin);
        contentStream.lineTo(width - margin, margin);
        contentStream.lineTo(width - margin, height - margin);
        contentStream.lineTo(margin, height - margin);
        contentStream.closePath();
        contentStream.stroke();
    }
}