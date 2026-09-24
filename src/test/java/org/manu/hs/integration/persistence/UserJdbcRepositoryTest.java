package org.manu.hs.integration.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserRepository;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Races on a user's row, made deterministic: a write based on a copy read before someone else's
 * change must not undo that change. Below the API, as no single request can be made to race.
 */
class UserJdbcRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("""
            Given a member's row read by a sign-in, and an admin who blocks the member right after
            When the sign-in saves its copy of the row
            Then the save is refused and the block stays: a stale copy never undoes a newer change
            """)
    void staleCopyCantUndoANewerChange() throws InterruptedException {
        // Given
        User readBySignIn = userRepository.findById(OTHER_USER).orElseThrow();
        Thread.sleep(5); // updated_at must move on, as it would between two requests
        jdbc.update("UPDATE app_user SET user_status = 'BLOCKED' WHERE id = ?", OTHER_USER);

        // When / Then
        assertThrows(OptimisticLockingFailureException.class,
                () -> userRepository.update(readBySignIn.toBuilder().displayName("Signed in again").build()));

        assertEquals(UserStatus.BLOCKED, userRepository.findById(OTHER_USER).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("""
            Given a member's row, read just now
            When a change based on it is saved
            Then it is stored, as nobody changed the row in between
            """)
    void freshCopyIsSaved() {
        // Given
        User fresh = userRepository.findById(OTHER_USER).orElseThrow();

        // When
        User saved = userRepository.update(fresh.toBuilder().displayName("New name").build());

        // Then
        assertEquals("New name", saved.getDisplayName());
    }

    @Test
    @DisplayName("""
            Given a transaction that counted the active admins, about to demote one
            When a second transaction counts them at the same time
            Then the second has to wait until the first ends: they can't both see two admins,
            each demote a different one, and leave none
            """)
    void countingTheActiveAdminsLocksThem() throws Exception {
        // Given
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        CountDownLatch counted = new CountDownLatch(1);
        CountDownLatch secondDone = new CountDownLatch(1);
        AtomicReference<Throwable> secondOutcome = new AtomicReference<>();

        // When
        transaction.executeWithoutResult(status -> {
            userRepository.countActiveAdminsLocking();
            counted.countDown();
            Thread second = new Thread(() -> {
                try {
                    transaction.executeWithoutResult(other -> {
                        jdbc.execute("SET LOCAL lock_timeout = '300ms'");
                        userRepository.countActiveAdminsLocking();
                    });
                } catch (Throwable outcome) {
                    secondOutcome.set(outcome);
                } finally {
                    secondDone.countDown();
                }
            });
            second.start();
            try {
                secondDone.await();
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
        });

        // Then
        // 55P03: PostgreSQL's "lock not available", here its lock timeout.
        SQLException timeout = assertInstanceOf(SQLException.class, secondOutcome.get().getCause());
        assertEquals("55P03", timeout.getSQLState());
    }
}
