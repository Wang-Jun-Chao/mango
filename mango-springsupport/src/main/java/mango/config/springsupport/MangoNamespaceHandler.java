package mango.config.springsupport;

import mango.config.ApplicationConfig;
import mango.config.ProtocolConfig;
import mango.config.RegistryConfig;
import mango.util.ConcurrentHashSet;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.Set;

/**
 * Mango命明空间处理器
 */
public class MangoNamespaceHandler extends NamespaceHandlerSupport {
    public final static Set<String> protocolDefineNames = new ConcurrentHashSet<>();
    public final static Set<String> registryDefineNames = new ConcurrentHashSet<>();
    public final static Set<String> serviceConfigDefineNames = new ConcurrentHashSet<>();
    public final static Set<String> referenceConfigDefineNames = new ConcurrentHashSet<>();
    public final static Set<String> applicationConfigDefineNames = new ConcurrentHashSet<>();

    @Override
    public void init() {
        registerBeanDefinitionParser("reference", new MangoBeanDefinitionParser(ReferenceConfigBean.class, false));
        registerBeanDefinitionParser("service", new MangoBeanDefinitionParser(ServiceConfigBean.class, true));
        registerBeanDefinitionParser("registry", new MangoBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("protocol", new MangoBeanDefinitionParser(ProtocolConfig.class, true));
        registerBeanDefinitionParser("application", new MangoBeanDefinitionParser(ApplicationConfig.class, true));
    }
}
