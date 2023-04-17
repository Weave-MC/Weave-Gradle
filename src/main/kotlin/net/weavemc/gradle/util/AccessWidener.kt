package net.weavemc.gradle.util

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AccessWidener(cv: ClassVisitor): ClassVisitor(Opcodes.ASM9, cv) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(makePublic(access), name, descriptor, signature, exceptions)
    }

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

private fun makePublic(acc: Int) =
    acc and (Opcodes.ACC_PRIVATE + Opcodes.ACC_PROTECTED).inv() or Opcodes.ACC_PUBLIC
