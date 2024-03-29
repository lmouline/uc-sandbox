/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
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
package ldas.duc.nodes.local;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.*;
import ldas.duc.DucException;
import ldas.duc.nodes.ExpressionNode;

/**
 * Node to write a local variable to a function's {@link VirtualFrame frame}. The Truffle frame API
 * allows to store primitive values of all Java primitive types, and Object values.
 */
@NodeChild("valueNode")
@NodeField(name = "slot", type = FrameSlot.class)
public abstract class WriteLocalVariableNode extends ExpressionNode {

    /**
     * Returns the descriptor of the accessed local variable. The implementation of this method is
     * created by the Truffle DSL based on the {@link NodeField} annotation on the class.
     */
    protected abstract FrameSlot getSlot();

    /**
     * Specialized method to write a primitive {@code long} value. This is only possible if the
     * local variable also has currently the type {@code long} or was never written before,
     * therefore a Truffle DSL {@link #isLong(VirtualFrame) custom guard} is specified.
     */
    @Specialization(guards = "isLong(frame)")
    protected long writeLong(VirtualFrame frame, long value) {
        /* Initialize type on first write of the local variable. No-op if kind is already Long. */
        getSlot().setKind(FrameSlotKind.Long);

        FrameUtil.getLongSafe(frame, getSlot());

        frame.setLong(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isInt(frame)")
    protected int writeInt(VirtualFrame frame, int value) {
        getSlot().setKind(FrameSlotKind.Int);

        //fixme
        try {
            frame.getInt(getSlot());
        } catch (FrameSlotTypeException e) {
            Object oInt = FrameUtil.getObjectSafe(frame, getSlot());
            if (!(oInt instanceof Integer)) {
                throw new IllegalStateException(e);
            }
        }

        frame.setInt(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isDouble(frame)")
    protected double writeDouble(VirtualFrame frame, double value) {
        getSlot().setKind(FrameSlotKind.Double);

        //fixme
        try {
            frame.getDouble(getSlot());
        } catch (FrameSlotTypeException e) {
            Object oDouble = FrameUtil.getObjectSafe(frame, getSlot());
            if (!(oDouble instanceof Double)) {
                throw new IllegalStateException(e);
            }
        }

        frame.setDouble(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isBoolean(frame)")
    protected boolean writeBoolean(VirtualFrame frame, boolean value) {
        /* Initialize type on first write of the local variable. No-op if kind is already Long. */
        getSlot().setKind(FrameSlotKind.Boolean);

        FrameUtil.getBooleanSafe(frame, getSlot());

        frame.setBoolean(getSlot(), value);
        return value;
    }

    @Specialization(guards = "isString(frame, value)")
    protected String writeString(VirtualFrame frame, Object value) {
        /* Initialize type on first write of the local variable. No-op if kind is already Long. */
        getSlot().setKind(FrameSlotKind.Object);

        Object previous = FrameUtil.getObjectSafe(frame, getSlot());
        if(!(previous instanceof String)) {
            throw new DucException("Incompatible types beteween receiver and value: " + previous.getClass() + " != string", this);
        }

        frame.setObject(getSlot(), value);
        return (String) value;
    }

    @Specialization(guards = "isChar(frame, value)")
    protected char writeChar(VirtualFrame frame, Object value) {
        getSlot().setKind(FrameSlotKind.Object);

        Object previous = FrameUtil.getObjectSafe(frame, getSlot());
        if(!(previous instanceof Character)) {
            throw new DucException("Incompatible types beteween receiver and value: " + previous.getClass() + " != char", this);
        }

        frame.setObject(getSlot(), value);
        return (Character) value;
    }

    /**
     * Generic write method that works for all possible types.
     * <p>
     * Why is this method annotated with {@link Specialization} and not {@link Fallback}? For a
     * {@link Fallback} method, the Truffle DSL generated code would try all other specializations
     * first before calling this method. We know that all these specializations would fail their
     * guards, so there is no point in calling them. Since this method takes a value of type
     * {@link Object}, it is guaranteed to never fail, i.e., once we are in this specialization the
     * node will never be re-specialized.
     */
    @Specialization(replaces = {"writeLong", "writeBoolean", "writeInt", "writeDouble", "writeString", "writeChar"})
    protected Object write(VirtualFrame frame, Object value) {
        /*
         * Regardless of the type before, the new and final type of the local variable is Object.
         * Changing the slot kind also discards compiled code, because the variable type is
         * important when the compiler optimizes a method.
         *
         * No-op if kind is already Object.
         */
        getSlot().setKind(FrameSlotKind.Object);

        try {
            Object previous = FrameUtil.getObjectSafe(frame, getSlot());
            if(previous != null && previous.getClass() != value.getClass()) {
                throw new DucException("Incompatible types beteween receiver and value: " + previous.getClass() + " != " + value.getClass(), this);
            }
        } catch (IllegalStateException e) {
            throw new DucException("5Incompatible types beteween receiver and value", this);
        }


        frame.setObject(getSlot(), value);
        return value;
    }

    /**
     * Guard function that the local variable has the type {@code long}.
     *
     * @param frame The parameter seems unnecessary, but it is required: Without the parameter, the
     *            Truffle DSL would not check the guard on every execution of the specialization.
     *            Guards without parameters are assumed to be pure, but our guard depends on the
     *            slot kind which can change.
     */
    protected boolean isLong(@SuppressWarnings("unused") VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Long;
    }

    protected boolean isInt(@SuppressWarnings("unused") VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Int;
    }

    protected boolean isDouble(@SuppressWarnings("unused") VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Double;
    }

    protected boolean isBoolean(@SuppressWarnings("unused") VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Boolean;
    }

    protected boolean isString(@SuppressWarnings("unused") VirtualFrame frame, Object value) {
        return getSlot().getKind() == FrameSlotKind.Object && value instanceof String;
    }

    protected boolean isChar(@SuppressWarnings("unused") VirtualFrame frame, Object value) {
        return getSlot().getKind() == FrameSlotKind.Object && value instanceof Character;
    }
}
