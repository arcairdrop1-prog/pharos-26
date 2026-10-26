import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SagaExecutor {

    public enum SagaState {
        IDLE, STEP_1_APPROVING, STEP_2_SWAPPING, STEP_3_DEPOSITING, COMPLETED, FAILED, ROLLED_BACK
    }

    public static class SagaLog {
        LocalDateTime timestamp;
        SagaState state;
        String message;

        public SagaLog(SagaState state, String message) {
            this.timestamp = LocalDateTime.now();
            this.state = state;
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("[%s] State: %s | %s",
                    timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")), state, message);
        }
    }

    private SagaState currentState = SagaState.IDLE;
    private final List<SagaLog> stateLogs = new ArrayList<>();

    private void updateState(SagaState newState, String message) {
        this.currentState = newState;
        SagaLog log = new SagaLog(newState, message);
        stateLogs.add(log);
        System.out.println(log);
    }

    public void printStateLog() {
        System.out.println("\n--- SAGA EXECUTION STATE LOG ---");
        for (SagaLog log : stateLogs) {
            System.out.println(log);
        }
    }

    // Step 1: Approve Token Allowance (Requirement 01)
    private boolean executeStep1Approve(boolean simulateTransientFailure, boolean simulatePermanentFailure) {
        updateState(SagaState.STEP_1_APPROVING, "Starting Step 1: Approving token allowance...");
        
        // Requirement 02: Defined retry policy for transient failures (e.g. gas spike / dropped tx)
        int attempts = 0;
        while (attempts < 3) {
            attempts++;
            if (simulateTransientFailure && attempts == 1) {
                System.out.println("⚠️ Transient error encountered (Gas spike / dropped tx). Retrying attempt " + (attempts + 1) + "...");
                continue;
            }
            break;
        }

        if (simulatePermanentFailure) {
            System.out.println("❌ Step 1 permanently failed.");
            return false;
        }

        System.out.println("✅ Step 1 verified: Approval receipt confirmed (TxHash: 0xappr123).");
        return true;
    }

    // Step 2: Token Swap (Requirement 01)
    private boolean executeStep2Swap(boolean simulatePermanentFailure) {
        updateState(SagaState.STEP_2_SWAPPING, "Starting Step 2: Executing token swap...");
        
        if (simulatePermanentFailure) {
            System.out.println("❌ Step 2 permanently failed during swap execution.");
            return false;
        }

        System.out.println("✅ Step 2 verified: Swap receipt confirmed (TxHash: 0xswap456).");
        return true;
    }

    // Step 3: Vault Deposit (Requirement 01)
    private boolean executeStep3Deposit(boolean simulatePermanentFailure) {
        updateState(SagaState.STEP_3_DEPOSITING, "Starting Step 3: Depositing funds into vault...");
        
        if (simulatePermanentFailure) {
            System.out.println("❌ Step 3 permanently failed during deposit.");
            return false;
        }

        System.out.println("✅ Step 3 verified: Deposit receipt confirmed (TxHash: 0xdep789).");
        return true;
    }

    // Requirement 03: Defined compensation/rollback path when a step permanently fails
    private void rollbackSaga(int failedStep) {
        System.out.println("\n🔄 Triggering Compensation / Rollback Path...");
        if (failedStep >= 2) {
            System.out.println(" - Reverting Step 2 Swap: Swapping back tokens to original asset...");
        }
        if (failedStep >= 1) {
            System.out.println(" - Reverting Step 1 Approval: Revoking token allowance...");
        }
        updateState(SagaState.ROLLED_BACK, "Saga successfully rolled back and funds secured.");
    }

    // Execute full saga workflow
    public boolean executeSaga(boolean transientFail, boolean failAtStep2) {
        stateLogs.clear();
        System.out.println("=== STARTING SAGA WORKFLOW ===");

        // Step 1
        boolean step1Success = executeStep1Approve(transientFail, false);
        if (!step1Success) {
            rollbackSaga(0);
            updateState(SagaState.FAILED, "Saga failed at Step 1.");
            return false;
        }

        // Step 2
        boolean step2Success = executeStep2Swap(failAtStep2);
        if (!step2Success) {
            rollbackSaga(1);
            updateState(SagaState.FAILED, "Saga failed at Step 2. Rollback executed.");
            return false;
        }

        // Step 3
        boolean step3Success = executeStep3Deposit(false);
        if (!step3Success) {
            rollbackSaga(2);
            updateState(SagaState.FAILED, "Saga failed at Step 3. Rollback executed.");
            return false;
        }

        updateState(SagaState.COMPLETED, "All 3 dependent steps completed successfully!");
        return true;
    }

    public static void main(String[] args) {
        SagaExecutor executor = new SagaExecutor();

        // Requirement 05: Shows one fully successful run
        System.out.println("--- RUN 1: Fully Successful Run (with Transient Retry) ---");
        executor.executeSaga(true, false);
        executor.printStateLog();

        // Requirement 05: One forced-failure run with recovery/rollback
        System.out.println("\n\n--- RUN 2: Forced-Failure Run with Rollback / Recovery ---");
        executor.executeSaga(false, true);
        executor.printStateLog();
    }
}
