package org.nutz.lessc4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class LesscServiceTest extends Assert {
    
    private static final Log log = Logs.get();
    
    LesscService lessc;
    
    @Before
    public void before() throws ScriptException {
        lessc = new LesscService();
        lessc.init();
    }

    @Test
    public void test_simple() throws ScriptException {
        String lessStr = ".class { width: (1 + 3) }";
        lessc.render(lessStr);
    }
    
    @Test
    public void test_with_import() throws ScriptException {
        String lessStr = "@import (less) 'demo/another.less';\n.class { width: (1 + 3) }";
        lessc.render(lessStr);
    }

    @Test
    public void test_threadpool_render() throws Exception {
        String lessStr = "@import (less) 'demo/another.less';\n.class { width: (1 + 3) }";
        String _result = lessc.render(lessStr, "/demo2/");
        int thread = 1; // 只支持单线程
        ExecutorService es = Executors.newFixedThreadPool(thread);
        AtomicLong counter = new AtomicLong();
        for (int i = 0; i < 1000; i++) {
            es.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Stopwatch sw = Stopwatch.begin();
                    String result = lessc.render(lessStr);
                    sw.stop();
                    if (!result.equals(_result)) {
                        System.err.println("fuck! --> " + result);
                    }
                    else {
                        counter.incrementAndGet();
                    }
                    
                    log.info(sw);
                    return null;
                }
            });
        }
        es.shutdown();
        es.awaitTermination(60, TimeUnit.SECONDS);
        System.out.println(_result);
        assertEquals(1000, counter.get());
    }

}
