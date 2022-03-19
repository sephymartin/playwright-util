package io.sephy.playwright;

import java.util.Map;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.util.CollectionUtils;

import com.microsoft.playwright.Playwright;

/**
 * @author sephy
 * @date 2022-02-10 21:19
 */
public class PlaywrightObjectFactory extends BasePooledObjectFactory<Playwright> {

    // private static final File LOCAL_VALIDATE_HTML;
    //
    // static {
    // LOCAL_VALIDATE_HTML = new File("PlaywrightObject.html");
    // String HTML = "<html><body></body></html>";
    // try {
    // StreamUtils.copy(HTML, StandardCharsets.UTF_8, new FileOutputStream(LOCAL_VALIDATE_HTML));
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }

    private Map<String, String> playwrightConfig;

    public PlaywrightObjectFactory(Map<String, String> playwrightConfig) {
        this.playwrightConfig = playwrightConfig;
    }

    @Override
    public Playwright create() throws Exception {
        return Playwright.create(
            CollectionUtils.isEmpty(playwrightConfig) ? null : new Playwright.CreateOptions().setEnv(playwrightConfig));
    }

    @Override
    public PooledObject<Playwright> wrap(Playwright playwright) {
        return new DefaultPooledPlaywright(playwright);
    }

    @Override
    public void destroyObject(PooledObject<Playwright> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(final PooledObject<Playwright> p) {
        // p.getObject().request().newContext().get(LOCAL_VALIDATE_HTML.toURI().toString());
        p.getObject().request().newContext().get("https://www.baidu.com");
        return true;
    }

    private static class DefaultPooledPlaywright extends DefaultPooledObject<Playwright> {

        public DefaultPooledPlaywright(Playwright playwright) {
            super(playwright);
        }
    }

}
