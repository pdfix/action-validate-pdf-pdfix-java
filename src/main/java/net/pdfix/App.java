package net.pdfix;

import java.io.IOException;
import java.util.Properties;

public class App {
        private static void displayVersion() {
                Properties properties = new Properties();
                try {
                        properties.load(App.class.getClassLoader().getResourceAsStream("version.properties"));
                        String version = properties.getProperty("project.version");
                        String name = properties.getProperty("project.name");
                        System.out.println(name + " v" + version);
                } catch (IOException e) {
                        System.err.println("Error reading version information.");
                }
        }

        public static void main(String[] args) throws Exception {
                displayVersion();

                String inputFile = "";
                String last = "";
                for (String s : args) {
                        if (!last.isEmpty()) {
                                // expected parameter name in last
                                if (last == "-i") {
                                        inputFile = s;
                                }
                        } else {
                                if (s == "--help") {
                                        System.out.println("Show help ...");
                                        return;
                                        // show version
                                } else if ((s.compareTo("-i") == 0) || (s.compareTo("--input") == 0)) {
                                        last = "-i";
                                }
                        }
                }

                try {
                        if (inputFile.isEmpty()) {
                                throw new RuntimeException("Missing input file. See --help for cli parameters");
                        }
                        if (FindDuplicateMcid.checkDuplicateMcid(inputFile, false) == 0) {
                                System.out.println("No duplicate MCIDs found");
                        }
                } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                }
        }
}
