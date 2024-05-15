package com.eteration.simplebanking.controller;

import com.eteration.simplebanking.model.entity.Account;
import com.eteration.simplebanking.model.exception.ExceptionModel;
import com.eteration.simplebanking.model.http.CreateAccountRequest;
import com.eteration.simplebanking.model.http.TransactionRequest;
import com.eteration.simplebanking.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTestIT {

    @Autowired
    protected TestRestTemplate restTemplate;

    private final AccountRepository accountRepository;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AccountControllerTestIT(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteAccount.sql")
    @Test
    void givenAccountRequest_whenAddAccount_thenStatus200() {
        // Given
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setOwnerName("Bülent Arda Yılmaz");
        createAccountRequest.setAccountNumber("1");
        // When
        ResponseEntity<Account> response = restTemplate.postForEntity("/account/v1", createAccountRequest, Account.class);
        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAccountNumber()).isNotNull();
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/insertAccount.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteAccount.sql")
    @Test
    void givenAccountRequestWithAlreadyExistsingAccountId_whenAddAccount_thenStatus404() {
        // Given
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setOwnerName("Bülent Arda Yılmaz");
        createAccountRequest.setAccountNumber("1");
        // When
        ResponseEntity<ExceptionModel> response = restTemplate.postForEntity("/account/v1", createAccountRequest, ExceptionModel.class);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("ALREADY_EXISTS");
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/insertAccount.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteAccount.sql")
    @Test
    void givenGetAccountWithExistingId_whenGetAccount_thenStatus200() {
        // Given
        // When
        ResponseEntity<Account> response = restTemplate.getForEntity("/account/v1/1", Account.class);
        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAccountNumber()).isNotNull();
    }

    @Test
    void givenGetAccountWithNonExistentId_whenGetAccount_thenStatus404() {
        // Given
        // When
        ResponseEntity<ExceptionModel> response = restTemplate.getForEntity("/account/v1/1", ExceptionModel.class);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("NOT_FOUND");
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/insertAccount.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteTransactions.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteAccount.sql")
    @Test
    void givenAccount_whenCredit_thenStatus200() {
        // Given
        TransactionRequest transactionRequest = new TransactionRequest(100D);
        // When
        ResponseEntity<TransactionStatus> response = restTemplate.postForEntity("/account/v1/credit/1", transactionRequest, TransactionStatus.class);
        // Then
        Account account = accountRepository.findById("1").orElse(null);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("OK");
        assertThat(account.getBalance()).isEqualTo(100D);
    }

    @Test
    void givenNonExistentAccountId_whenCredit_thenStatus404() {
        // Given
        TransactionRequest transactionRequest = new TransactionRequest(100D);
        // When
        ResponseEntity<ExceptionModel> response = restTemplate.postForEntity("/account/v1/credit/1", transactionRequest, ExceptionModel.class);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("NOT_FOUND");
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/insertAccountWithInitialBalance.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteTransactions.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteAccount.sql")
    @Test
    void givenAccount_whenDebit_thenStatus200() {
        // Given
        TransactionRequest transactionRequest = new TransactionRequest(100D);
        // When
        ResponseEntity<TransactionStatus> response = restTemplate.postForEntity("/account/v1/debit/1", transactionRequest, TransactionStatus.class);
        // Then
        Account account = accountRepository.findById("1").orElse(null);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("OK");
        assertThat(account.getBalance()).isEqualTo(0D);
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/insertAccount.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteAccount.sql")
    @Test
    void givenAccountWithInsufficientBalance_whenDebit_thenStatus400() {
        // Given
        TransactionRequest transactionRequest = new TransactionRequest(100D);
        // When
        ResponseEntity<ExceptionModel> response = restTemplate.postForEntity("/account/v1/debit/1", transactionRequest, ExceptionModel.class);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INSUFFICIENT_BALANCE");
    }

    @Test
    void givenNonExistentAccountId_whenDebit_thenStatus404() {
        // Given
        TransactionRequest transactionRequest = new TransactionRequest(100D);
        // When
        ResponseEntity<ExceptionModel> response = restTemplate.postForEntity("/account/v1/debit/1", transactionRequest, ExceptionModel.class);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("NOT_FOUND");
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/insertAccountsForTransfer.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteTransactions.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteAccount.sql")
    @Test
    void givenAccounts_whenSendMoney_thenStatus200() {
        // Given
        TransactionRequest transactionRequest = new TransactionRequest(100D);
        // When
        ResponseEntity<TransactionStatus> response = restTemplate.postForEntity("/account/v1/send-from/1/to/0", transactionRequest, TransactionStatus.class);
        // Then
        Account sender = accountRepository.findById("1").orElse(null);
        Account receiver = accountRepository.findById("0").orElse(null);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("OK");
        assertThat(sender.getBalance()).isEqualTo(0D);
        assertThat(receiver.getBalance()).isEqualTo(100D);
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/insertAccountsForTransferWithInsufficientBalance.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteTransactions.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/deleteAccount.sql")
    @Test
    void givenAccountsWithInsufficientBalance_whenSendMoney_thenStatus400() {
        // Given
        TransactionRequest transactionRequest = new TransactionRequest(100D);
        // When
        ResponseEntity<ExceptionModel> response = restTemplate.postForEntity("/account/v1/send-from/1/to/0", transactionRequest, ExceptionModel.class);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INSUFFICIENT_BALANCE");
    }

    @Test
    void givenNonExistentAccounts_whenSendMoney_thenStatus404() {
        // Given
        TransactionRequest transactionRequest = new TransactionRequest(100D);
        // When
        ResponseEntity<ExceptionModel> response = restTemplate.postForEntity("/account/v1/send-from/1/to/0", transactionRequest, ExceptionModel.class);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("NOT_FOUND");
    }
}
