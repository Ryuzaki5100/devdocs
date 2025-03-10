import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

@RestController
@RequestMapping("/api")
public class DevController {

    @PostMapping("/get-docs")
    public ResponseEntity<InputStreamResource> getdocs(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String branch) {
        
        List<String> fileContents = GitHubRepoContents.listFileStructure(owner, repo, branch, "");
        List<String> batchFiles = new ArrayList<>();
        
        for (String str : fileContents) {
            if (str.contains(".java") || str.contains(".py") || str.contains(".js")) {  
                String content = GitHubRepoContents.getFileContents(owner, repo, branch, str);
                batchFiles.add(content);
            }
        }

        List<String> documentList = new ArrayList<>();
        GPTClient gpt = new GPTClient();
        
        for (String x : batchFiles) {
            String res = gpt.getDoc(x);
            documentList.add(res);
        }

        // Generate PDF and return as response
        try {
            File pdfFile = generatePDF(documentList);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documentation.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/get-docsFileChanged")
    public ResponseEntity<InputStreamResource> getdocs(
            @RequestParam String changedFiles) {
        JSONArray fileContents = new JSONArray(changedFiles);
        List<String> batchFiles = new ArrayList<>();
        
        for (int i=0; i<fileContents.length(); i++) {
            String str = fileContents.getString(i);
            if (str.contains(".java") || str.contains(".py") || str.contains(".js")) {  
                String content = GitHubRepoContents.getFileContents(owner, repo, branch, str);
                batchFiles.add(content);
            }
        }

        List<String> documentList = new ArrayList<>();
        GPTClient gpt = new GPTClient();
        
        for (String x : batchFiles) {
            String res = gpt.getDoc(x);
            documentList.add(res);
        }

        // Generate PDF and return as response
        try {
            File pdfFile = generatePDF(documentList);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documentation.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    private File generatePDF(List<String> documentList) throws Exception {
        File pdfFile = File.createTempFile("documentation", ".pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 12);

        for (String doc : documentList) {
            Paragraph title = new Paragraph("Documentation Section", titleFont);
            title.setSpacingBefore(10);
            title.setSpacingAfter(5);
            document.add(title);

            Paragraph content = new Paragraph(doc, contentFont);
            document.add(content);

            document.add(new Paragraph("\n----------------------\n"));
        }

        document.close();
        return pdfFile;
    }

    

    
}
