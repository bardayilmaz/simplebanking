package com.eteration.simplebanking.model.entity.transaction;

import com.eteration.simplebanking.model.entity.Account;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("BillPaymentTransaction")
public class BillPaymentTransaction extends TransferTransaction {

    public BillPaymentTransaction(double amount, Account targetAccount) {
        super(amount, targetAccount);
    }

    public BillPaymentTransaction() {
        super();
    }

    @Override
    public String toString() {
        return "BillPaymentTransaction {" +
                "id=" + getId() +
                ", account=" + getAccount() +
                ", targetAccount=" + getTargetAccount() +
                ", amount=" + getAmount() +
                ", createdDate=" + getCreatedDate() +
                ", modifiedDate=" + getModifiedDate() +
                ", dType=" + getDType() +
                '}';
    }
}
