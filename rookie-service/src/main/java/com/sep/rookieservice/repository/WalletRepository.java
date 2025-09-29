package com.sep.rookieservice.repository;

import com.sep.rookieservice.model.UserAddress;
import com.sep.rookieservice.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
}
