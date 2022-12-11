package diarsid.messaging.defaultimpl;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.messaging.api.Messaging;
import diarsid.support.concurrency.stateful.workers.AbstractStatefulDestroyableWorker;
import diarsid.support.objects.references.Possible;
import diarsid.support.objects.references.References;

import static diarsid.support.concurrency.threads.ThreadsUtil.shutdownAndWait;

public class DestinationAsyncCleaner extends AbstractStatefulDestroyableWorker {

    private static final Logger log = LoggerFactory.getLogger(DestinationAsyncCleaner.class);

    private final Messaging messaging;
    private final Messaging.Destination.Cleaning cleaning;
    private final Messaging.Destination<?> destination;
    private final ScheduledExecutorService async;
    private final Possible<Future<?>> asyncCleaning;

    public DestinationAsyncCleaner(
            Messaging messaging,
            Messaging.Destination<?> destination,
            Messaging.Destination.Cleaning cleaning) {
        super(messaging.name() + "." + destination.name() + ".cleaner");
        this.messaging = messaging;
        this.cleaning = cleaning;
        this.destination = destination;
        this.async = ((MessagingThreads) messaging)
                .threads()
                .newNamedScheduledExecutorService(
                        "Messaging[" + messaging.name() + "]." + destination.type().name() + "[" + destination.name() + "].cleaner",
                        1);
        this.asyncCleaning = References.simplePossibleButEmpty();

        if ( cleaning instanceof CleaningAtTime ) {
            ((CleaningAtTime) cleaning).inject(this.messaging);
        }
    }

    private void cleanSafely() {
        try {
            log.info("cleaning with " + this.cleaning.getClass().getSimpleName());
            this.cleaning.clean(this.destination);
        }
        catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    @Override
    protected boolean doSynchronizedStartWork() {
        Messaging.Destination.Cleaning.Time time = this.cleaning.time();
        this.asyncCleaning.resetTo(this.async.scheduleWithFixedDelay(
                this::cleanSafely,
                time.interval(),
                time.interval(),
                time.unit()));

        return true;
    }

    @Override
    protected boolean doSynchronizedDestroy() {
        this.asyncCleaning.ifPresent(job -> job.cancel(true));
        this.asyncCleaning.nullify();
        shutdownAndWait(this.async);

        return true;
    }
}
