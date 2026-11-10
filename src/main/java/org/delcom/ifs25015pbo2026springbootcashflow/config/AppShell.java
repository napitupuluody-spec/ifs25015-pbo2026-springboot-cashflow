package org.delcom.ifs25015pbo2026springbootcashflow.config;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.aura.Aura;

/**
 * Application shell: mengisi title, meta description, dan viewport pada HTML awal
 * (sebelum JavaScript berjalan) agar lolos audit SEO dan aksesibilitas.
 * Tema Aura dimuat eksplisit: begitu ada AppShellConfigurator, Vaadin tidak lagi memuatnya otomatis.
 */
@StyleSheet(Aura.STYLESHEET)
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.setViewport("width=device-width, initial-scale=1");
        settings.setPageTitle("Cashflow - Catatan Keuangan Pribadi");
        settings.addMetaTag("description",
                "Aplikasi web pencatat pemasukan dan pengeluaran pribadi dengan perhitungan saldo otomatis.");
        settings.addMetaTag("robots", "index, follow");
        settings.addMetaTag("theme-color", "#ffffff");
    }
}
