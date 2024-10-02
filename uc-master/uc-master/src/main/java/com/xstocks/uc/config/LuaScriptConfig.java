package com.xstocks.uc.config;


import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;


/**
 * @author firtuss
 */
@Slf4j
@Configuration
public class LuaScriptConfig {

    public final String script;

    public LuaScriptConfig() {
        try (InputStream stream = LuaScriptConfig.class.getClassLoader().getResourceAsStream("script/smooth-rate-limit.lua")) {
            Assert.notNull(stream, "file not exists");
            List<String> lines = IOUtils.readLines(stream, Charset.defaultCharset());
            script = String.join(System.lineSeparator(), lines);
//            log.info("scriptLoad success {}", script);
        } catch (Exception e) {
            throw new BizException(ErrorCode.DEFAULT_ERROR,e.getMessage());
        }
    }

    public String getScriptSha() {
        return this.script;
    }

}
