/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ldas.duc.nodes.interop;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.Node;
import ldas.duc.nodes.Types;
import ldas.duc.runtime.Context;
import ldas.duc.runtime.Null;

/**
 * The node for converting a foreign primitive or boxed primitive value to an SL value.
 */
@TypeSystemReference(Types.class)
public abstract class ForeignToDucTypeNode extends Node {

    public abstract Object executeConvert(Object value);

    @Specialization
    protected static Object fromObject(Number value) {
        return Context.fromForeignValue(value);
    }

    @Specialization
    protected static Object fromString(String value) {
        return value;
    }

    @Specialization
    protected static Object fromBoolean(boolean value) {
        return value;
    }

    @Specialization
    protected static Object fromChar(char value) {
        return String.valueOf(value);
    }

    /*
     * In case the foreign object is a boxed primitive we unbox it using the UNBOX message.
     */
    @Specialization(guards = "isBoxedPrimitive(value)")
    public Object unbox(TruffleObject value) {
        Object unboxed = doUnbox(value);
        return Context.fromForeignValue(unboxed);
    }

    @Specialization(guards = "!isBoxedPrimitive(value)")
    public Object fromTruffleObject(TruffleObject value) {
        return value;
    }

    @Child private Node isBoxed;

    protected final boolean isBoxedPrimitive(TruffleObject object) {
        if (isBoxed == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            isBoxed = insert(Message.IS_BOXED.createNode());
        }
        return ForeignAccess.sendIsBoxed(isBoxed, object);
    }

    @Child private Node unbox;

    protected final Object doUnbox(TruffleObject value) {
        if (unbox == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            unbox = insert(Message.UNBOX.createNode());
        }
        try {
            return ForeignAccess.sendUnbox(unbox, value);
        } catch (UnsupportedMessageException e) {
            return Null.SINGLETON;
        }
    }

    public static ForeignToDucTypeNode create() {
        return ForeignToDucTypeNodeGen.create();
    }
}
