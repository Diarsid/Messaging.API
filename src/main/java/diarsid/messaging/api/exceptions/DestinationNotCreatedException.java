package diarsid.messaging.api.exceptions;

import java.nio.file.Path;

import diarsid.messaging.api.Messaging;

public class DestinationNotCreatedException extends MessagingException {

    public final String name;
    public final Messaging.Destination.Type type;
    public final Path path;

    public DestinationNotCreatedException(String name, Messaging.Destination.Type type) {
        this.name = name;
        this.type = type;
        this.path = null;
    }

    public DestinationNotCreatedException(String name, Messaging.Destination.Type type, Path path, Exception e) {
        super(e);
        this.name = name;
        this.type = type;
        this.path = path;
    }
}
