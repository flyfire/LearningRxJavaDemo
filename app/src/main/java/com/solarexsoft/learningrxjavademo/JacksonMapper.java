package com.solarexsoft.learningrxjavademo;

/**
 * Created by houruhou on 2020/5/25/8:25 PM
 * Desc:
 */
import android.annotation.SuppressLint;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class JacksonMapper {

    private ObjectMapper objectMapper;

    private static final Pattern OLD_FORMAT_TIME = Pattern.compile("[0-9]+:[0-9]+:[0-9]");

    private static final boolean JAVA8_TIME_AVAILABLE = isJava8TimeAvailable();
    private static boolean isJava8TimeAvailable() {
        try {
            Class.forName("java.time.Instant");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    JacksonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static MapperBuilder xml() {
        return new MapperBuilder(new XmlMapper());
    }

    public static MapperBuilder json() {
        return new MapperBuilder(new ObjectMapper());
    }

    public static class MapperBuilder {
        private ObjectMapper objectMapper;
        private boolean failOnUnknownProperties;
        private boolean supportDerivedClassInCollection;
        private boolean supportNanosTimestampSerializer;
        private boolean sortPropertiesAlphabetically;
        private boolean allowUnquotedControlChars;


        MapperBuilder(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            this.failOnUnknownProperties = true;
            this.supportDerivedClassInCollection = false;
            this.supportNanosTimestampSerializer = true;
            this.sortPropertiesAlphabetically = false;
            this.allowUnquotedControlChars = false;
        }

        public MapperBuilder failOnUnknownProperties(boolean failOnUnknownProperties) {
            this.failOnUnknownProperties = failOnUnknownProperties;
            return this;
        }

        public MapperBuilder supportDerivedClassInCollection(boolean supportDerivedClassInCollection) {
            this.supportDerivedClassInCollection = supportDerivedClassInCollection;
            return this;
        }

        public MapperBuilder allowUnquotedControlChars(boolean allowUnquotedControlChars) {
            this.allowUnquotedControlChars = allowUnquotedControlChars;
            return this;
        }

        public MapperBuilder supportNanosTimestampSerializer(boolean supportNanosTimestampSerializer) {
            this.supportNanosTimestampSerializer = supportNanosTimestampSerializer;
            return this;
        }

        public MapperBuilder sortPropertiesAlphabetically(boolean sortPropertiesAlphabetically) {
            this.sortPropertiesAlphabetically = sortPropertiesAlphabetically;
            return this;
        }

        public JacksonMapper build() {
            setModule(objectMapper, supportDerivedClassInCollection, supportNanosTimestampSerializer);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, sortPropertiesAlphabetically);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, allowUnquotedControlChars);
            return new JacksonMapper(objectMapper);
        }

    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }


    /**
     * value should be a POJO when serialize to xml
     *
     * https://github.com/FasterXML/jackson-dataformat-xml#known-limitations
     */
    public String serialize(Object value) throws JacksonException {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JacksonException(value.toString(), e);
        }
    }

    public <V> V deserialize(String value, Class<V> valueClass)
            throws JacksonException {
        return deserialize(value, toTypeRef(valueClass));
    }

    /**
     * This method is necessary for binding generic type containers, e.g.
     * List<T>, Map<K, V>.
     *
     * http://wiki.fasterxml.com/JacksonInFiveMinutes#Data_Binding_with_Generics
     * http://wiki.fasterxml.com/JacksonFAQ#Deserializing_Generic_types
     */
    public <V> V deserialize(String value, TypeReference<V> valueTypeRef)
            throws JacksonException {
        try {
            return objectMapper.readValue(value, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new JacksonException(value, e);
        } catch (IOException e) {
            throw new JacksonException(value, e);
        }
    }

    // XmlMapper Support for native class is incomplete, such as XmlArray to JsonNode will miss element
    public JsonNode createNode(String json) throws JacksonException {
        if (objectMapper instanceof XmlMapper) {
            throw new UnsupportedOperationException("XmlMapper support for JsonNode is incomplete");
        }
        try {
            return objectMapper.readValue(json, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new JacksonException(json, e);
        } catch (IOException e) {
            throw new JacksonException(json, e);
        }
    }

    public <V> V fromJson(JsonNode node, Class<V> valueClass, String fieldName)
            throws JacksonException {
        return fromJson(node, toTypeRef(valueClass), fieldName);
    }

    public <V> V fromJson(JsonNode node, TypeReference<V> valueTypeRef, String fieldName)
            throws JacksonException {
        if (objectMapper instanceof XmlMapper) {
            throw new UnsupportedOperationException("XmlMapper support for JsonNode is incomplete");
        }
        JsonNode target = node.get(fieldName);
        if (target == null) {
            return null;
        }

        try {
            return objectMapper.readValue(objectMapper.treeAsTokens(target), valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new JacksonException(node.toString(), e);
        } catch (IOException e) {
            throw new JacksonException(node.toString(), e);
        }
    }

    public static <V> TypeReference<V> toTypeRef(final Class<V> clazz) {
        return new TypeReference<V>() {
            @Override
            public Type getType() {
                return clazz;
            }
        };
    }

    @SuppressLint("NewApi")
    private static void setModule(ObjectMapper mapper, boolean supportDerivedClassInCollection,
                                  boolean supportNanosTimestampSerializer) {
        SimpleModule module = createModule();
        if (supportNanosTimestampSerializer) {
            module.addSerializer(DATE_SERIALIZER).addDeserializer(Date.class, TIMESTAMP_DESERIALIZER)
                    .addDeserializer(Timestamp.class, TIMESTAMP_DESERIALIZER);
            if (JAVA8_TIME_AVAILABLE) {
                module.addSerializer(Java8Support.INSTANT_SERIALIZER).addDeserializer(Instant.class,
                        Java8Support.INSTANT_DESERIALIZER);
            }
        }
        if (supportDerivedClassInCollection) {
            module.addSerializer(MAP_SERIALIZER).addSerializer(LIST_SERIALIZER);
        }
        mapper.registerModule(module);

        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, Visibility.NONE)
                .setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        mapper.setSerializationInclusion(Include.NON_NULL);

    }

    public static SimpleModule createModule() {
        return new SimpleModule("CacheModule", new Version(1, 0, 0, null, "com.solarexsoft", "jackson"));
    }

    private static final JsonSerializer<Date> DATE_SERIALIZER = new StdScalarSerializer<Date>(Date.class) {
        private static final long serialVersionUID = 6536326305352993269L;

        @Override
        public void serialize(Date value, JsonGenerator jgen,
                              SerializerProvider provider) throws IOException,
                JsonGenerationException {
            if (!(value instanceof Timestamp)) {
                value = new Timestamp(value.getTime());
            }
            Timestamp ts = (Timestamp) value;
            long milliSeconds = ts.getTime();
            int nano = ts.getNanos();
            milliSeconds -= nano / 1000000;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds);
            jgen.writeString(seconds + ":" + nano);
        }
    };

    private static final JsonDeserializer<Timestamp> TIMESTAMP_DESERIALIZER = new FromStringDeserializer<Timestamp>(Timestamp.class) {
        private static final long serialVersionUID = 4959439642542829L;

        @Override
        protected Timestamp _deserialize(String value,
                                         DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            if (OLD_FORMAT_TIME.matcher(value).find()) {
                return Timestamp.valueOf(value);
            }

            String[] formatStr = value.split(":");
            if (formatStr.length != 2) {
                throw new IllegalArgumentException("date format " + value +" is illegal");
            }

            long seconds = Long.parseLong(formatStr[0]);
            long milliSeconds = TimeUnit.SECONDS.toMillis(seconds);
            int nanoTime = Integer.parseInt(formatStr[1]);
            Timestamp ts = new Timestamp(milliSeconds);
            ts.setNanos(nanoTime);
            return ts;
        }
    };

    // Wrap the serializer and deserializer for Java 8 classes so that these
    // classes are not loaded when JackMapper class initializes. This makes it
    // possible for Java 6 or 7 programs to use JacksonMapper.
    @SuppressLint("NewApi")
    private static class Java8Support {
        private static final JsonSerializer<Instant> INSTANT_SERIALIZER = new StdScalarSerializer<Instant>(Instant.class) {
            private static final long serialVersionUID = -8311364944098261488L;

            @Override
            public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeString(value.getEpochSecond() + ":" + value.getNano());
            }
        };

        private static final JsonDeserializer<Instant> INSTANT_DESERIALIZER = new FromStringDeserializer<Instant>(Instant.class) {
            private static final long serialVersionUID = -5195214836168335571L;

            @Override
            protected Instant _deserialize(String value, DeserializationContext ctxt) throws IOException {
                String[] formatStr = value.split(":");
                if (formatStr.length != 2) {
                    throw new IllegalArgumentException("time format " + value +" is illegal");
                }
                return Instant.ofEpochSecond(Long.parseLong(formatStr[0]), Integer.parseInt(formatStr[1]));
            }
        };
    }

    // XXX: Because jackson does not support derived class in collection(Map/List),
    //  and the form of data in Result between service and App was Map<String, Object> or List ,
    //  so we add special serializer for Map<String, Object> and List.
    //  the more information can see:
    //  http://jira.codehaus.org/browse/JACKSON-544,
    //  and the content of "5. Known Issues" in : http://wiki.fasterxml.com/JacksonPolymorphicDeserialization
    private static final StdScalarSerializer<Map<Object, Object>> MAP_SERIALIZER = new StdScalarSerializer<Map<Object, Object>>(
            Map.class, false) {
        private static final long serialVersionUID = 2639614593299551041L;

        @Override
        public void serialize(Map<Object, Object> map,
                              JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonGenerationException {
            jgen.writeStartObject();
            for (Map.Entry<Object, Object> e: map.entrySet()) {
                //  XXX: The serializer is a bit different from StdKeySerializer,
                //        And SimpleModule.setKeySerializers, SimpleModule.setKeyDeserializers may not produce a result.
                //        the Map to be json must be Map<String, Object> format, the Key of map must be String,
                //        which is standard json data format.
                //        the more information can see:
                //        http://stackoverflow.com/questions/11628698/can-we-make-object-as-key-in-map-when-using-json?rq=1
                jgen.writeFieldName(e.getKey().toString());
                jgen.writeObject(e.getValue());
            }
            jgen.writeEndObject();
        }
    };

    private static final StdScalarSerializer<List<?>> LIST_SERIALIZER = new StdScalarSerializer<List<?>>(List.class, false) {
        private static final long serialVersionUID = 7157572708154175514L;

        @Override
        public void serialize(List<?> list, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonGenerationException {
            jgen.writeStartArray();
            for (Object a : list) {
                jgen.writeObject(a);
            }
            jgen.writeEndArray();
        }
    };

    public static class JacksonException extends JsonProcessingException {
        private static final long serialVersionUID = 1023567556331673452L;

        public JacksonException(String msg) {
            super(msg);
        }

        protected JacksonException(String msg, Throwable rootCause) {
            super(msg, rootCause);
        }
    }
}
