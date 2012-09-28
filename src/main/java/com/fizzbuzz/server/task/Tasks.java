package com.fizzbuzz.server.task;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fizzbuzz.server.persist.LoggingManager;
import com.google.appengine.api.taskqueue.InternalFailureException;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.QueueFailureException;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransactionalTaskException;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.common.collect.ImmutableMap;

public class Tasks {
    private static int MAX_TRIES = 3;

    public static void queueTask(final String url,
            final ImmutableMap<String, String> args) {
        queueTask(url, args, null);
    }

    public static void queueTask(final String url,
            final ImmutableMap<String, String> args,
            final String taskName) {

        TaskOptions task = TaskOptions.Builder.withUrl(url);
        if (taskName != null)
            task.taskName(taskName);
        for (Map.Entry<String, String> entry : args.entrySet()) {
            task.param(entry.getKey(), entry.getValue());
        }

        // handle certain exceptions by retrying a few times, with an exponential backoff
        // see also:
        // http://stackoverflow.com/questions/9331545/which-task-queue-exceptions-raised-from-add-make-sense-to-retry
        boolean done = false;
        for (Integer tryCount = 0, retryWaitMs = 100; !done && tryCount < MAX_TRIES; tryCount++, retryWaitMs *= 2)
        {
            try {
                QueueFactory.getDefaultQueue().add(task);
                done = true;
            }
            catch (TransientFailureException tfe) {
                handleTaskQueueException(tryCount, tfe);
            }
            catch (InternalFailureException ife) {
                handleTaskQueueException(tryCount, ife);
            }
            catch (QueueFailureException qfe) {
                handleTaskQueueException(tryCount, qfe);
            }
            catch (TransactionalTaskException tte) {
                handleTaskQueueException(tryCount, tte);
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
            throw new TaskException();

    }

    private static void handleTaskQueueException(final Integer tryCount,
            final RuntimeException e) {
        Logger logger = LoggerFactory.getLogger(LoggingManager.TAG);
        logger.warn(
                "Task queueing exception (attempt #" + Integer.toString(tryCount) +
                        ") for thread {}.", Thread.currentThread().getId(), e);
    }
}
