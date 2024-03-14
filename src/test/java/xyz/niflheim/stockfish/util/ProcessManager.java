package xyz.niflheim.stockfish.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.niflheim.stockfish.engine.util.Util.ENGINE_FILE_NAME_PREFIX;

public class ProcessManager {

    /**
     * Get Stockfish process number.
     *
     * @return Stockfish process number
     * @throws IOException when can not execute Unix command
     */
    public static long getProcessNumber() throws IOException {
        return getProcessNumber(ENGINE_FILE_NAME_PREFIX);
    }


    /**
     * @param process the name of the process to be found.
     * @return process number with name {@code process}
     * @throws IOException when can not execute Unix command
     */
    public static long getProcessNumber(String process) throws IOException {
        String processListCommand = null;
        if(OSValidator.isUnix()) {
            processListCommand = "ps -few";
        } else if (OSValidator.isWindows()) {
            processListCommand = "tasklist /FI \"IMAGENAME eq " + ENGINE_FILE_NAME_PREFIX + "*\" ";
        }
        Process p = Runtime.getRuntime().exec(processListCommand);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        long ans = input.lines().filter(l -> l.contains(process)).count();
        input.close();
        return ans;
    }

    /**
     * Kill one Stockfish process.
     *
     * @throws IOException when can not execute Unix command
     */
    public static void killStockfishProcess() throws IOException, InterruptedException {
        String processListingCommand = null;
        if(OSValidator.isUnix()) {
            processListingCommand = "ps -few";

        } else if (OSValidator.isWindows()) {
            processListingCommand = "tasklist /FI \"IMAGENAME eq "+ ENGINE_FILE_NAME_PREFIX + "*\"";
        }
        Process p = Runtime.getRuntime().exec(processListingCommand);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        List<String> pids = input.lines().filter(l -> l.contains(ENGINE_FILE_NAME_PREFIX)).collect(Collectors.toList());
        String pid = pids.get(0).split("\\s+")[1];
        if (OSValidator.isUnix()) {
            Runtime.getRuntime().exec("kill " + pid);
        } else if (OSValidator.isWindows()) {
            Runtime.getRuntime().exec("taskkill /F /PID " + pid);
            //it seems like it takes some time for a process to die on windows
            Thread.sleep(200);
        }
        input.close();
    }
}
