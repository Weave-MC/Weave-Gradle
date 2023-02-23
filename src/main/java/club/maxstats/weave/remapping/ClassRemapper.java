package club.maxstats.weave.remapping;

import org.objectweb.asm.commons.Remapper;

public class ClassRemapper extends Remapper {
    @Override
    public String map(String internalName) {
        String mappedClassName = Mappings.getMappedClassName(internalName);
        if (mappedClassName != null)
            return mappedClassName;

        return super.map(internalName);
    }

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
