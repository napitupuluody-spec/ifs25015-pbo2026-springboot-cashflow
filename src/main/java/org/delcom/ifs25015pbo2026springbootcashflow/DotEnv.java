package org.delcom.ifs25015pbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Memuat berkas {@code .env} (baris {@code KEY=VALUE}) ke system properties
 * sebelum Spring Boot dijalankan, sehingga {@code ${APP_PORT:8080}} pada
 * application.yaml dapat terisi. Variabel yang sudah ada (system property atau
 * environment) tidak ditimpa.
 */
public final class DotEnv {

    private DotEnv() {
    }

    /** Memuat {@code .env} dari direktori kerja. */
    public static void load() {
        load(Path.of(".env"));
    }

    /** Memuat berkas env tertentu; diam saja bila berkas tidak ada. */
    public static void load(Path file) {
        if (!Files.isRegularFile(file)) {
            return;
        }
        try {
            parse(Files.readAllLines(file)).forEach((key, value) -> {
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });
        } catch (IOException e) {
            System.err.println("[!] Gagal membaca " + file + ": " + e.getMessage());
        }
    }

    /** Mengubah baris-baris teks menjadi pasangan KEY=VALUE (komentar & baris rusak diabaikan). */
    static Map<String, String> parse(List<String> lines) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String raw : lines) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("export ")) {
                line = line.substring("export ".length()).strip();
            }
            int eq = line.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = line.substring(0, eq).strip();
            String value = line.substring(eq + 1).strip();
            if (value.length() >= 2) {
                char first = value.charAt(0);
                char last = value.charAt(value.length() - 1);
                if ((first == '"' || first == '\'') && first == last) {
                    value = value.substring(1, value.length() - 1);
                }
            }
            result.put(key, value);
        }
        return result;
    }
}
