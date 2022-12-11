package diarsid.messaging.api.exceptions;

import static diarsid.messaging.api.Messaging.Destination.Type.TOPIC;

public class TopicNotCreatedException extends DestinationNotCreatedException {

    public TopicNotCreatedException(String name) {
        super(name, TOPIC);
    }
}
