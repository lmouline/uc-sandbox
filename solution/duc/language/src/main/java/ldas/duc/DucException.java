/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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
package ldas.duc;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.SourceSection;
import ldas.duc.runtime.BigNumber;
import ldas.duc.runtime.Context;
import ldas.duc.runtime.Function;
import ldas.duc.runtime.Null;

/**
 * Duc does not need a sophisticated error checking and reporting mechanism, so all unexpected
 * conditions just abort execution. This exception class is used when we abort from within the Duc
 * implementation.
 */
public class DucException extends RuntimeException implements TruffleException {

    private static final long serialVersionUID = -6799734410727348507L;

    private final Node location;

    @TruffleBoundary
    public DucException(String message, Node location) {
        super(message);
        this.location = location;
    }

    @SuppressWarnings("sync-override")
    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

    public Node getLocation() {
        return location;
    }

    /**
     * Provides a user-readable message for run-time type errors. Duc is strongly typed, i.e., there
     * are no automatic type conversions of values.
     */
    @TruffleBoundary
    public static DucException typeError(Node operation, Object... values) {
        StringBuilder result = new StringBuilder();
        result.append("Type error");

        if (operation != null) {
            SourceSection ss = operation.getEncapsulatingSourceSection();
            if (ss != null && ss.isAvailable()) {
                result.append(" at ").append(ss.getSource().getName()).append(" line ").append(ss.getStartLine()).append(" col ").append(ss.getStartColumn());
            }
        }

        result.append(": operation");
        if (operation != null) {
            NodeInfo nodeInfo = Context.lookupNodeInfo(operation.getClass());
            if (nodeInfo != null) {
                result.append(" \"").append(nodeInfo.shortName()).append("\"");
            }
        }

        result.append(" not defined for");

        String sep = " ";
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            result.append(sep);
            sep = ", ";
            if (value instanceof Long || value instanceof BigNumber) {
                result.append("Number ").append(value);
            } else if (value instanceof Boolean) {
                result.append("Boolean ").append(value);
            } else if (value instanceof String) {
                result.append("String \"").append(value).append("\"");
            } else if (value instanceof Function) {
                result.append("Function ").append(value);
            } else if (value == Null.SINGLETON) {
                result.append("NULL");
            } else if (value == null) {
                // value is not evaluated because of short circuit evaluation
                result.append("ANY");
            } else {
                result.append(value);
            }
        }
        return new DucException(result.toString(), operation);
    }

}
