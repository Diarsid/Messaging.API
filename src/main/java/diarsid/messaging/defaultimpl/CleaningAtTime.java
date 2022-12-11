package diarsid.messaging.defaultimpl;

import java.util.concurrent.TimeUnit;

import diarsid.messaging.api.Messaging;

public abstract class CleaningAtTime implements Messaging.Destination.Cleaning, Messaging.Destination.Cleaning.Time {

    private final int interval;
    private final TimeUnit unit;
    protected Messaging messaging;

    public CleaningAtTime(int interval, TimeUnit unit) {
        this.interval = interval;
        this.unit = unit;
    }

    public final void inject(Messaging messaging) {
        this.messaging = messaging;
    }

    @Override
    public TimeUnit unit() {
        return this.unit;
    }

    @Override
    public int interval() {
        return this.interval;
    }

    @Override
    public Time time() {
        return this;
    }

}
