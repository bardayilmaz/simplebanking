package com.eteration.simplebanking.service;

import com.eteration.simplebanking.controller.TransactionStatus;
import com.eteration.simplebanking.model.exception.AlreadyExistsException;
import com.eteration.simplebanking.model.exception.NotFoundException;
import com.eteration.simplebanking.model.entity.Account;
import com.eteration.simplebanking.model.entity.transaction.Transaction;
import com.eteration.simplebanking.model.entity.transaction.TransferTransaction;
import com.eteration.simplebanking.model.http.CreateAccountRequest;
import com.eteration.simplebanking.model.http.TransactionRequest;
import com.eteration.simplebanking.repository.AccountRepository;
import com.eteration.simplebanking.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found!"));
    }

    public ResponseEntity<Account> createAccount(CreateAccountRequest createAccountRequest) {
        if (accountRepository.existsById(createAccountRequest.getAccountNumber())) {
            throw new AlreadyExistsException("Account already exists!");
        }
        Account account = new Account(createAccountRequest.getOwnerName(), createAccountRequest.getAccountNumber());
        account = accountRepository.save(account);
        return ResponseEntity.ok(account);
    }

    public ResponseEntity<TransactionStatus> sendMoneyFromTo(String fromAccountId, String toAccountId, TransactionRequest transactionRequest) {
        Account fromAccount = findAccount(fromAccountId);
        Account toAccount = findAccount(toAccountId);
        Transaction transaction = new TransferTransaction(transactionRequest.getAmount(), toAccount);
        fromAccount.post(transaction);
        transactionRepository.save(transaction);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        return ResponseEntity.ok(new TransactionStatus(TransactionStatus.OK));
    }
}
