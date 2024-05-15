package com.eteration.simplebanking.model.entity;

import com.eteration.simplebanking.model.exception.InsufficientBalanceException;
import com.eteration.simplebanking.model.entity.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account extends Auditable {

    @Id
    @Column(name = "id")
    private String accountNumber;

    @Column(name = "owner")
    private String owner;

    @Column(name = "balance")
    private double balance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public Account(String owner, String accountNumber) {
        this.owner = owner;
        this.accountNumber = accountNumber;
        this.balance = 0;
        this.transactions = new ArrayList<>();
    }

    public Account() {

    }

    public void post(Transaction transaction) {
        if (this.transactions == null) {
            this.transactions = new ArrayList<>();
        }
        transaction.setAccount(this);
        this.transactions.add(transaction);
        transaction.doTransaction();
    }

    public void withdraw(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new InsufficientBalanceException("Insufficient Balance!");
        }
    }

    public void deposit(double amount) {
        this.balance += amount;
    }
}
