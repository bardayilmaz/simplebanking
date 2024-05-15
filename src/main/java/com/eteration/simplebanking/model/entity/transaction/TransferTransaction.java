package com.eteration.simplebanking.model.entity.transaction;

import com.eteration.simplebanking.model.exception.InsufficientBalanceException;
import com.eteration.simplebanking.model.entity.Account;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class TransferTransaction extends Transaction {

    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    public TransferTransaction(double amount, Account targetAccount) {
        super(amount);
        this.targetAccount = targetAccount;
    }

    public TransferTransaction() {
        super(0);
    }

    @Override
    public final void doTransaction() {
        try {
            getAccount().withdraw(getAmount());
            targetAccount.deposit(getAmount());
        } catch (InsufficientBalanceException e) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }
    }
}
