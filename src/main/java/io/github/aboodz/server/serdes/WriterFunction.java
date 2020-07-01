package io.github.aboodz.server.serdes;

import com.google.common.net.MediaType;
import org.reactivestreams.Publisher;

import java.util.function.Supplier;

public interface WriterFunction extends Supplier<Publisher<byte[]>> {

    MediaType getMediaType();

}
