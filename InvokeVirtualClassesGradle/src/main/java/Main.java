import org.apache.bcel.classfile.*;

import java.io.IOException;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: ");
        }
        // assume that in first argument there will be absolute path to needed .class file
        ClassParser parser = new ClassParser(args[0]);
        try {
            findVirtualCalls(parser);
        } catch (ClassFormatException cle) {
            System.err.println("Could not parse given file as correct .class file");
            System.exit(-1);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            System.exit(-1);
        }
    }

    private static void findVirtualCalls(ClassParser parser) throws ClassFormatException, IOException {
        TreeSet<String> foundClasses = new TreeSet<>();
        JavaClass javaClass = parser.parse();

        for (Method method : javaClass.getMethods()) {
            String bytecode = method.getCode().toString();
            String[] invokeVirtualEntries = bytecode.split("invokevirtual");

            for (int i = 1; i < invokeVirtualEntries.length; ++i) {
                String[] tokens = invokeVirtualEntries[i].split(" +");
                String[] methodNameParts = tokens[0].split("\\.");
                String className = methodNameParts[methodNameParts.length - 2];
                foundClasses.add(className);
            }
        }

        System.out.printf("Found classes: %d\n", foundClasses.size());

        for (String className : foundClasses) {
            System.out.println("-- " + className);
        }
    }
}
