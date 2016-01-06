package tonivade.db;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tonivade.redis.command.IRequest;

public class TransactionState implements Iterable<IRequest> {

    private List<IRequest> requests = new LinkedList<>();

    public void enqueue(IRequest request) {
        requests.add(request);
    }

    public int size() {
        return requests.size();
    }

    @Override
    public Iterator<IRequest> iterator() {
        return requests.iterator();
    }

}
