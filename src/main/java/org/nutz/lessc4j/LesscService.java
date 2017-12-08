package org.nutz.lessc4j;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * lessc by nashorn
 * 
 * @author wendal
 *
 */
public class LesscService {

    protected static final Log log = Logs.get();

    /**
     * 脚本引擎就是这样咯
     */
    protected ScriptEngineManager manager = new ScriptEngineManager();
    /**
     * 只支持nashorn,其他的是不存在的
     */
    protected ScriptEngine scriptEngine = manager.getEngineByName("nashorn");
    /**
     * 这是less在nashorn的对象
     */
    protected Object less;
    /**
     * 这是用于执行的预编译脚本
     */
    protected CompiledScript script;

    /**
     * 本方法必须先调用一次,而且很慢,起码2秒
     */
    public void init() throws ScriptException {
        // 相当于上下文
        Bindings bindings = scriptEngine.createBindings();
        // 放个map,用于提取返回值
        NutMap _result = new NutMap();
        bindings.put("_result", _result);
        bindings.put("lessc4j", this); // 读取js和less文件的时候需要用
        // 来吧,执行,很慢的
        scriptEngine.eval(readJs("/lessc4j/entry-point.js"), bindings);
        // 取出less对象
        less = _result.get("less");
        // 生成预编译脚本,这一步很快的
        script = ((Compilable) scriptEngine).compile("var options = {};options.paths=paths;less.render(lessStr, options).then(function (output) {_result.put('css', output.css);})");
    }

    /**
     * <b>这个方法是同步</>,将一串less文本渲染为css文本
     * 
     * @param lessStr
     *            less文本
     * @param paths
     *            搜索路径
     * @return css文本或空字符串,出错的话
     */
    public synchronized String render(String lessStr, String... paths) throws ScriptException {
        Bindings bindings = scriptEngine.createBindings();
        bindings.put("lessStr", lessStr);
        bindings.put("lessc4j", this);
        bindings.put("less", less);
        bindings.put("paths", paths);
        NutMap _result = new NutMap();
        bindings.put("_result", _result);
        script.eval(bindings);
        return _result.getString("css", "");
    }

    /**
     * 这是供require(xxx)读取js脚本用的
     * 
     * @param path
     *            js脚本地址,肯定是/开头
     * @return js文本
     */
    public String readJs(String path) {
        path = "jslib" + path;
        InputStream ins = LesscService.class.getClassLoader().getResourceAsStream(path);
        String str = Lang.readAll(new InputStreamReader(ins));
        return str;
    }

    /**
     * 读取@import用到的文件,子类可按需覆盖.建立符合自身需要的加载路径
     * 
     * @param path
     *            less路径,通常
     * @return less或css文本
     */
    public String readLess(String path) {
        path = "lesscpath/" + path;
        path = Disks.getCanonicalPath(path);
        InputStream ins = LesscService.class.getClassLoader().getResourceAsStream(path);
        if (ins == null) {
            log.debug("not found->" + path);
            return null;
        }
        return Lang.readAll(new InputStreamReader(ins));
    }
}
