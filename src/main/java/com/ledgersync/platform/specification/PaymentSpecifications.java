package com.ledgersync.platform.specification;

import com.ledgersync.platform.model.PaymentLog;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecifications {

    public static Specification<PaymentLog> withDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null)
                return cb.conjunction();
            if (startDate != null && endDate != null) {
                return cb.between(root.get("transactionDate"), startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay());
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate.atStartOfDay());
            }
            return cb.lessThan(root.get("transactionDate"), endDate.plusDays(1).atStartOfDay());
        };
    }

    public static Specification<PaymentLog> withGatewayReference(String gatewayReference) {
        return (root, query, cb) -> {
            if (gatewayReference == null || gatewayReference.isEmpty())
                return cb.conjunction();
            return cb.like(cb.lower(root.get("gatewayReference")), "%" + gatewayReference.toLowerCase() + "%");
        };
    }
}
