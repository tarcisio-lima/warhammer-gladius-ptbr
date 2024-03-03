package app.tarcisio.warhammertranslator.controller;

import app.tarcisio.warhammertranslator.dto.Language;
import app.tarcisio.warhammertranslator.service.TranslateXMLService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class XMLTranslateController {

    private final TranslateXMLService service;

    public XMLTranslateController(TranslateXMLService service){
        this.service = service;
    }

    @PostMapping(value = "/organizze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> organizze(
            @RequestParam("xmlFileToOrganizze") MultipartFile xmlFileToOrganizze
    ) throws IOException {

        var result = service.organizze(xmlFileToOrganizze);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> extract(
            @RequestParam("xmlFileToExtract") MultipartFile xmlFileToExtract
    ) throws IOException {

        var result = service.extract(xmlFileToExtract);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/combine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Language> combine(
            @RequestParam("xmlFileToCombine") MultipartFile xmlFileToCombine,
            @RequestParam("translatedText") MultipartFile translatedText
    ) throws IOException {

        var result = service.combine(xmlFileToCombine, translatedText);

        return ResponseEntity.ok(result);
    }

}
