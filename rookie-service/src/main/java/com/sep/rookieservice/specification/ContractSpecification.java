package com.sep.rookieservice.specification;

import com.sep.rookieservice.entity.Contract;
import com.sep.rookieservice.enums.ContractStatus;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.domain.Specification;

public final class ContractSpecification {

    public static Specification<Contract> buildSpecification(
            String keyword,
            ContractStatus status,
            IsActived isActived
    ) {
        Specification<Contract> spec = Specification.allOf();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String like = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("contractNumber")), like),
                            cb.like(cb.lower(root.get("title")), like),
                            cb.like(cb.lower(root.get("description")), like),
                            cb.like(cb.lower(root.get("note")), like)
                    )
            );
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (isActived != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActived"), isActived));
        }

        return spec;
    }
}
