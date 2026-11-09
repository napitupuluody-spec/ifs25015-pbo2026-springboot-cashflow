package org.delcom.ifs25015pbo2026springbootcashflow;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DotEnvTest {

    @Test
    void parseIgnoresCommentsBlankAndBrokenLines() {
        Map<String, String> result = DotEnv.parse(List.of(
                "# komentar", "", "APP_PORT=9090", "  NAME = \"Cash Flow\" ", "export X='y'", "rusak", "=kosong"));
        assertEquals(3, result.size());
        assertEquals("9090", result.get("APP_PORT"));
        assertEquals("Cash Flow", result.get("NAME"));
        assertEquals("y", result.get("X"));
    }

    @Test
    void loadSetsSystemPropertyButDoesNotOverrideExisting() throws Exception {
        Path file = Files.createTempFile("cashflow", ".env");
        String key = "CASHFLOW_TEST_KEY";
        System.clearProperty(key);
        try {
            Files.writeString(file, key + "=dari-file\n");
            DotEnv.load(file);
            assertEquals("dari-file", System.getProperty(key));

            Files.writeString(file, key + "=timpa\n");
            DotEnv.load(file);
            assertEquals("dari-file", System.getProperty(key));
        } finally {
            System.clearProperty(key);
            Files.deleteIfExists(file);
        }
    }

    @Test
    void loadMissingFileIsSilent() {
        DotEnv.load(Path.of("tidak-ada.env"));
        assertNull(System.getProperty("tidak-ada"));
    }
}
