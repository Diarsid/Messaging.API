package diarsid.messaging.api;

import java.util.concurrent.TimeUnit;

import diarsid.support.model.CreatedAt;
import diarsid.support.model.Named;
import diarsid.support.objects.CommonEnum;
import diarsid.support.objects.workers.Worker;

import static diarsid.messaging.api.Messaging.Destination.Type.QUEUE;
import static diarsid.messaging.api.Messaging.Destination.Type.TOPIC;

public interface Messaging extends Named, Worker.Destroyable {

    <T> Queue<T> queue(String name, Class<T> type);

    <T> Queue<T> queue(String name, Destination.Serialization<T> serialization);

    <T> Queue<T> queue(String name, Class<T> type, Destination.Cleaning cleaning);

    <T> Queue<T> queue(String name, Destination.Serialization<T> serialization, Destination.Cleaning cleaning);

    <T> Topic<T> topic(String name, Class<T> type);

    <T> Topic<T> topic(String name, Destination.Serialization<T> serialization);

    <T> Topic<T> topic(String name, Class<T> type, Destination.Cleaning cleaning);

    <T> Topic<T> topic(String name, Destination.Serialization<T> serialization, Destination.Cleaning cleaning);

    interface Receiver<T> {

        void receive(long offset, T t);
    }

    interface Destination<T> extends Named {

        enum Type implements CommonEnum<Type> {

            QUEUE("Queue"),
            TOPIC("Topic");

            public final String typeName;

            Type(String typeName) {
                this.typeName = typeName;
            }
        }

        void send(T t);

        Destination.Type type();

        interface Serialization<T> {

            enum PersistentType implements CommonEnum<PersistentType> {
                BYTES,
                STRING
            }

            Class<T> messageType();

            PersistentType persistentType();

            T deserialize(Object stored);

            Object serialize(T message);

        }

        interface Cleaning {

            interface Time {

                TimeUnit unit();

                int interval();
            }

            void clean(Destination<?> destination);

            Cleaning.Time time();
        }
    }

    interface Queue<T> extends Destination<T> {

        Agent.OfQueue<T> connect(String agentName, Receiver<T> receiver);

        @Override
        default Destination.Type type() {
            return QUEUE;
        }
    }

    interface Topic<T> extends Destination<T> {

        Agent.OfTopic<T> connect(String agentName, Receiver<T> receiver);

        @Override
        default Destination.Type type() {
            return TOPIC;
        }
    }

    interface Agent extends Named, CreatedAt, Worker.Pausable, Worker.Destroyable {

        interface OfQueue<T> extends Agent {

            Queue<T> queue();
        }

        interface OfTopic<T> extends Agent {

            Topic<T> topic();
        }

    }
}
