package com.eteration.simplebanking.controller;

import com.eteration.simplebanking.model.entity.Account;
import com.eteration.simplebanking.model.entity.transaction.DepositTransaction;
import com.eteration.simplebanking.model.entity.transaction.Transaction;
import com.eteration.simplebanking.model.entity.transaction.WithdrawalTransaction;
import com.eteration.simplebanking.model.http.CreateAccountRequest;
import com.eteration.simplebanking.model.http.TransactionRequest;
import com.eteration.simplebanking.repository.AccountRepository;
import com.eteration.simplebanking.repository.TransactionRepository;
import com.eteration.simplebanking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/account/v1")
@Transactional
public class AccountController {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Autowired
    public AccountController(TransactionRepository transactionRepository, AccountRepository accountRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @PostMapping("/credit/{accountNumber}")
    public ResponseEntity<TransactionStatus> creditPost(@PathVariable(name = "accountNumber") String accountNumber,
                                                        @RequestBody @Valid TransactionRequest transactionRequest) {
        Transaction transaction = new DepositTransaction(transactionRequest.getAmount());
        transactionRepository.save(transaction);
        return credit(accountNumber, transaction);
    }

    public ResponseEntity<TransactionStatus> credit(String accountNumber, Transaction transaction) {
        return handleOneWayTransaction(accountNumber, transaction);
    }

    @PostMapping("/debit/{accountNumber}")
    public ResponseEntity<TransactionStatus> debitPost(@PathVariable(name = "accountNumber") String accountNumber,
                                                       @RequestBody @Valid TransactionRequest transactionRequest) {
        Transaction transaction = new WithdrawalTransaction(transactionRequest.getAmount());
        transactionRepository.save(transaction);
        return debit(accountNumber, transaction);
    }

    public ResponseEntity<TransactionStatus> debit(String accountNumber, Transaction transaction) {
        return handleOneWayTransaction(accountNumber, transaction);
    }

    private TransactionStatus saveTransaction(Account account, Transaction transaction) {
        account.post(transaction);
        TransactionStatus transactionStatus = new TransactionStatus(TransactionStatus.OK);
        accountRepository.save(account);
        transactionRepository.save(transaction);
        return transactionStatus;
    }

    private ResponseEntity<TransactionStatus> handleOneWayTransaction(String accountNumber, Transaction transaction) {
        Account account = accountService.findAccount(accountNumber);
        TransactionStatus transactionStatus = saveTransaction(account, transaction);
        if (transactionStatus.getStatus().equals(TransactionStatus.OK)) {
            return ResponseEntity.ok(transactionStatus);
        } else {
            return ResponseEntity.badRequest().body(transactionStatus);
        }
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody @Valid CreateAccountRequest createAccountRequest) {
        return accountService.createAccount(createAccountRequest);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable(name = "accountNumber") String accountNumber) {
        return ResponseEntity.ok(accountService.findAccount(accountNumber));
    }

    @PostMapping("/send-from/{fromAccountNumber}/to/{toAccountNumber}")
    public ResponseEntity<TransactionStatus> sendMoney(@PathVariable(name = "fromAccountNumber") String fromAccountNumber,
                                                       @PathVariable(name = "toAccountNumber") String toAccountNumber,
                                                       @RequestBody @Valid TransactionRequest transactionRequest) {
       return accountService.sendMoneyFromTo(fromAccountNumber, toAccountNumber, transactionRequest);
    }
}