package java.lang;

import java.io.IOException;

/**
 *
 */
class ProcessImpl {
    private ProcessImpl() {}
    native static Process start(String[] cmdarray, java.util.Map<String, String> environment, String dir,
                                boolean redirectErrorStream) throws IOException;
}
