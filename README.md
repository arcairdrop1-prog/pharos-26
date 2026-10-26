# Pharos Saga Executor Agent

An advanced agent developed in **Java** for the Pharos Agent Arena designed to execute multi-step dependent workflows (`approve → swap → deposit`) with robust retry policies for transient failures and clean compensation/rollback paths on permanent failures.

## 📋 Requirements Met

1. **Dependent Steps & Verification (Req. 01):** Implements at least 3 dependent steps (`Approve`, `Swap`, `Deposit`), each verified with receipt checks before the next step starts.
2. **Retry Policy (Req. 02):** Features a defined retry policy for transient failures such as gas spikes or dropped transactions.
3. **Compensation & Rollback Path (Req. 03):** Includes a defined compensation/rollback mechanism to prevent funds from being stranded halfway when a step permanently fails.
4. **State Log Tracking (Req. 04):** Maintains a detailed state log showing exactly which step the saga is in at any given time.
5. **Success & Failure Demos (Req. 05):** Demonstrates both a fully successful run and a forced-failure run with automatic recovery/rollback.

## 🚀 How to Run

1. Make sure you have **Java JDK** installed.
2. Compile the file:
   ```bash
   javac SagaExecutor.java
