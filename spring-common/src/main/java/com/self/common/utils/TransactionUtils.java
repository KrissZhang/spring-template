package com.self.common.utils;

import com.self.common.lambda.TransAction;
import com.self.common.manager.CustomTransactionManager;
import com.self.common.manager.TransactionManagerWrapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

public class TransactionUtils {

    private TransactionUtils() {
    }

    private static volatile CustomTransactionManager customTransactionManager;

    private static CustomTransactionManager getCustomTransactionManager(){
        if (customTransactionManager == null) {
            synchronized(TransactionUtils.class) {
                if (customTransactionManager == null) {
                    PlatformTransactionManager platformTransactionManager = SpringUtils.getBean(PlatformTransactionManager.class);
                    customTransactionManager = new CustomTransactionManager(platformTransactionManager);
                }
            }
        }

        return customTransactionManager;
    }

    /**
     * 事务操作数据库
     * 传播行为：默认 Propagation.REQUIRED --- 当前如果有事务，Spring就会使用该事务，否则会开始一个新事务
     * 隔离级别：默认 Isolation.DEFAULT --- PlatformTransactionManager 的默认隔离级别（对大多数数据库来说就是 ISOLATION.READ_COMMITTED）
     */
    public static void beginTransaction(TransAction action) {
        beginTransaction(Propagation.REQUIRED, Isolation.DEFAULT, action);
    }

    /**
     * @param propagation Spring事务传播行为
     *                    Propagation.REQUIRED --- 当前如果有事务，Spring就会使用该事务，否则会开始一个新事务
     *                    Propagation.SUPPORTS --- 当前如果有事务，Spring就会使用该事务，否则不会开始一个新事务
     *                    Propagation.MANDATORY --- 当前如果有事务，Spring就会使用该事务，否则会抛出异常
     *                    Propagation.REQUIRES_NEW --- Spring总是开始一个新事务，如果当前有事务，则该事务挂起
     *                    Propagation.NOT_SUPPORTED --- Spring不会执行事务中的代码，代码总是在非事务环境下执行，如果当前有事务，则该事务挂起
     *                    Propagation.NEVER --- 即使当前有事务，Spring也会在非事务环境下执行，如果当前有事务，则抛出异常
     *                    Propagation.NESTED --- 如果当前有事务，则在嵌套事务中执行，如果没有，那么执行情况与 Transaction - Definition.PROPAGATION_REQUIRED 一样
     * @param isolation   事务隔离级别
     *                    Isolation.DEFAULT --- PlatformTransactionManager 的默认隔离级别（对大多数数据库来说就是 ISOLATION_ READ_COMMITTED）
     *                    Isolation.READ_UNCOMMITTED --- 最低的隔离级别，事实上我们不应该称其为隔离级别，因为在事务完成前，其他事务可以看到该事务所修改的数据；而在其他事务提交前，该事务也可以看到其他事务所做的修改
     *                    Isolation.READ_COMMITTED --- 大多数数据库的默认级别；在事务完成前，其他事务无法看到该事务所修改的数据；遗憾的是，在该事务提交后，你就可以查看其他事务插入或更新的数据。这意味着在事务的不同点上，如果其他事务修改了数据，你就会看到不同的数据
     *                    Isolation.REPEATABLE_READ ---	比 ISOLATION_READ_COMMITTED 更严格，该隔离级别确保如果在事务中查询了某个数据集，即使其他事务修改了所查询的数据，你至少还能再次查询到相同的数据集；然而如果其他事务插入了新数据，你就可以查询到该新插入的数据
     *                    Isolation.SERIALIZABLE --- 代价最大、可靠性最高的隔离级别，所有的事务都是按顺序一个接一个地执行
     */
    public static void beginTransaction(Propagation propagation, Isolation isolation, TransAction action) {
        CustomTransactionManager customTransactionManager = getCustomTransactionManager();
        TransactionManagerWrapper tmWrapper = customTransactionManager.createTransactionManager(propagation, isolation);
        TransactionStatus transactionStatus = tmWrapper.getTransaction();

        try {
            action.doAction();
            tmWrapper.commit(transactionStatus);
        } catch (Exception ex) {
            tmWrapper.rollback(transactionStatus);
            throw ex;
        }
    }

}
