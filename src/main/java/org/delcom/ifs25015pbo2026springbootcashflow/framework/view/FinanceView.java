package org.delcom.ifs25015pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25015pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25015pbo2026springbootcashflow.usecase.FinanceUseCase;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Tampilan web Cashflow. Semua aturan bisnis didelegasikan ke {@link FinanceUseCase};
 * semua teks tampil berasal dari {@link FinancePresenter}. View hanya merakit komponen
 * dan menangkap exception domain menjadi notifikasi.
 */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {

    private static final String COLOR_INCOME = "#16A34A";
    private static final String COLOR_EXPENSE = "#DC2626";

    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<SortOption> sortBox = new ComboBox<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span();

    private final Span totalCount = new Span("0");
    private final Span incomeTotal = new Span("Rp 0.00");
    private final Span expenseTotal = new Span("Rp 0.00");
    private final Span balanceValue = new Span("Rp 0.00");

    /** Kata kunci pencarian & opsi urut yang sedang aktif. */
    private String keyword = "";
    private SortOption sortOption = null;

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        add(new H1("Cashflow"), buildSummaryCards(), buildToolbar(), statusLabel, grid);
        reload();
    }

    // ---------- ringkasan ----------

    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout cards = new HorizontalLayout(
                summaryCard("Jumlah Transaksi", totalCount, "#DBEAFE", "#1D4ED8"),
                summaryCard("Total Pemasukan", incomeTotal, "#DCFCE7", COLOR_INCOME),
                summaryCard("Total Pengeluaran", expenseTotal, "#FEE2E2", COLOR_EXPENSE),
                summaryCard("Saldo", balanceValue, "#FEF3C7", "#D97706"));
        cards.setWidthFull();
        cards.getStyle().set("align-items", "stretch");
        return cards;
    }

    private Div summaryCard(String label, Span value, String background, String accent) {
        Span caption = new Span(label);
        caption.getStyle().set("display", "block").set("font-size", "0.875rem")
                .set("font-weight", "500").set("color", accent);
        value.getStyle().set("display", "block").set("margin-top", "0.35rem")
                .set("font-size", "1.6rem").set("font-weight", "700").set("color", accent);

        Div card = new Div(caption, value);
        card.getStyle().set("flex", "1").set("min-width", "0").set("background", background)
                .set("border-left", "6px solid " + accent).set("border-radius", "12px")
                .set("padding", "1rem 1.1rem").set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        BigDecimal balance = useCase.getBalance();
        totalCount.setText(String.valueOf(useCase.getAllTransactions().size()));
        incomeTotal.setText(presenter.formatMoney(useCase.getTotalIncome()));
        expenseTotal.setText(presenter.formatMoney(useCase.getTotalExpense()));
        balanceValue.setText(presenter.formatMoney(balance));
        balanceValue.getStyle().set("color", balance.signum() < 0 ? COLOR_EXPENSE : "#92400E");
    }

    // ---------- toolbar ----------

    private HorizontalLayout buildToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.addKeyDownListener(Key.ENTER, event -> applySearch());

        sortBox.setItems(SortOption.values());
        sortBox.setItemLabelGenerator(presenter::sortLabel);
        sortBox.setPlaceholder("Urutkan...");
        sortBox.setClearButtonVisible(true);
        sortBox.addValueChangeListener(event -> {
            sortOption = event.getValue();
            reload();
        });

        Button searchButton = new Button("Cari", VaadinIcon.SEARCH.create(), e -> applySearch());
        Button resetButton = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), e -> resetFilters());

        Button incomeButton = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                e -> openAddDialog(TransactionType.INCOME));
        incomeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        Button expenseButton = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                e -> openAddDialog(TransactionType.EXPENSE));
        expenseButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField, searchButton, sortBox, resetButton, incomeButton, expenseButton);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        return toolbar;
    }

    private void applySearch() {
        keyword = searchField.getValue() == null ? "" : searchField.getValue().strip();
        reload();
    }

    private void resetFilters() {
        keyword = "";
        sortOption = null;
        searchField.clear();
        sortBox.clear();
        reload();
    }

    // ---------- grid ----------

    private void configureGrid() {
        grid.addColumn(Transaction::getId).setHeader("ID").setKey("id")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true).setComparator(Comparator.comparingInt(Transaction::getId));

        grid.addColumn(Transaction::getDescription).setHeader("Keterangan").setKey("description")
                .setFlexGrow(1)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));

        grid.addColumn(t -> presenter.formatMoney(t.getAmount())).setHeader("Jumlah").setKey("amount")
                .setAutoWidth(true).setTextAlign(ColumnTextAlign.END)
                .setSortable(true).setComparator(Comparator.comparing(Transaction::getAmount));

        grid.addComponentColumn(this::typeBadge).setHeader("Tipe").setKey("type")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true).setComparator(Comparator.comparing(Transaction::getType));

        grid.addColumn(presenter::formatCreatedAt).setHeader("Waktu").setKey("createdAt")
                .setAutoWidth(true)
                .setSortable(true).setComparator(Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::actionButtons).setHeader("Tindakan").setKey("actions")
                .setAutoWidth(true).setFlexGrow(0);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private Span typeBadge(Transaction transaction) {
        Span badge = new Span(presenter.formatType(transaction.getType()));
        String color = transaction.isIncome() ? COLOR_INCOME : COLOR_EXPENSE;
        badge.getStyle().set("color", color).set("font-weight", "600");
        return badge;
    }

    private HorizontalLayout actionButtons(Transaction transaction) {
        Button delete = new Button("Hapus", VaadinIcon.TRASH.create(), e -> openDeleteDialog(transaction));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(delete);
    }

    private void reload() {
        List<Transaction> rows = useCase.listTransactions(keyword, sortOption);
        grid.setItems(rows);
        emptyState.setText(presenter.emptyMessage(!keyword.isBlank()));
        updateSummary();
        statusLabel.setText(presenter.listHeader(keyword) + " — "
                + presenter.formatBalance(useCase.getBalance()));
    }

    // ---------- dialog ----------

    private void openAddDialog(TransactionType type) {
        boolean income = type == TransactionType.INCOME;

        TextField descriptionField = new TextField("Keterangan");
        descriptionField.setWidthFull();
        TextField amountField = new TextField("Jumlah (Rp)");
        amountField.setPlaceholder("contoh: 150000");
        amountField.setWidthFull();

        VerticalLayout form = new VerticalLayout(descriptionField, amountField);
        form.setPadding(false);

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(income ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.setWidth("440px");
        dialog.setMaxWidth("95vw");
        dialog.add(form);

        Button save = new Button("Simpan", VaadinIcon.CHECK.create(), e -> {
            try {
                Transaction created = income
                        ? useCase.addIncome(descriptionField.getValue(), amountField.getValue())
                        : useCase.addExpense(descriptionField.getValue(), amountField.getValue());
                showSuccess(income
                        ? presenter.showAddIncomeSuccess(created)
                        : presenter.showAddExpenseSuccess(created));
                dialog.close();
                reload();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());

        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private void openDeleteDialog(Transaction transaction) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Hapus Transaksi");
        dialog.add(new Paragraph("Yakin ingin menghapus " + presenter.format(transaction) + " ?"));

        Button confirm = new Button("Hapus", VaadinIcon.TRASH.create(), e -> {
            deleteTransaction(transaction);
            dialog.close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());

        dialog.getFooter().add(cancel, confirm);
        dialog.open();
    }

    private void deleteTransaction(Transaction transaction) {
        try {
            useCase.removeTransaction(transaction.getId());
            showSuccess(presenter.showRemoveSuccess());
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
        } catch (ValidationException ex) {
            showError(presenter.showValidationError(ex.getMessage()));
        }
        reload();
    }

    // ---------- notifikasi ----------

    private void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification.show(message, 4000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
