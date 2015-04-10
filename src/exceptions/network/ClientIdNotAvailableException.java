package exceptions.network;

import exceptions.NetworkException;

/**
 * @author Tim Hintzbergen
 * @since 10-4-15.
 * When this Exception is thrown it could mean that you are not connected to the Ad-hoc network.
 * When this Exception is caught it is advised to kill the task. This should not occur.
 * Exit code -1 should be used.
 */
public class ClientIdNotAvailableException extends NetworkException {

    @Override
    public String getMessage() {
        return "No Client ID found by NetworkManager.getClientId()";
    }
}
