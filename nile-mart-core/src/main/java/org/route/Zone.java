package org.route;

import java.math.BigDecimal;

public enum Zone {
    GREATER_CAIRO(BigDecimal.valueOf(50), true),
    ALEX_DELTA(BigDecimal.valueOf(65), true),
    CANAL(BigDecimal.valueOf(70), true),
    UPPER_EGYPT(BigDecimal.valueOf(85), true),
    REMOTE(BigDecimal.valueOf(120), false);

    private final BigDecimal fee;
    private final boolean freeShipping;

    Zone(BigDecimal fee, boolean freeShipping) {
        this.fee = fee;
        this.freeShipping = freeShipping;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }
}
