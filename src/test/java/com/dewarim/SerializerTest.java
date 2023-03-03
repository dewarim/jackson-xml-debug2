package com.dewarim;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializerTest {

    private final String xmlContent = "<content><xml>xxx</xml></content>";

    @JsonSerialize(using=CoreSerializer.class)
    public static class Core{
        private final Long   id;
        private final String content;

        public Core(Long id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    public static class CoreSerializer extends JsonSerializer<Core> {

        protected CoreSerializer() {
        }

        @Override
        public void serialize(Core core, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", core.id);
            jsonGenerator.writeObjectFieldStart("content");
            jsonGenerator.writeRaw(core.content);
            jsonGenerator.writeEndObject();
        }
    }

    @Test
    public void serializeTest() throws JsonProcessingException {
        Core core = new Core(1L,xmlContent );
        String value  = new XmlMapper().writeValueAsString(core);
        assertTrue(value.contains(xmlContent));
    }


    public static class CoreWrapper{
        @JacksonXmlElementWrapper(localName = "cores")
        @JacksonXmlProperty(localName = "core")
        List<Core> core= new ArrayList<>();

        public List<Core> getCore() {
            return core;
        }

        public void setCore(List<Core> core) {
            this.core = core;
        }
    }
    @Test
    public void wrappedSerializer() throws JsonProcessingException{

        CoreWrapper wrapper = new CoreWrapper();
        wrapper.setCore(List.of(new Core(2L,xmlContent)));
        // throws: com.fasterxml.jackson.databind.JsonMappingException: Current context not Array but Object (through reference chain: com.dewarim.SerializerTest$CoreWrapper["core"])
        new XmlMapper().writeValueAsString(wrapper);

    }
}
