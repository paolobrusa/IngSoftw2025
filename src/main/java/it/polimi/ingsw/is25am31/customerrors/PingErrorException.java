package it.polimi.ingsw.is25am31.customerrors;

import java.io.Serial;

public class PingErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -495265457434603548L;
    public PingErrorException(String cause) {
        super(cause);
    }
}
