package net.weavemc.gradle.util

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AccessWidener(cv: ClassVisitor): ClassVisitor(Opcodes.ASM9, cv) {

    /**
     * Visits a method in a class.
     *
     * This method is invoked by the visitor when it reaches a class declaration.
     * It can preform access modification, method renaming, or just a bytecode analysis.
     *
     * @param access     The access flags of the method.
     * @param name       The internal name of the method.
     * @param descriptor The descriptor of the method.
     * @param signature  The signature of the method.
     * @param exceptions The exceptions thrown by the method.
     * @return A [MethodVisitor] to visit the method's bytecode.
     */
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(makePublic(access), name, descriptor, signature, exceptions)
    }

    /**
     * Visits a field in a class.
     *
     * This method, invoked by the visitor when it reaches a class declaration, can preform various
     * operations on the field, such as access modification, renaming, or an analysis on its attributes.
     *
     * @param access     The access flags of the field.
     * @param name       The internal name of the field.
     * @param descriptor The descriptor of the field.
     * @param signature  The signature of the field.
     * @param value      The initial value of the field.
     * @return A [FieldVisitor] to visit the field's attributes.
     */
    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(makePublic(access), name, descriptor, signature, value)
    }
}

/**
 * Modifies the access flags of a method or field in order to make it public
 *
 * This function removes the `private` and `protected` modifiers from the access flag
 * and adds the `public` modifier to it.
 *
 * @param acc The access flags of the method or field.
 * @return The modified access flags with the `public` modifier.
 */
private fun makePublic(acc: Int) =
    acc and (Opcodes.ACC_PRIVATE + Opcodes.ACC_PROTECTED).inv() or Opcodes.ACC_PUBLIC
