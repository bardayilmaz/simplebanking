package com.eteration.simplebanking.model.entity.transaction;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@DiscriminatorValue("WithdrawTransaction")
public class WithdrawalTransaction extends Transaction {

    public WithdrawalTransaction(double amount) {
        super(amount);
    }

    public WithdrawalTransaction() {
        super(0);
    }

    @Override
    public void doTransaction() {
        getAccount().withdraw(getAmount());
    }

    @Override
    public String toString() {
        return "WithdrawTransaction {" +
                "id=" + getId() +
                ", account=" + getAccount() +
                ", amount=" + getAmount() +
                ", createdDate=" + getCreatedDate() +
                ", modifiedDate=" + getModifiedDate() +
                '}';
    }
}
