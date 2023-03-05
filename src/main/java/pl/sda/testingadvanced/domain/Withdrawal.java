package pl.sda.testingadvanced.domain;

public final class Withdrawal {
    private Withdrawal() {
    }
    public static final Double FIXED_WITHDRAWAL_FEE_PERCENT = 1.5;

    static Double calculateWithdrawalFeeAmount(Double amount) {
        return amount * FIXED_WITHDRAWAL_FEE_PERCENT / 100;
    }
}
