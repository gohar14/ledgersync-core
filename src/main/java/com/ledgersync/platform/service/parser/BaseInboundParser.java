package com.ledgersync.platform.service.parser;

import java.io.InputStream;
import java.util.List;

public interface BaseInboundParser<T> {
    List<T> parse(InputStream inputStream);
}
