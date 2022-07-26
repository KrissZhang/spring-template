package com.self.common.config;

import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

public class LongSerializer extends NumberSerializers.LongSerializer {

    private static final long serialVersionUID = 3181467364424677992L;

    public LongSerializer() {
        super(Long.class);
    }

}
