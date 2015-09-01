package com.itextpdf.forms;

import com.itextpdf.core.pdf.IPdfPageExtraCopier;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.forms.fields.PdfFormField;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PdfPageFormCopier implements IPdfPageExtraCopier {

    PdfAcroForm formFrom;
    PdfAcroForm formTo;
    PdfDocument documentFrom;
    PdfDocument documentTo;

    @Override
    public void copy(PdfPage fromPage, PdfPage toPage) {
        if (documentFrom != fromPage.getDocument()) {
            documentFrom = fromPage.getDocument();
            formFrom = PdfAcroForm.getAcroForm(documentFrom, false);
        }
        if (documentTo != toPage.getDocument()) {
            documentTo = toPage.getDocument();
            formTo = PdfAcroForm.getAcroForm(documentTo, true);
            if (formFrom != null) {
                //duplicate AcroForm dictionary
                List<PdfName> excludedKeys = new ArrayList<>();
                excludedKeys.add(PdfName.Fields);
                excludedKeys.add(PdfName.DR);
                PdfDictionary dict = formFrom.getPdfObject().copyToDocument(documentTo, excludedKeys, false);
                formTo.getPdfObject().mergeDifferent(dict);
            }
        }

        List<PdfDictionary> usedParents = new ArrayList<>();
        if (formFrom != null) {
            LinkedHashMap<String, PdfFormField> fieldsFrom = formFrom.getFormFields();
            if (!fieldsFrom.isEmpty()) {
                LinkedHashMap<String, PdfFormField> fieldsTo = formTo.getFormFields();
                List<PdfAnnotation> annots = toPage.getAnnotations();
                for (PdfAnnotation annot : annots) {
                    if (annot.getSubtype().equals(PdfName.Widget)) {
                        PdfDictionary parent = annot.getPdfObject().getAsDictionary(PdfName.Parent);
                        if (parent != null) {
                            PdfString parentName = parent.getAsString(PdfName.T);
                            if (parentName == null || !usedParents.contains(parent)) {
                                PdfFormField field = PdfFormField.makeFormField(parent, toPage.getDocument());
                                formTo.addField(field, toPage);
                                usedParents.add(parent);
                            }
                        } else {
                            PdfString annotName = annot.getPdfObject().getAsString(PdfName.T);
                            String annotNameString = null;
                            if (annotName != null) {
                                annotNameString = annotName.toUnicodeString();
                            }
                            if (annotNameString != null && fieldsFrom.containsKey(annotNameString)) {
                                PdfFormField field = PdfFormField.makeFormField(annot.getPdfObject(), toPage.getDocument());
                                if (fieldsTo.containsKey(annotNameString)) {
                                    while (fieldsTo.containsKey(annotNameString)) {
                                        annotNameString += "_1";
                                        field.setFieldName(annotNameString);
                                    }

                                }
                                formTo.addField(field, null);
                            }
                        }
                    }
                }
            }

        }
    }

}
