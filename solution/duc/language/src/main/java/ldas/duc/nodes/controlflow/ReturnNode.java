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
package ldas.duc.nodes.controlflow;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import ldas.duc.nodes.ExpressionNode;
import ldas.duc.nodes.StatementNode;
import ldas.duc.runtime.Null;

/**
 * Implementation of the SL return statement. We need to unwind an unknown number of interpreter
 * frames that are between this {@link ReturnNode} and the {@link FunctionBodyNode} of the
 * method we are exiting. This is done by throwing an {@link ReturnException exception} that is
 * caught by the {@link FunctionBodyNode#executeGeneric function body}. The exception transports
 * the return value.
 */
@NodeInfo(shortName = "return", description = "The node implementing a return statement")
public final class ReturnNode extends StatementNode {

    @Child private ExpressionNode valueNode;

    public ReturnNode(ExpressionNode valueNode) {
        this.valueNode = valueNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object result;
        if (valueNode != null) {
            result = valueNode.executeGeneric(frame);
        } else {
            /*
             * Return statement that was not followed by an expression, so return the SL null value.
             */
            result = Null.SINGLETON;
        }
        throw new ReturnException(result);
    }
}
