package com.eteration.simplebanking.model.entity.transaction;

import com.eteration.simplebanking.model.entity.Account;
import com.eteration.simplebanking.model.entity.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transactions") // transaction is a reserved word in PostgreSQL, so table name is transactions
public abstract class Transaction extends Auditable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dtype", insertable = false, updatable = false)
    private String dType;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "amount")
    private double amount;

    @Column(name = "date")
    @CreatedDate
    private LocalDateTime date;

    public Transaction(double amount) {
        this.amount = amount;
        this.date = LocalDateTime.now();
    }

    public Transaction() {
        this.date = LocalDateTime.now();
    }

    public abstract void doTransaction();
}
