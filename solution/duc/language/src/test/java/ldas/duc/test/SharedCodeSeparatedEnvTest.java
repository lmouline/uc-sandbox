/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
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

import com.oracle.truffle.api.instrumentation.EventBinding;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import ldas.duc.DucLanguage;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Instrument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SharedCodeSeparatedEnvTest {

    private ByteArrayOutputStream osRuntime;
    private ByteArrayOutputStream os1;
    private ByteArrayOutputStream os2;
    private Engine engine;
    private Context e1;
    private Context e2;

    @Before
    public void initializeEngines() {
        osRuntime = new ByteArrayOutputStream();
        engine = Engine.newBuilder().out(osRuntime).err(osRuntime).build();

        os1 = new ByteArrayOutputStream();
        os2 = new ByteArrayOutputStream();

        int instances = DucLanguage.counter;
        // @formatter:off
        e1 = Context.newBuilder("duc").engine(engine).out(os1).build();
        e1.getPolyglotBindings().putMember("extra", 1);
        e2 = Context.newBuilder("duc").engine(engine).out(os2).build();
        e2.getPolyglotBindings().putMember("extra", 2);
        e1.initialize("duc");
        e2.initialize("duc");
        assertEquals("One DucLanguage instance created", instances + 1, DucLanguage.counter);
    }

    @After
    public void closeEngines() {
        engine.close();
    }

    @Test
    public void shareCodeUseDifferentOutputStreams() throws Exception {

        String sayHello =
            "function main() {\n" +
            "  println(\"Ahoj\" + import(\"extra\"));" +
            "}";
        // @formatter:on

        e1.eval("duc", sayHello);
        assertEquals("Ahoj1\n", os1.toString("UTF-8"));
        assertEquals("", os2.toString("UTF-8"));

        e2.eval("duc", sayHello);
        assertEquals("Ahoj1\n", os1.toString("UTF-8"));
        assertEquals("Ahoj2\n", os2.toString("UTF-8"));
    }

    @Test
    public void instrumentsSeeOutputOfBoth() throws Exception {
        Instrument outInstr = e2.getEngine().getInstruments().get("captureOutput");
        ByteArrayOutputStream outConsumer = outInstr.lookup(ByteArrayOutputStream.class);
        assertNotNull("Stream capturing is ready", outConsumer);

        String sayHello = "function main() {\n" +
                        "  println(\"Ahoj\" + import(\"extra\"));" +
                        "}";
        // @formatter:on

        e1.eval("duc", sayHello);
        assertEquals("Ahoj1\n", os1.toString("UTF-8"));
        assertEquals("", os2.toString("UTF-8"));

        e2.eval("duc", sayHello);
        assertEquals("Ahoj1\n", os1.toString("UTF-8"));
        assertEquals("Ahoj2\n", os2.toString("UTF-8"));

        engine.close();

        assertEquals("Output of both contexts and instruments is capturable",
                        "initializingOutputCapture\n" +
                                        "Ahoj1\n" +
                                        "Ahoj2\n" +
                                        "endOfOutputCapture\n",
                        outConsumer.toString("UTF-8"));

        assertEquals("Output of instrument goes not to os runtime if specified otherwise",
                        "initializingOutputCapture\n" + "endOfOutputCapture\n",
                        osRuntime.toString("UTF-8"));
    }

    @TruffleInstrument.Registration(id = "captureOutput", services = ByteArrayOutputStream.class)
    public static class CaptureOutput extends TruffleInstrument {
        private EventBinding<ByteArrayOutputStream> binding;

        @Override
        protected void onCreate(final TruffleInstrument.Env env) {
            final ByteArrayOutputStream capture = new ByteArrayOutputStream() {
                @Override
                public void write(byte[] b) throws IOException {
                    super.write(b);
                }

                @Override
                public synchronized void write(byte[] b, int off, int len) {
                    super.write(b, off, len);
                }

                @Override
                public synchronized void write(int b) {
                    super.write(b);
                }
            };
            binding = env.getInstrumenter().attachOutConsumer(capture);
            env.registerService(capture);
            try {
                env.out().write("initializingOutputCapture\n".getBytes("UTF-8"));
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        protected void onDispose(Env env) {
            try {
                env.out().write("endOfOutputCapture\n".getBytes("UTF-8"));
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
            binding.dispose();
        }
    }
}
