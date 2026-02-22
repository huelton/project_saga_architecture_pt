package com.saga.orchestrator.repository;

import com.saga.orchestrator.constants.SagaConstants;
import com.saga.orchestrator.constants.TestConstants;
import com.saga.orchestrator.entity.SagaInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SagaInstanceRepositoryTest {

    @Autowired
    private SagaInstanceRepository repository;

    @Test
    void shouldSaveAndFindByTransferId() {
        SagaInstance instance = new SagaInstance();
        instance.setTransferId(TestConstants.TRANSFER_ID_1);
        instance.setCurrentState(SagaConstants.STATE_PENDING);
        repository.save(instance);
        Optional<SagaInstance> found = repository.findByTransferId(TestConstants.TRANSFER_ID_1);
        assertTrue(found.isPresent());
        assertEquals(TestConstants.TRANSFER_ID_1, found.get().getTransferId());
        assertEquals(SagaConstants.STATE_PENDING, found.get().getCurrentState());
    }
}
