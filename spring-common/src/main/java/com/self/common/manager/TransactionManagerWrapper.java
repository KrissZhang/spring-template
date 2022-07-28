package com.self.common.manager;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionManagerWrapper {

    private PlatformTransactionManager platformTransactionManager;

    private Propagation propagation;

    private Isolation isolation;

    public TransactionManagerWrapper(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
        this.propagation = Propagation.REQUIRED;
        this.isolation = Isolation.DEFAULT;
    }

    public TransactionManagerWrapper(PlatformTransactionManager platformTransactionManager, Propagation propagation) {
        this.platformTransactionManager = platformTransactionManager;
        this.propagation = propagation;
        this.isolation = Isolation.DEFAULT;
    }

    public TransactionManagerWrapper(PlatformTransactionManager platformTransactionManager, Propagation propagation, Isolation isolation) {
        this.platformTransactionManager = platformTransactionManager;
        this.propagation = propagation;
        this.isolation = isolation;
    }

    /**
     * 手动获取事务
     */
    public TransactionStatus getTransaction() throws TransactionException {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(this.propagation.value());
        definition.setIsolationLevel(this.isolation.value());

        return this.platformTransactionManager.getTransaction(definition);
    }

    public void commit(TransactionStatus transactionStatus) throws TransactionException {
        this.platformTransactionManager.commit(transactionStatus);
    }

    public void rollback(TransactionStatus transactionStatus) throws TransactionException {
        this.platformTransactionManager.rollback(transactionStatus);
    }

}
