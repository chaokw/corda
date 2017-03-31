package net.corda.core.serialization.amqp

import org.apache.qpid.proton.codec.Data
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ListSerializer(val declaredType: ParameterizedType) : Serializer() {
    private val typeName = declaredType.toString()
    private val typeDescriptor = declaredType.toString()

    private val typeNotation: TypeNotation = RestrictedType(typeName, null, emptyArray(), "list", Descriptor(typeDescriptor, null), emptyArray())

    override fun writeClassInfo(output: SerializationOutput) {
        output.writeTypeNotations(typeNotation)
        output.requireSerializer(declaredType.actualTypeArguments[0])
    }

    override fun writeObject(obj: Any, data: Data, type: Type, output: SerializationOutput) {
        // Write described
        data.putDescribed()
        data.enter()
        // Write descriptor
        data.putObject(typeNotation.descriptor)
        // Write list
        data.putList()
        data.enter()
        for (entry in obj as List<*>) {
            output.writeObjectOrNull(entry, data, declaredType.actualTypeArguments[0])
        }
        data.exit() // exit list
        data.exit() // exit described
    }
}