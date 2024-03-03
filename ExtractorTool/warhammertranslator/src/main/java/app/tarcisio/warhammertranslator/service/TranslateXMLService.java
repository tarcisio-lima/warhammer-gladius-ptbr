package app.tarcisio.warhammertranslator.service;

import app.tarcisio.warhammertranslator.dto.Entry;
import app.tarcisio.warhammertranslator.dto.Language;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class TranslateXMLService {

    public String organizze(MultipartFile xmlFileToOrganizze) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(xmlFileToOrganizze.getInputStream()));
        var translatedLines = new ArrayList<String>();
        var untranslatedLines = new ArrayList<String>();

        for(String line; (line = reader.readLine()) != null; ) {

            if (
                    line.contains("<icon") ||
                    line.contains("<string") ||
                    line.contains("<control") ||
                    line.contains("<style")
            ) {
                untranslatedLines.add(line);
            } else if(line.contains("<br/>")) {
                line = replaceInvalidChars(line);
                line = line.replaceAll("<br/>", "-br-");
                translatedLines.add(line);
            } else {
                line = replaceInvalidChars(line);
                translatedLines.add(line);
            }
        }

        translatedLines.addAll(untranslatedLines);

        return aggregateList(translatedLines);
    }

    private String aggregateList(ArrayList<String> translatedLines) {

        StringBuilder builder = new StringBuilder();
        for (String line : translatedLines) {
            builder.append(line).append("\n");
        }

        return builder.toString();
    }

    public String extract(MultipartFile xmlFileToExtract) throws IOException {

        var xml = xmlFileToExtract.getInputStream();
        XmlMapper mapper = new XmlMapper();

        Language result = mapper.readValue(xml, Language.class);

        return aggregateTexts(result);
    }

    public Language combine(MultipartFile xmlFileToCombine, MultipartFile textToCombine) throws IOException {

        XmlMapper mapper = new XmlMapper();

        var xml = xmlFileToCombine.getInputStream();
        var textFile = textToCombine.getInputStream();

        Language xmlMapped = mapper.readValue(xml, Language.class);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(textFile));

        var lines = new ArrayList<String>();

        for(String line; (line = fileReader.readLine()) != null; ) {
            line = line.replaceAll("-br-", "<br/>");
            lines.add(line.substring(0, line.length()-1));
        }

        return combineTexts(xmlMapped.getEntries(), lines);
    }

    private String aggregateTexts(Language deserializedXml){
        StringBuilder builder = new StringBuilder();
        for (Entry entry : deserializedXml.getEntries()) {
            builder.append(entry.getValue()).append(";\n");
        }

        return builder.toString();
    }

    private Language combineTexts(List<Entry> entries, ArrayList<String> textsArray){
        for (int index = 0; index < entries.size(); index++){

            if (textsArray.get(index).startsWith(" ")){
                var text = textsArray.get(index).substring(1);
                entries.get(index).setValue(text);
            }else {
                entries.get(index).setValue(textsArray.get(index));
            }
        }

        var combinedLanguage = new Language();
        combinedLanguage.setEntries(entries);

        return combinedLanguage;
    }

    private String replaceInvalidChars(String line) {
        return line
                .replace('‘', '\'')
                .replace('’', '\'')
                .replace('“', '\'')
                .replace('”', '\'')
                .replace('—', '-')
                .replace("…", "...")
                .replace("&", " e ");
    }


}
