package com.jstarcraft.core.codec.avro;

import com.jstarcraft.core.codec.avro.converter.AvroContext;
import com.jstarcraft.core.codec.specification.CodecDefinition;

import java.io.OutputStream;

/**
 * @author: MnZzV
 **/
public class AvroWriter extends AvroContext {

    private OutputStream outputStream;

    public AvroWriter(OutputStream outputStream, CodecDefinition definition) {
        super(definition);
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
