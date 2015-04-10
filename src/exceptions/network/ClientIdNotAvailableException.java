package exceptions.network;

import exceptions.NetworkException;
import network.NetworkManager;

/**
 * @author Tim Hintzbergen
 * @since 10-4-15.
 * Thrown when no ID could be generated
 * <p>
 *     When this Exception is thrown it could mean that you are not connected to the Ad-hoc network or for some other reason no ID could be generated.
 *     When this Exception is caught it is advised to {@link System#exit(int status) kill the task}.
 *     Exit code -1 should be used.
 * </p>
 * @see NetworkManager#getClientId() getClientId()
 */
public class ClientIdNotAvailableException extends NetworkException {

    @Override
    public String getMessage() {
        return "No Client ID found by NetworkManager.getClientId()";
    }
}
