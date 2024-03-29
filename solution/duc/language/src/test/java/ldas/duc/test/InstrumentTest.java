/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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
package ldas.duc.test;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Scope;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.*;
import com.oracle.truffle.api.instrumentation.StandardTags.CallTag;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.tck.DebuggerTester;
import ldas.duc.runtime.BigNumber;
import org.graalvm.polyglot.*;
import org.junit.Assume;
import org.junit.Test;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

/**
 * Test of SL instrumentation.
 */
public class InstrumentTest {

    @Test
    public void testLexicalScopes() throws Exception {
        String code = "function test(n) {\n" +
                        "  a = 1;\n" +          // 2
                        "  if (a > 0) {\n" +
                        "    b = 10;\n" +
                        "    println(b);\n" +   // 5
                        "  }\n" +
                        "  if (a == 1) {\n" +
                        "    b = 20;\n" +
                        "    a = 0;\n" +
                        "    c = 1;\n" +        // 10
                        "    if (b > 0) {\n" +
                        "      a = 4;\n" +
                        "      b = 5;\n" +
                        "      c = 6;\n" +
                        "      d = 7;\n" +      // 15
                        "      println(d);\n" +
                        "    }\n" +
                        "  }\n" +
                        "  println(b);\n" +
                        "  println(a);\n" +     // 20
                        "}\n" +
                        "function main() {\n" +
                        "  test(\"n_n\");\n" +
                        "}";
        Source source = Source.newBuilder("duc", code, "testing").build();
        List<Throwable> throwables;
        try (Engine engine = Engine.newBuilder().out(new java.io.OutputStream() {
            // null output stream
            @Override
            public void write(int b) throws IOException {
            }
        }).build()) {
            Instrument envInstr = engine.getInstruments().get("testEnvironmentHandlerInstrument");
            TruffleInstrument.Env env = envInstr.lookup(Environment.class).env;
            throwables = new ArrayList<>();
            env.getInstrumenter().attachExecutionEventListener(SourceSectionFilter.newBuilder().lineIn(1, source.getLineCount()).build(), new ExecutionEventListener() {
                @Override
                public void onEnter(EventContext context, VirtualFrame frame) {
                    Node node = context.getInstrumentedNode();
                    Iterable<Scope> lexicalScopes = env.findLocalScopes(node, null);
                    Iterable<Scope> dynamicScopes = env.findLocalScopes(node, frame);
                    try {
                        verifyLexicalScopes(lexicalScopes, dynamicScopes, context.getInstrumentedSourceSection().getStartLine(), frame.materialize());
                    } catch (ThreadDeath t) {
                        throw t;
                    } catch (Throwable t) {
                        CompilerDirectives.transferToInterpreter();
                        PrintStream lsErr = System.err;
                        lsErr.println("Line = " + context.getInstrumentedSourceSection().getStartLine());
                        lsErr.println("Node = " + node + ", class = " + node.getClass().getName());
                        t.printStackTrace(lsErr);
                        throwables.add(t);
                    }
                }

                @Override
                public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
                }

                @Override
                public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
                }
            });
            Context.newBuilder().engine(engine).build().eval(source);
        }
        assertTrue(throwables.toString(), throwables.isEmpty());
    }

    @CompilerDirectives.TruffleBoundary
    private static void verifyLexicalScopes(Iterable<Scope> lexicalScopes, Iterable<Scope> dynamicScopes, int line, MaterializedFrame frame) {
        int depth = 0;
        switch (line) {
            case 1:
                break;
            case 2:
                for (Scope ls : lexicalScopes) {
                    // Test that ls.getNode() returns the current root node:
                    checkRootNode(ls, "test", frame);
                    TruffleObject arguments = (TruffleObject) ls.getArguments();
                    checkVars(arguments, "n", null);
                    TruffleObject variables = (TruffleObject) ls.getVariables();
                    checkVars(variables, "n", null);
                    depth++;
                }
                assertEquals("LexicalScope depth", 1, depth);
                depth = 0;
                for (Scope ls : dynamicScopes) {
                    // Test that ls.getNode() returns the current root node:
                    checkRootNode(ls, "test", frame);
                    TruffleObject arguments = (TruffleObject) ls.getArguments();
                    checkVars(arguments, "n", "n_n");
                    TruffleObject variables = (TruffleObject) ls.getVariables();
                    checkVars(variables, "n", "n_n");
                    depth++;
                }
                assertEquals("DynamicScope depth", 1, depth);
                break;
            case 3:
            case 7:
            case 19:
            case 20:
                for (Scope ls : lexicalScopes) {
                    checkRootNode(ls, "test", frame);
                    TruffleObject arguments = (TruffleObject) ls.getArguments();
                    checkVars(arguments, "n", null);
                    TruffleObject variables = (TruffleObject) ls.getVariables();
                    checkVars(variables, "n", null, "a", null);
                    depth++;
                }
                assertEquals("LexicalScope depth", 1, depth);
                depth = 0;
                for (Scope ls : dynamicScopes) {
                    checkRootNode(ls, "test", frame);
                    TruffleObject arguments = (TruffleObject) ls.getArguments();
                    checkVars(arguments, "n", "n_n");
                    TruffleObject variables = (TruffleObject) ls.getVariables();
                    long aVal = (line < 19) ? 1L : 4L;
                    checkVars(variables, "n", "n_n", "a", aVal);
                    depth++;
                }
                assertEquals("DynamicScope depth", 1, depth);
                break;
            case 4:
            case 8:
                for (Scope ls : lexicalScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "n", null, "a", null);
                        TruffleObject arguments = (TruffleObject) ls.getArguments();
                        checkVars(arguments, "n", null);
                    }
                    depth++;
                }
                assertEquals("LexicalScope depth", 2, depth);
                depth = 0;
                for (Scope ls : dynamicScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "n", "n_n", "a", 1L);
                        TruffleObject arguments = (TruffleObject) ls.getArguments();
                        checkVars(arguments, "n", "n_n");
                    }
                    depth++;
                }
                assertEquals("DynamicScope depth", 2, depth);
                break;
            case 5:
            case 9:
            case 10:
                for (Scope ls : lexicalScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "b", null);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "n", null, "a", null);
                        TruffleObject arguments = (TruffleObject) ls.getArguments();
                        checkVars(arguments, "n", null);
                    }
                    depth++;
                }
                assertEquals("LexicalScope depth", 2, depth);
                depth = 0;
                for (Scope ls : dynamicScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        long bVal = (line == 5) ? 10L : 20L;
                        checkVars(variables, "b", bVal);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        long aVal = (line == 10) ? 0L : 1L;
                        checkVars(variables, "n", "n_n", "a", aVal);
                        TruffleObject arguments = (TruffleObject) ls.getArguments();
                        checkVars(arguments, "n", "n_n");
                    }
                    depth++;
                }
                assertEquals("DynamicScope depth", 2, depth);
                break;
            case 11:
                for (Scope ls : lexicalScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "b", null, "c", null);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                    }
                    depth++;
                }
                assertEquals("LexicalScope depth", 2, depth);
                depth = 0;
                for (Scope ls : dynamicScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "b", 20L, "c", 1L);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                    }
                    depth++;
                }
                assertEquals("DynamicScope depth", 2, depth);
                break;
            case 12:
            case 13:
            case 14:
            case 15:
                for (Scope ls : lexicalScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables);
                        assertNull(ls.getArguments());
                    } else if (depth == 1) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "b", null, "c", null);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "n", null, "a", null);
                    }
                    depth++;
                }
                assertEquals("LexicalScope depth", 3, depth);
                depth = 0;
                for (Scope ls : dynamicScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables);
                        assertNull(ls.getArguments());
                    } else if (depth == 1) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        long bVal = (line < 14) ? 20L : 5L;
                        long cVal = (line < 15) ? 1L : 6L;
                        checkVars(variables, "b", bVal, "c", cVal);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        long aVal = (line == 12) ? 0L : 4L;
                        checkVars(variables, "n", "n_n", "a", aVal);
                    }
                    depth++;
                }
                assertEquals("DynamicScope depth", 3, depth);
                break;
            case 16:
                for (Scope ls : lexicalScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "d", null);
                        assertNull(ls.getArguments());
                    } else if (depth == 1) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "b", null, "c", null);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "n", null, "a", null);
                    }
                    depth++;
                }
                assertEquals("LexicalScope depth", 3, depth);
                depth = 0;
                for (Scope ls : dynamicScopes) {
                    if (depth == 0) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "d", 7L);
                        assertNull(ls.getArguments());
                    } else if (depth == 1) {
                        checkBlock(ls);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "b", 5L, "c", 6L);
                        assertNull(ls.getArguments());
                    } else {
                        checkRootNode(ls, "test", frame);
                        TruffleObject variables = (TruffleObject) ls.getVariables();
                        checkVars(variables, "n", "n_n", "a", 4L);
                    }
                    depth++;
                }
                assertEquals("DynamicScope depth", 3, depth);
                break;
            case 22:
            case 23:
                for (Scope ls : lexicalScopes) {
                    checkRootNode(ls, "main", frame);
                    TruffleObject arguments = (TruffleObject) ls.getArguments();
                    checkVars(arguments);
                    TruffleObject variables = (TruffleObject) ls.getVariables();
                    checkVars(variables);
                    depth++;
                }
                assertEquals("LexicalScope depth", 1, depth);
                depth = 0;
                for (Scope ls : dynamicScopes) {
                    checkRootNode(ls, "main", frame);
                    TruffleObject arguments = (TruffleObject) ls.getArguments();
                    checkVars(arguments);
                    TruffleObject variables = (TruffleObject) ls.getVariables();
                    checkVars(variables);
                    depth++;
                }
                assertEquals("DynamicScope depth", 1, depth);
                break;
            default:
                fail("Untested line: " + line);
                break;
        }
    }

    private static void checkRootNode(Scope ls, String name, MaterializedFrame frame) {
        assertEquals(name, ls.getName());
        Node node = ls.getNode();
        assertTrue(node.getClass().getName(), node instanceof RootNode);
        assertEquals(name, ((RootNode) node).getName());
        assertEquals(frame.getFrameDescriptor(), ((RootNode) node).getFrameDescriptor());
    }

    private static void checkBlock(Scope ls) {
        assertEquals("block", ls.getName());
        // Test that ls.getNode() does not return the current root node, it ought to be a block node
        Node node = ls.getNode();
        assertNotNull(node);
        assertFalse(node.getClass().getName(), node instanceof RootNode);
    }

    private static boolean contains(TruffleObject vars, String key) {
        return KeyInfo.isExisting(ForeignAccess.sendKeyInfo(Message.KEY_INFO.createNode(), vars, key));
    }

    private static Object read(TruffleObject vars, String key) {
        try {
            return ForeignAccess.sendRead(Message.READ.createNode(), vars, key);
        } catch (UnknownIdentifierException | UnsupportedMessageException e) {
            throw new AssertionError(e);
        }
    }

    private static boolean isNull(TruffleObject vars) {
        return ForeignAccess.sendIsNull(Message.IS_NULL.createNode(), vars);
    }

    private static int keySize(TruffleObject vars) {
        try {
            Object keys = ForeignAccess.sendKeys(Message.KEYS.createNode(), vars);
            return ((Number) ForeignAccess.sendGetSize(Message.GET_SIZE.createNode(), (TruffleObject) keys)).intValue();
        } catch (UnsupportedMessageException e) {
            throw new AssertionError(e);
        }
    }

    private static void checkVars(TruffleObject vars, Object... expected) {
        for (int i = 0; i < expected.length; i += 2) {
            String name = (String) expected[i];
            Object value = expected[i + 1];
            assertTrue(name, contains(vars, name));
            if (value != null) {
                assertEquals(name, value, read(vars, name));
            } else {
                assertTrue(isNull((TruffleObject) read(vars, name)));
            }
        }
        assertEquals(expected.length / 2, keySize(vars));
    }

    @Test
    public void testOutput() throws IOException {
        String code = "function main() {\n" +
                        "  f = fac(5);\n" +
                        "  println(f);\n" +
                        "}\n" +
                        "function fac(n) {\n" +
                        "  println(n);\n" +
                        "  if (n <= 1) {\n" +
                        "    return 1;\n" + // break
                        "  }\n" +
                        "  return n * fac(n - 1);\n" +
                        "}\n";
        String fullOutput = "5\n4\n3\n2\n1\n120\n";
        String fullLines = "[5, 4, 3, 2, 1, 120]";
        // Pure exec:
        Source source = Source.newBuilder("duc", code, "testing").build();
        ByteArrayOutputStream engineOut = new ByteArrayOutputStream();
        Engine engine = Engine.newBuilder().out(engineOut).build();
        Context context = Context.newBuilder().engine(engine).build();
        context.eval(source);
        String engineOutput = fullOutput;
        assertEquals(engineOutput, engineOut.toString());

        // Check output
        Instrument outInstr = engine.getInstruments().get("testEnvironmentHandlerInstrument");
        TruffleInstrument.Env env = outInstr.lookup(Environment.class).env;
        ByteArrayOutputStream consumedOut = new ByteArrayOutputStream();
        EventBinding<ByteArrayOutputStream> outputConsumerBinding = env.getInstrumenter().attachOutConsumer(consumedOut);
        assertEquals(0, consumedOut.size());
        context.eval(source);
        BufferedReader fromOutReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(consumedOut.toByteArray())));
        engineOutput = engineOutput + fullOutput;
        assertEquals(engineOutput, engineOut.toString());
        assertTrue(fromOutReader.ready());
        assertEquals(fullLines, readLinesList(fromOutReader));

        // Check two output readers
        ByteArrayOutputStream consumedOut2 = new ByteArrayOutputStream();
        EventBinding<ByteArrayOutputStream> outputConsumerBinding2 = env.getInstrumenter().attachOutConsumer(consumedOut2);
        assertEquals(0, consumedOut2.size());
        context.eval(source);
        fromOutReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(consumedOut.toByteArray())));
        BufferedReader fromOutReader2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(consumedOut2.toByteArray())));
        engineOutput = engineOutput + fullOutput;
        assertEquals(engineOutput, engineOut.toString());
        assertTrue(fromOutReader.ready());
        assertTrue(fromOutReader2.ready());
        String fullLines2x = fullLines.substring(0, fullLines.length() - 1) + ", " + fullLines.substring(1);
        assertEquals(fullLines2x, readLinesList(fromOutReader));
        assertEquals(fullLines, readLinesList(fromOutReader2));

        // One output reader closes, the other still receives the output
        outputConsumerBinding.dispose();
        consumedOut.reset();
        consumedOut2.reset();
        context.eval(source);
        engineOutput = engineOutput + fullOutput;
        assertEquals(engineOutput, engineOut.toString());
        assertEquals(0, consumedOut.size());
        assertTrue(consumedOut2.size() > 0);
        fromOutReader2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(consumedOut2.toByteArray())));
        assertEquals(fullLines, readLinesList(fromOutReader2));

        // Remaining closes and pure exec successful:
        consumedOut2.reset();
        outputConsumerBinding2.dispose();
        context.eval(source);
        engineOutput = engineOutput + fullOutput;
        assertEquals(engineOutput, engineOut.toString());
        assertEquals(0, consumedOut.size());
        assertEquals(0, consumedOut2.size());

    }

    String readLinesList(BufferedReader br) throws IOException {
        List<String> lines = new ArrayList<>();
        while (br.ready()) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            lines.add(line);
        }
        return lines.toString();
    }

    /**
     * Test that we reenter a node whose execution was interrupted. Unwind just the one node off.
     */
    @Test
    public void testRedoIO() throws Throwable {
        String code = "function main() {\n" +
                        "  a = readln();\n" +
                        "  return a;\n" +
                        "}\n";
        final Source ioWait = Source.newBuilder("duc", code, "testing").build();
        final TestRedoIO[] redoIOPtr = new TestRedoIO[1];
        InputStream strIn = new ByteArrayInputStream("O.K.".getBytes());
        InputStream delegateInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                synchronized (InstrumentTest.class) {
                    // Block reading before we do unwind:
                    if (redoIOPtr[0].beforePop) {
                        redoIOPtr[0].inRead.release();
                        try {
                            InstrumentTest.class.wait();
                        } catch (InterruptedException ex) {
                            throw new RuntimeInterruptedException();
                        }
                    }
                }
                return strIn.read();
            }
        };
        Engine engine = Engine.newBuilder().in(delegateInputStream).build();
        TestRedoIO redoIO = engine.getInstruments().get("testRedoIO").lookup(TestRedoIO.class);
        redoIOPtr[0] = redoIO;
        redoIO.inRead.drainPermits();
        Context context = Context.newBuilder().engine(engine).build();
        Value ret = context.eval(ioWait);
        assertEquals("O.K.", ret.asString());
        assertFalse(redoIO.beforePop);
    }

    private static class RuntimeInterruptedException extends RuntimeException {
        private static final long serialVersionUID = -4735601164894088571L;
    }

    @TruffleInstrument.Registration(id = "testRedoIO", services = TestRedoIO.class)
    public static class TestRedoIO extends TruffleInstrument {

        boolean beforePop = true;
        Semaphore inRead = new Semaphore(1);

        @Override
        protected void onCreate(Env env) {
            env.registerService(env.getInstrumenter());
            env.registerService(this);
            env.getInstrumenter().attachExecutionEventListener(SourceSectionFilter.ANY, new ExecutionEventListener() {
                @Override
                public void onEnter(EventContext context, VirtualFrame frame) {
                    if ("readln".equals(context.getInstrumentedSourceSection().getCharacters())) {
                        CompilerDirectives.transferToInterpreter();
                        // Interrupt the I/O
                        final Thread thread = Thread.currentThread();
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    inRead.acquire();
                                } catch (InterruptedException ex) {
                                }
                                synchronized (InstrumentTest.class) {
                                    if (beforePop) {
                                        thread.interrupt();
                                    }
                                }
                            }
                        }.start();
                    }
                }

                @Override
                public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
                }

                @Override
                public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
                    if (exception instanceof RuntimeInterruptedException) {
                        CompilerDirectives.transferToInterpreter();
                        synchronized (InstrumentTest.class) {
                            beforePop = false;
                        }
                        throw context.createUnwind(null);
                    }
                }

                @Override
                public Object onUnwind(EventContext context, VirtualFrame frame, Object info) {
                    return ProbeNode.UNWIND_ACTION_REENTER;
                }
            });
        }

    }

    /**
     * Test that we can forcibly return early from call nodes with an arbitrary value.
     */
    @Test
    public void testEarlyReturn() throws Exception {
        Assume.assumeFalse("Crashes on AArch64 in C2 (GR-8733)", System.getProperty("os.arch").equalsIgnoreCase("aarch64"));
        String code = "function main() {\n" +
                        "  a = 10;\n" +
                        "  b = a;\n" +
                        "  // Let fce() warm up and specialize:\n" +
                        "  while (a == b && a < 100000) {\n" +
                        "    a = fce(a);\n" +
                        "    b = b + 1;\n" +
                        "  }\n" +
                        "  c = a;\n" +
                        "  // Run fce() and alter it's return type in an instrument:\n" +
                        "  c = fce(c);\n" +
                        "  return c;\n" +
                        "}\n" +
                        "function fce(x) {\n" +
                        "  return x + 1;\n" +
                        "}\n";
        final Source source = Source.newBuilder("duc", code, "testing").build();
        ByteArrayOutputStream engineOut = new ByteArrayOutputStream();
        Engine engine = Engine.newBuilder().err(engineOut).build();
        Context context = Context.newBuilder().engine(engine).build();
        // No instrument:
        Value ret = context.eval(source);
        assertTrue(ret.isNumber());
        assertEquals(100001L, ret.asLong());

        EarlyReturnInstrument earlyReturn = context.getEngine().getInstruments().get("testEarlyReturn").lookup(EarlyReturnInstrument.class);

        earlyReturn.fceCode = "fce(a)";
        earlyReturn.returnValue = 200000L;
        ret = context.eval(source);
        assertTrue(ret.isNumber());
        assertEquals(200001L, ret.asLong());

        earlyReturn.returnValue = "Hello!";
        ret = context.eval(source);
        assertFalse(ret.isNumber());
        assertTrue(ret.isString());
        assertEquals("Hello!1", ret.asString());

        // Specialize to long again:
        earlyReturn.fceCode = "<>";
        ret = context.eval(source);
        assertTrue(ret.isNumber());
        assertEquals(100001L, ret.asLong());

        earlyReturn.fceCode = "fce(a)";
        earlyReturn.returnValue = new BigInteger("-42");
        boolean interopFailure;
        try {
            context.eval(source);
            interopFailure = false;
        } catch (PolyglotException err) {
            interopFailure = true;
        }
        assertTrue(interopFailure);

        earlyReturn.returnValue = new BigNumber(new BigInteger("-42"));
        ret = context.eval(source);
        assertTrue(ret.isNumber());
        assertEquals(-41L, ret.asLong());

        earlyReturn.fceCode = "fce(c)";
        earlyReturn.returnValue = Boolean.TRUE;
        ret = context.eval(source);
        assertTrue(ret.isBoolean());
        assertEquals(Boolean.TRUE, ret.asBoolean());

        earlyReturn.fceCode = "fce(c)";
        earlyReturn.returnValue = -42.42;
        ret = context.eval(source);
        assertTrue(ret.isNumber());
        assertEquals(-42.42, ret.asDouble(), 1e-8);

        earlyReturn.fceCode = "fce(c)";
        earlyReturn.returnValue = "Hello!";
        ret = context.eval(source);
        assertTrue(ret.isString());
        assertEquals("Hello!", ret.asString());

    }

    @TruffleInstrument.Registration(id = "testEarlyReturn", services = EarlyReturnInstrument.class)
    public static class EarlyReturnInstrument extends TruffleInstrument {

        String fceCode;      // return when this code is hit
        Object returnValue;  // return this value

        @Override
        protected void onCreate(Env env) {
            env.registerService(this);
            env.getInstrumenter().attachExecutionEventListener(SourceSectionFilter.newBuilder().tagIs(CallTag.class).build(), new ExecutionEventListener() {
                @Override
                public void onEnter(EventContext context, VirtualFrame frame) {
                }

                @Override
                public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
                    if (fceCode.equals(context.getInstrumentedSourceSection().getCharacters())) {
                        CompilerDirectives.transferToInterpreter();
                        throw context.createUnwind(null);
                    }
                }

                @Override
                public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
                }

                @Override
                public Object onUnwind(EventContext context, VirtualFrame frame, Object info) {
                    return returnValue;
                }

            });
        }

    }

    /**
     * This test demonstrates that it's possible to easily replace a return value of any node using
     * {@link ExecutionEventListener#onUnwind(com.oracle.truffle.api.instrumentation.EventContext, com.oracle.truffle.api.frame.VirtualFrame, java.lang.Object)}
     * .
     */
    @Test
    public void testReplaceNodeReturnValue() throws Exception {
        String code = "function main() {\n" +
                        "  a = new();\n" +
                        "  b = a.rp1;\n" +
                        "  return b;\n" +
                        "}\n";
        final Source source = Source.newBuilder("duc", code, "testing").build();
        SourceSection ss = DebuggerTester.getSourceImpl(source).createSection(24, 5);
        Context context = Context.create();
        NewReplacedInstrument replaced = context.getEngine().getInstruments().get("testNewNodeReplaced").lookup(NewReplacedInstrument.class);
        replaced.attachAt(ss);

        Value ret = context.eval(source);
        assertEquals("Replaced Value", ret.toString());
    }

    @TruffleInstrument.Registration(id = "testNewNodeReplaced", services = NewReplacedInstrument.class)
    public static final class NewReplacedInstrument extends TruffleInstrument {

        private Env env;
        private final Object replacedValue = new ReplacedTruffleObject();

        @Override
        @SuppressWarnings("hiding")
        protected void onCreate(Env env) {
            this.env = env;
            env.registerService(this);
        }

        void attachAt(SourceSection ss) {
            env.getInstrumenter().attachExecutionEventListener(SourceSectionFilter.newBuilder().sourceSectionEquals(ss).build(), new ExecutionEventListener() {
                @Override
                public void onEnter(EventContext context, VirtualFrame frame) {
                }

                @Override
                public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
                    if (result instanceof TruffleObject) {
                        CompilerDirectives.transferToInterpreter();
                        throw context.createUnwind(null);
                    }
                }

                @Override
                public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
                }

                @Override
                public Object onUnwind(EventContext context, VirtualFrame frame, Object info) {
                    return replacedValue;
                }

            });
        }

        static class ReplacedTruffleObject implements TruffleObject {
            @Override
            public ForeignAccess getForeignAccess() {
                return ReplacedTruffleObjectMessageResolutionForeign.ACCESS;
            }

            public static boolean isInstance(TruffleObject obj) {
                return obj instanceof ReplacedTruffleObject;
            }

            @MessageResolution(receiverType = ReplacedTruffleObject.class)
            static final class ReplacedTruffleObjectMessageResolution {

                @Resolve(message = "KEYS")
                abstract static class ReplacedKeysNode extends Node {

                    @SuppressWarnings("unused")
                    public Object access(ReplacedTruffleObject ato) {
                        return new KeysArray(new String[]{"rp1, rp2"});
                    }
                }

                @Resolve(message = "READ")
                abstract static class ReplacedReadNode extends Node {

                    @SuppressWarnings("unused")
                    public Object access(ReplacedTruffleObject ato, String name) {
                        return "Replaced Value";
                    }
                }
            }
        }
    }

    @MessageResolution(receiverType = KeysArray.class)
    static final class KeysArray implements TruffleObject {

        private final String[] keys;

        KeysArray(String[] keys) {
            this.keys = keys;
        }

        @Resolve(message = "HAS_SIZE")
        abstract static class HasSize extends Node {

            public Object access(@SuppressWarnings("unused") KeysArray receiver) {
                return true;
            }
        }

        @Resolve(message = "GET_SIZE")
        abstract static class GetSize extends Node {

            public Object access(KeysArray receiver) {
                return receiver.keys.length;
            }
        }

        @Resolve(message = "READ")
        abstract static class Read extends Node {

            public Object access(KeysArray receiver, int index) {
                try {
                    return receiver.keys[index];
                } catch (IndexOutOfBoundsException e) {
                    CompilerDirectives.transferToInterpreter();
                    throw UnknownIdentifierException.raise(String.valueOf(index));
                }
            }
        }

        @Override
        public ForeignAccess getForeignAccess() {
            return KeysArrayForeign.ACCESS;
        }

        static boolean isInstance(TruffleObject array) {
            return array instanceof KeysArray;
        }

    }

    /**
     * Test that we can alter function arguments on reenter.
     */
    @Test
    public void testChangeArgumentsOnReenter() throws Exception {
        String code = "function main() {\n" +
                        "  y = fce(0, 10000);\n" +
                        "  return y;\n" +
                        "}\n" +
                        "function fce(x, z) {\n" +
                        "  y = 2 * x;\n" +
                        "  if (y < z) {\n" +
                        "    print(\"A bad error.\");\n" +
                        "    return 0 - 1;\n" +
                        "  } else {\n" +
                        "    return y;\n" +
                        "  }\n" +
                        "}\n";
        final Source source = Source.newBuilder("duc", code, "testing").build();
        Context context = Context.create();
        IncreaseArgOnErrorInstrument incOnError = context.getEngine().getInstruments().get("testIncreaseArgumentOnError").lookup(IncreaseArgOnErrorInstrument.class);
        incOnError.attachOn("A bad error");

        Value ret = context.eval(source);
        assertEquals(10000, ret.asInt());
    }

    @TruffleInstrument.Registration(id = "testIncreaseArgumentOnError", services = IncreaseArgOnErrorInstrument.class)
    public static final class IncreaseArgOnErrorInstrument extends TruffleInstrument {

        private Env env;
        @CompilationFinal private ThreadDeath unwind;

        @Override
        @SuppressWarnings("hiding")
        protected void onCreate(Env env) {
            this.env = env;
            env.registerService(this);
        }

        void attachOn(String error) {
            EventBinding<ExecutionEventListener> reenterBinding = env.getInstrumenter().attachExecutionEventListener(SourceSectionFilter.newBuilder().tagIs(StandardTags.RootTag.class).build(),
                            new ExecutionEventListener() {
                                @Override
                                public void onEnter(EventContext context, VirtualFrame frame) {
                                }

                                @Override
                                public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
                                }

                                @Override
                                public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
                                }

                                @Override
                                public Object onUnwind(EventContext context, VirtualFrame frame, Object info) {
                                    frame.getArguments()[0] = (Long) frame.getArguments()[0] + 1;
                                    return ProbeNode.UNWIND_ACTION_REENTER;
                                }

                            });
            env.getInstrumenter().attachExecutionEventListener(SourceSectionFilter.newBuilder().tagIs(StandardTags.StatementTag.class).build(), new ExecutionEventListener() {
                @Override
                public void onEnter(EventContext context, VirtualFrame frame) {
                    SourceSection ss = context.getInstrumentedSourceSection();
                    if (ss.getCharacters().toString().contains(error)) {
                        if (unwind == null) {
                            CompilerDirectives.transferToInterpreterAndInvalidate();
                            unwind = context.createUnwind(null, reenterBinding);
                        }
                        throw unwind;
                    }
                }

                @Override
                public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
                }

                @Override
                public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
                }

            });
        }
    }

    @TruffleInstrument.Registration(id = "testEnvironmentHandlerInstrument", services = Environment.class)
    public static class EnvironmentHandlerInstrument extends TruffleInstrument {

        @Override
        protected void onCreate(final TruffleInstrument.Env env) {
            env.registerService(new Environment(env));
        }
    }

    private static class Environment {

        TruffleInstrument.Env env;

        Environment(TruffleInstrument.Env env) {
            this.env = env;
        }
    }

}
