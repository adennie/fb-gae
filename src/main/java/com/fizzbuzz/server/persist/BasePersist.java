package com.fizzbuzz.server.persist;

import java.util.ConcurrentModificationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.Transaction;
import com.google.code.twig.configuration.Configuration;
import com.google.code.twig.standard.BaseObjectDatastore;

public class BasePersist {

    private static int MAX_TRIES = 3;

    protected static BaseObjectDatastore getDs() {
        return DatastoreHelper.getDs();
    }

    static Configuration getConfiguration() {
        return DatastoreHelper.getDs().getConfiguration();
    }

    /** Alternate interface to Runnable for executing transactions */
    public interface Transactable
    {
        void run();
    }

    /**
     * Provides a place to put the result. Note that the result
     * is only valid if the transaction completes successfully; otherwise
     * it should be ignored because it is not necessarily valid.
     */
    abstract public static class TransactableWithResult<R>
            implements Transactable
    {
        protected R result;

        public R getResult() {
            return this.result;
        }
    }

    /**
     * Run this task through transactions until it succeeds without an optimistic
     * concurrency failure.
     */

    public static void repeatInTransaction(final Transactable t) {
        repeatInTransaction(t, false);
    }

    // run the Transactable in a transaction. Retry indefinitely if ConcurentModificationExceptions occur.
    public static void repeatInTransaction(final Transactable t,
            final boolean mustBeOuterTx)
    {
        while (true)
        {
            try
            {
                runInTransaction(t, mustBeOuterTx);
                break;
            }
            catch (ConcurrentModificationException cme)
            {
                Logger logger = LoggerFactory.getLogger(LoggingManager.TAG);
                logger.warn("Optimistic concurrency failure for thread {}.  Retrying...", Thread.currentThread().getId(), cme);
            }
        }
    }

    public static <R> R repeatInTransactionWithResult(final TransactableWithResult<R> t) {
        return repeatInTransactionWithResult(t, false);
    }

    public static <R> R repeatInTransactionWithResult(final TransactableWithResult<R> t,
            final boolean mustBeOuterTx) {
        repeatInTransaction(t, mustBeOuterTx);
        return t.getResult();
    }

    private static void runInTransaction(final Transactable t,
            final boolean mustBeOuterTx) {
        BasePersist p = new BasePersist();
        p.doTransaction(t, mustBeOuterTx);
    }

    /*
     * Executes the task in a transaction. If a timeout occurs, retry a few times, with an exponential backoff. GAE
     * automatically retries non-transactional datastore operations, but for transactions, we have to do it ourselves.
     */
    private void doTransaction(final Transactable task,
            final boolean mustBeOuterTx)
    {
        boolean done = false;
        for (Integer tryCount = 0, retryWaitMs = 100; !done && tryCount < MAX_TRIES; tryCount++, retryWaitMs *= 2)
        {
            Transaction tx = beginTransaction();

            if (tx == null && mustBeOuterTx == true) {
                throw new PersistenceException(
                        "attempted to start an outer transaction when a transaction was already in progress.");
            }

            try
            {
                task.run();
                commitTransaction(tx);
                done = true;
            }
            catch (DatastoreTimeoutException dte) {
                Logger logger = LoggerFactory.getLogger(LoggingManager.TAG);
                logger.warn(
                        "Datastore timeout exception (attempt #" + Integer.toString(tryCount) +
                                ") for thread {}.", Thread.currentThread().getId(), dte);
            }
            finally {
                rollbackTransactionIfActive(tx);
            }

            if (!done) {
                try {
                    Thread.sleep(retryWaitMs);
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        // if we reached the maximum # of tries without success, throw something easier to handle higher up
        if (!done)
            throw new PersistenceException("Tried " + Integer.toString(MAX_TRIES)
                    + " times to execute datastore transaction for thread "
                    + Long.toString(Thread.currentThread().getId())
                    + ", but encountered DatastoreTimeoutException each time.  Giving up.");

    }

    // start a transaction if one isn't already in place. If one is already in place, return null.
    private static Transaction beginTransaction() {

        Transaction tx = getDs().getTransaction();
        if (tx == null) {
            tx = getDs().beginTransaction();
            Logger logger = LoggerFactory.getLogger(LoggingManager.TAG);
            logger.trace("Transaction {} started for thread {}", tx, Thread.currentThread().getId());
            return tx;
        }

        else {
            Logger logger = LoggerFactory.getLogger(LoggingManager.TAG);
            logger.debug(
                    "BasePersist.beginTransaction: transaction already in progress for thread{}, no new transaction started",
                    Thread.currentThread().getId());
            return null;
        }
    }

    // not currently using this method, but saving it for later. Commenting out to eliminate warning about unused code.
    // private static Transaction beginXGTransaction() {
    //
    // Transaction tx = getDs().getTransaction();
    // if (tx == null) {
    // TransactionOptions options = TransactionOptions.Builder.withXG(true);
    // tx = getDs().beginTransaction(options);
    // Logger logger = LoggerFactory.getLogger(LoggingManager.TAG);
    // logger.trace("Transaction {} started for thread {}", tx, Thread.currentThread().getId());
    // return tx;
    // }
    // else
    // return null;
    // }

    private static void commitTransaction(final Transaction tx) {
        if (tx != null) {
            Logger logger = LoggerFactory.getLogger(LoggingManager.TAG);
            logger.trace("Committing transaction {} for thread {}", tx, Thread.currentThread().getId());

            try {
                tx.commit();
            }
            finally {
                getDs().removeTransaction();
            }
        }
    }

    private static void rollbackTransactionIfActive(final Transaction tx) {
        if (tx != null) {
            try {
                if (tx.isActive()) {
                    Logger logger = LoggerFactory.getLogger(LoggingManager.TAG);
                    logger.trace("Rolling back transaction {} for thread {}", tx, Thread.currentThread().getId());
                    tx.rollback();
                }
            }
            finally {
                getDs().removeTransaction();
            }
        }
    }
}
