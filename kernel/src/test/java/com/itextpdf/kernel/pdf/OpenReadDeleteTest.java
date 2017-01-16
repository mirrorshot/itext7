package com.itextpdf.kernel.pdf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by Daniele Brambilla on 16/01/2017.
 */
@RunWith(JUnit4.class)
public class OpenReadDeleteTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfReaderTest/";
    private static final String workingFolder = "./target/temp/";
    private static final String sourceFile = "simpleCanvasWithFullCompression.pdf";

    @Test
    public void openReadDelete_filename() throws Exception {
        String fileNameIn = workingFolder + sourceFile;
        File fileSource = new File(sourceFolder + sourceFile);
        File fileIn = new File(fileNameIn);
        Files.copy(fileSource.toPath(), fileIn.toPath(), REPLACE_EXISTING);

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(fileNameIn));
        pdfDocument.checkIsoConformance();

        pdfDocument.close();

        Files.delete(fileIn.toPath());
    }
}
