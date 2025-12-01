package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContractRepository extends JpaRepository<Contract, String>, JpaSpecificationExecutor<Contract> {
    boolean existsByContractNumber(String contractNumber);
    boolean existsByUser_UserId(String userId);
}
