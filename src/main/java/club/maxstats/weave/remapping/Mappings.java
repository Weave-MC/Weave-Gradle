package club.maxstats.weave.remapping;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

record Method(String name, String desc) {}

@ToString
class MappedClass {

    public String name;
    public final Map<Method, Method> methods = new HashMap<>();
    public final Map<String, String> fields = new HashMap<>();

    public MappedClass(String name) {
        this.name = name;
    }

}

@UtilityClass
public class Mappings {

    private final Map<String, MappedClass> classMap = new HashMap<>();

    public MappedClass getMappedClass(String notchName) {
        return classMap.get(notchName);
    }

    public String getMappedClassName(String notchName) {
        MappedClass mappedClass = getMappedClass(notchName);

        if (mappedClass == null) return null;
        return mappedClass.name;
    }

    static {
        InputStream joinedStream = Mappings.class.getResourceAsStream("/mappings");
        if (joinedStream == null) {
            System.err.println("Mappings stream is null");
            System.exit(1);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(joinedStream));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String type = line.substring(0, 2);
                String content = line.substring(4);
                switch (type) {
                    case "CL" -> {
                        String[] split = content.split(" ", 2);
                        MappedClass oldClass = classMap.get(split[0]);
                        if (oldClass != null) oldClass.name = split[1];
                        else classMap.put(split[0], new MappedClass(split[1]));
                    }
                    case "FD" -> {
                        String[] split = content.split(" ", 2);
                        String oldFieldClass = split[0];
                        int lastSlash = oldFieldClass.lastIndexOf('/');
                        String oldClass = oldFieldClass.substring(0, lastSlash);
                        String oldField = oldFieldClass.substring(lastSlash+1);

                        String newFieldClass = split[1];
                        lastSlash = newFieldClass.lastIndexOf('/');
                        String newField = newFieldClass.substring(lastSlash+1);

                        classMap.computeIfAbsent(oldClass, MappedClass::new).fields.put(oldField, newField);
                    }
                    case "MD" -> {
                        String[] split = content.split(" ", 4);
                        String oldMethodClass = split[0];
                        int lastSlash = oldMethodClass.lastIndexOf('/');
                        String oldClass = oldMethodClass.substring(0, lastSlash);
                        Method oldMethod = new Method(oldMethodClass.substring(lastSlash+1), split[1]);

                        String newMethodClass = split[2];
                        lastSlash = newMethodClass.lastIndexOf('/');
                        Method newMethod = new Method(newMethodClass.substring(lastSlash+1), split[3]);

                        classMap.computeIfAbsent(oldClass, MappedClass::new).methods.put(oldMethod, newMethod);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
