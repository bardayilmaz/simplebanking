package com.eteration.simplebanking.repository;

import com.eteration.simplebanking.model.entity.transaction.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction, Long> {

}
