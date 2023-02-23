package club.maxstats.weave.remapping;

import org.objectweb.asm.commons.Remapper;

/**
 * Maps obfuscated classes and fields into MCP names using object-web's {@link Remapper} class, as-well as a mapping
 * file.
 *
 * @author Max (<a href="https://github.com/exejar">...</a>)
 * @see org.objectweb.asm.commons.Remapper
 */
public class ClassRemapper extends Remapper {


    /**
     * Maps a class name from obfuscated to MCP.
     *
     * @param internalName The internal name of a class.
     * @return             The mapped name of the class/field.
     */
    @Override
    public String map(String internalName) {
        String mappedClassName = Mappings.getMappedClassName(internalName);
        if (mappedClassName != null)
            return mappedClassName;

        return super.map(internalName);
    }

    /**
     * Maps a method name from obfuscated to MCP.
     *
     * @param owner      The internal name of the owner class of the method.
     * @param name       The name of the method.
     * @param descriptor The descriptor of the method.
     * @return           The mapped name of the method.
     */
    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        MappedClass mappedClass = Mappings.getMappedClass(owner);

        if (mappedClass != null) {
            Method mappedMethod = mappedClass.methods.get(new Method(name, descriptor));
            if (mappedMethod != null)
                return mappedMethod.name();
        }

        return super.mapMethodName(owner, name, descriptor);
    }

    /**
     * Maps a field name from obfuscated to MCP.
     *
     * @param owner      The internal name of the owner class of the field.
     * @param name       The name of the field.
     * @param descriptor The descriptor of the field.
     * @return           The mapped name of the field.
     */
    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        MappedClass mappedClass = Mappings.getMappedClass(owner);

        if (mappedClass != null) {
            String mappedField = mappedClass.fields.get(name);
            if (mappedField != null)
                return mappedField;
        }

        return super.mapFieldName(owner, name, descriptor);
    }

}
