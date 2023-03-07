package club.maxstats.weave.remapping;

import lombok.experimental.UtilityClass;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Mappings {

    private final Map<String, String> classMap = new HashMap<>();

    /**
     * Key is Method Class Owner + "/" + Method Name + Method Description in Notch mappings.
     * <p>
     * ex. "ave/Z()V"
     */
    private final Map<String, String> methodMap = new HashMap<>();

    /**
     * Key is Field Class Owner + "/" + Field Name in Notch mappings.
     * <p>
     * ex. "ave/A"
     */
    private final Map<String, String> fieldMap = new HashMap<>();

    public String getMappedClass(String notchClass) {
        return classMap.get(notchClass);
    }

    public String getMappedMethod(String notchMethod) {
        return methodMap.get(notchMethod);
    }

    public String getMappedField(String notchField) {
        return fieldMap.get(notchField);
    }

    public void printTest() {
        for (Map.Entry<String, String> classEntry : classMap.entrySet()) {
            System.out.println("Classes - Vanilla: " + classEntry.getKey() + " MCP: " + classEntry.getValue());
        }
        for (Map.Entry<String, String> methodEntry : methodMap.entrySet()) {
            System.out.println("Methods - Vanilla: " + methodEntry.getKey() + " MCP: " + methodEntry.getValue());
        }
        for (Map.Entry<String, String> fieldEntry : fieldMap.entrySet()) {
            System.out.println("Fields - Vanilla: " + fieldEntry.getKey() + " MCP: " + fieldEntry.getValue());
        }
    }

    public static void createMappings(String joinedPath, String methodsPath, String fieldsPath) {
        File joinedFile = new File(joinedPath);
        File methodsFile = new File(methodsPath);
        File fieldsFile = new File(fieldsPath);

        Map<String, String> srgToMcpMethods = new HashMap<>();
        Map<String, String> srgToMcpFields = new HashMap<>();

        parseCSV(methodsFile, srgToMcpMethods);
        parseCSV(fieldsFile, srgToMcpFields);

        try (InputStream joinedStream = new FileInputStream(joinedFile)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(joinedStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String type = line.substring(0, 2);
                    String content = line.substring(4);
                    String[] split = content.split(" ");

                    switch (type) {
                        case "CL" -> classMap.put(split[0], split[1]);
                        case "FD" -> {
                            String fieldJoined = split[0];
                            String srgName = split[1].substring(split[1].lastIndexOf('/') + 1);
                            String mcpName = srgToMcpFields.get(srgName);

                            fieldMap.put(fieldJoined, mcpName);
                        }
                        case "MD" -> {
                            String methodJoined = split[0] + split[1];
                            String srgName = split[2].substring(split[2].lastIndexOf('/') + 1);
                            String mcpName = srgToMcpMethods.get(srgName);

                            methodMap.put(methodJoined, mcpName);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//      printTest();
    }

    private static void parseCSV(File csvFile, Map<String, String> srgToMcpFields) {
        try (InputStream fieldsStream = new FileInputStream(csvFile)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fieldsStream))) {
                String line;
                while((line = br.readLine()) != null) {
                    /* Skip the first line. */
                    if (line.contains("searge"))
                        continue;

                    /* SRG, MCP */
                    String[] split = line.split(",");
                    srgToMcpFields.put(split[0], split[1]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
