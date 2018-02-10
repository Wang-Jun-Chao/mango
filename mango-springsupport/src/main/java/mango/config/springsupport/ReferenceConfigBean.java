package mango.config.springsupport;

import mango.common.URLParam;
import mango.config.ProtocolConfig;
import mango.config.ReferenceConfig;
import mango.config.RegistryConfig;
import mango.util.CollectionUtil;
import mango.util.FrameworkUtils;
import mango.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

/**
 * 引用配置Bean
 */
public class ReferenceConfigBean<T> extends ReferenceConfig<T> implements
        FactoryBean<T>, BeanFactoryAware, InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Bean工厂
     */
    private transient BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public T getObject() throws Exception {
        return get();
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceClass();
    }

    /**
     * 判断是否是单例
     *
     * @return
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.debug("check reference interface:%s config", getInterfaceName());
        //检查依赖的配置
        checkApplication();
        checkProtocolConfig();
        checkRegistryConfig();

        if (StringUtils.isEmpty(getGroup())) {
            setGroup(URLParam.group.getValue());
        }
        if (StringUtils.isEmpty(getVersion())) {
            setVersion(URLParam.version.getValue());
        }

        if (getTimeout() == null) {
            setTimeout(URLParam.requestTimeout.getIntValue());
        }
        if (getRetries() == null) {
            setRetries(URLParam.retries.getIntValue());
        }
    }

    @Override
    public void destroy() throws Exception {
        super.destroy0();
    }

    private void checkRegistryConfig() {
        if (CollectionUtil.isEmpty(getRegistries())) {
            for (String name : MangoNamespaceHandler.registryDefineNames) {
                RegistryConfig rc = beanFactory.getBean(name, RegistryConfig.class);
                if (rc == null) {
                    continue;
                }
                // TODO
                if (MangoNamespaceHandler.registryDefineNames.size() == 1) {
                    setRegistry(rc);
                } else if (rc.isDefault() != null && rc.isDefault()) {
                    setRegistry(rc);
                }
            }
        }
        // 如果没有获取到注册对象，就使用默认的注册配置
        if (CollectionUtil.isEmpty(getRegistries())) {
            setRegistry(FrameworkUtils.getDefaultRegistryConfig());
        }
    }

    private void checkProtocolConfig() {
        if (CollectionUtil.isEmpty(getProtocols())) {
            for (String name : MangoNamespaceHandler.protocolDefineNames) {
                ProtocolConfig pc = beanFactory.getBean(name, ProtocolConfig.class);
                if (pc == null) {
                    continue;
                }
                // TODO
                if (MangoNamespaceHandler.protocolDefineNames.size() == 1) {
                    setProtocol(pc);
                } else if (pc.isDefault() != null && pc.isDefault()) {
                    setProtocol(pc);
                }
            }
        }

        // 如果没有获取到注册对象，就使用默认的协议配置
        if (CollectionUtil.isEmpty(getProtocols())) {
            setProtocol(FrameworkUtils.getDefaultProtocolConfig());
        }
    }
}
