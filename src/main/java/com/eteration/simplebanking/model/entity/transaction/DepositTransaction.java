package com.eteration.simplebanking.model.entity.transaction;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@DiscriminatorValue("DepositTransaction")
public class DepositTransaction extends Transaction {

    public DepositTransaction(double amount) {
        super(amount);
    }

    public DepositTransaction() {
        super(0);
    }

    @Override
    public void doTransaction() {
        getAccount().deposit(getAmount());
    }

    @Override
    public String toString() {
        return "DepositTransaction {" +
                "id=" + getId() +
                ", account=" + getAccount() +
                ", amount=" + getAmount() +
                ", createdDate=" + getCreatedDate() +
                ", modifiedDate=" + getModifiedDate() +
                '}';
    }

}
