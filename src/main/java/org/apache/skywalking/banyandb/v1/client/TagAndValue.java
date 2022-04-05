/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.banyandb.v1.client;

import java.util.List;

import com.google.protobuf.ByteString;
import lombok.EqualsAndHashCode;
import org.apache.skywalking.banyandb.model.v1.BanyandbModel;

/**
 * TagAndValue represents a value of column in the response
 */
@EqualsAndHashCode(callSuper = true)
public abstract class TagAndValue<T> extends Value<T> {
    protected final String tagName;

    protected TagAndValue(String tagName, T value) {
        super(value);
        this.tagName = tagName;
    }

    /**
     * @return tag name
     */
    public String getTagName() {
        return this.tagName;
    }

    /**
     * @return true if value is null;
     */
    public boolean isNull() {
        return this.value == null;
    }

    static TagAndValue<?> build(BanyandbModel.Tag tag) {
        switch (tag.getValue().getValueCase()) {
            case INT:
                return new LongTagPair(tag.getKey(), tag.getValue().getInt().getValue());
            case STR:
                return new StringTagPair(tag.getKey(), tag.getValue().getStr().getValue());
            case INT_ARRAY:
                return new LongArrayTagPair(tag.getKey(), tag.getValue().getIntArray().getValueList());
            case STR_ARRAY:
                return new StringArrayTagPair(tag.getKey(), tag.getValue().getStrArray().getValueList());
            case BINARY_DATA:
                return new BinaryTagPair(tag.getKey(), tag.getValue().getBinaryData());
            case NULL:
                return new NullTagPair(tag.getKey());
            default:
                throw new IllegalArgumentException("Unrecognized NullType");
        }
    }

    public static class StringTagPair extends TagAndValue<String> {
        StringTagPair(final String tagName, final String value) {
            super(tagName, value);
        }
    }

    public static class StringArrayTagPair extends TagAndValue<List<String>> {
        StringArrayTagPair(final String tagName, final List<String> value) {
            super(tagName, value);
        }
    }

    public static class LongTagPair extends TagAndValue<Long> {
        LongTagPair(final String tagName, final Long value) {
            super(tagName, value);
        }
    }

    public static class LongArrayTagPair extends TagAndValue<List<Long>> {
        LongArrayTagPair(final String tagName, final List<Long> value) {
            super(tagName, value);
        }
    }

    public static class BinaryTagPair extends TagAndValue<ByteString> {
        public BinaryTagPair(String fieldName, ByteString byteString) {
            super(fieldName, byteString);
        }
    }

    public static class NullTagPair extends TagAndValue<Void> {
        NullTagPair(final String tagName) {
            super(tagName, null);
        }

        @Override
        public boolean isNull() {
            return true;
        }
    }
}
