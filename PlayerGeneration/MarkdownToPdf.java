// Completly stolen from ChatGPT
// Needs some dependencies set up using maven eg.
/*
<dependencies>
    <!-- CommonMark for Markdown to HTML -->
    <dependency>
        <groupId>org.commonmark</groupId>
        <artifactId>commonmark</artifactId>
        <version>0.18.2</version>
    </dependency>

    <!-- iText for HTML to PDF -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itext7-core</artifactId>
        <version>7.2.2</version>
        <type>pom</type>
    </dependency>
</dependencies>
 */

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MarkdownToPdf {
    public static void main(String[] args) throws Exception {
        // Specify the path to the markdown file
        String markdownFilePath = "output.md"; // Example markdown file
        String outputPdfPath = "output.pdf";  // Output PDF file

        // Convert the Markdown to HTML
        String htmlContent = markdownToHtml(markdownFilePath);

        // Convert the HTML to PDF
        htmlToPdf(htmlContent, outputPdfPath);
    }

    // Converts Markdown file to HTML
    public static String markdownToHtml(String markdownFilePath) throws IOException {
        // Read the markdown file content
        Path path = Paths.get(markdownFilePath);
        String markdownContent = Files.readString(path);

        // Create a parser and renderer
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        // Parse the markdown to an Abstract Syntax Tree (AST)
        org.commonmark.node.Node document = parser.parse(markdownContent);

        // Render the AST to HTML
        return renderer.render(document);
    }

    // Converts HTML content to a PDF
    public static void htmlToPdf(String htmlContent, String outputPdfPath) throws Exception {
        // Create an output stream to write the PDF
        OutputStream outputStream = new FileOutputStream(outputPdfPath);

        // Use iText to convert HTML to PDF
        HtmlConverter.convertToPdf(htmlContent, outputStream);

        System.out.println("PDF generated successfully: " + outputPdfPath);
    }
}
