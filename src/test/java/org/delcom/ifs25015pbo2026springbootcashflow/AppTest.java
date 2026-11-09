package org.delcom.ifs25015pbo2026springbootcashflow;

import org.delcom.ifs25015pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25015pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25015pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/** Smoke test: wiring bean Clean Architecture & delegasi App.main ke SpringApplication.run. */
@SpringBootTest
class AppTest {

    @Autowired
    private ITransactionRepository repository;

    @Autowired
    private FinanceUseCase useCase;

    @Autowired
    private FinancePresenter presenter;

    @Test
    void contextLoadsAndBeansAreWired() {
        assertNotNull(useCase);
        assertNotNull(presenter);
        assertInstanceOf(TransactionRepository.class, repository);
    }

    @Test
    void useCaseWorksThroughWiredRepository() throws Exception {
        BigDecimal before = useCase.getBalance();
        useCase.addIncome("Smoke test", "100.00");
        assertEquals(0, before.add(new BigDecimal("100.00")).compareTo(useCase.getBalance()));
    }

    @Test
    void mainDelegatesToSpringApplicationRun() throws Exception {
        try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
            spring.when(() -> SpringApplication.run(eq(App.class), any(String[].class)))
                    .thenReturn(mock(ConfigurableApplicationContext.class));

            App.main(new String[0]);

            spring.verify(() -> SpringApplication.run(eq(App.class), any(String[].class)));
        }
    }
}
