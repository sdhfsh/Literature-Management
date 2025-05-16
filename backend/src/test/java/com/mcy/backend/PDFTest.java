package com.mcy.backend;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class PDFTest {


    @Test
    void pdfTest() {
        try {
            PDDocument document = PDDocument.load(new File("E:\\TMP\\基于EndNote的文献管理系统评价流程优化策略.pdf"));
            PDDocumentInformation info = document.getDocumentInformation();
            System.out.println(info.getTitle());
            System.out.println(info.getAuthor());
            System.out.println(info.getKeywords());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String filePath = "E:\\TMP\\基于EndNote的文献管理系统评价流程优化策略.pdf"; // TODO: 换成上传的文件路径

        try (PDDocument document = PDDocument.load(new File(filePath))) {
            DocumentInfo docInfo = new DocumentInfo();

            // 1. 尝试读取Metadata
            PDDocumentInformation meta = document.getDocumentInformation();
            docInfo.setTitle(nonEmpty(meta.getTitle()));
            docInfo.setAuthor(nonEmpty(meta.getAuthor()));
            docInfo.setKeywords(nonEmpty(meta.getKeywords()));

            // 2. Metadata空的话，读取正文第一页
            if (isEmpty(docInfo.getTitle()) || isEmpty(docInfo.getAuthor())) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setStartPage(1);
                stripper.setEndPage(1);
                String firstPage = stripper.getText(document);

                parseFirstPage(firstPage, docInfo);
            }

            System.out.println("提取结果:");
            System.out.println(docInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
