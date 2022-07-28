package com.self.common.manager;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

public class CustomTransactionManager {

    private PlatformTransactionManager platformTransactionManager;

    public CustomTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    public TransactionManagerWrapper createTransactionManager(){
        return new TransactionManagerWrapper(platformTransactionManager);
    }

    public TransactionManagerWrapper createTransactionManager(Propagation propagation){
        return new TransactionManagerWrapper(platformTransactionManager, propagation);
    }

    public TransactionManagerWrapper createTransactionManager(Propagation propagation, Isolation isolation){
        return new TransactionManagerWrapper(platformTransactionManager, propagation, isolation);
    }

}
