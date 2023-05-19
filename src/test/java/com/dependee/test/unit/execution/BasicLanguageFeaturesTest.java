package com.dependee.test.unit.execution;

import com.deependee.parser.DeependeeParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class BasicLanguageFeaturesTest {

    @Test
    public void testArrays() throws Exception {
        List<Object> parsingResult = parseFile("deependee/arrays.dep");
        Assertions.assertEquals(22, parsingResult.size());
    }

    @Test
    public void testNumbers() throws Exception {
        List<Object> parsingResult = parseFile("deependee/numbers.dep");
        Assertions.assertEquals(12, parsingResult.size());
    }

    @Test
    public void testStrings() throws Exception {
        List<Object> parsingResult = parseFile("deependee/strings.dep");
        Assertions.assertEquals(10, parsingResult.size());
    }

    public List<Object> parseFile(String filename) throws Exception {
        DeependeeParser parser = new DeependeeParser();

        InputStream inputStream = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(filename);
        Assertions.assertNotNull(inputStream);
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder sb = new StringBuilder();
        for (String line; (line = reader.readLine()) != null;) {
            //TODO: work out why we need to skip empty lines
            //      the parser should be able to handle them properly
            if (!"".equals(line.trim()))
                sb.append(line).append("\n");
        }
        List<Object> parsingResult = parser.parse(sb.toString());
        Assertions.assertNotNull(parsingResult);

        return parsingResult;
    }
}
