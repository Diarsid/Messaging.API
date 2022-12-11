package diarsid.messaging.api.exceptions;

import static diarsid.messaging.api.Messaging.Destination.Type.QUEUE;

public class QueueNotCreatedException extends DestinationNotCreatedException {

    public QueueNotCreatedException(String name) {
        super(name, QUEUE);
    }
}
