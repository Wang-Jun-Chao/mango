package mango.config.springsupport;

import mango.config.ApplicationConfig;
import mango.config.ProtocolConfig;
import mango.config.RegistryConfig;
import mango.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Bean定义解析器
 */
public class MangoBeanDefinitionParser implements BeanDefinitionParser {

    /**
     * 解析的类类型
     */
    private final Class<?> beanClass;

    /**
     *
     */
    private final boolean required;

    public MangoBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    /**
     * 类型解析
     *
     * @param element
     * @param parserContext
     * @param beanClass
     * @param required
     * @return
     * @throws ClassNotFoundException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required)
            throws ClassNotFoundException {
        // 创建根结的Bean定义
        RootBeanDefinition bd = new RootBeanDefinition();
        // 设置xml对应的类
        bd.setBeanClass(beanClass);
        // 不允许lazy init
        bd.setLazyInit(false);

        // 如果没有id则按照规则生成一个id,注册id到context中
        String id = element.getAttribute("id");
        if ((id == null || id.length() == 0) && required) {
            // 获取元素上定义的name属性
            String generatedBeanName = element.getAttribute("name");
            if (generatedBeanName == null || generatedBeanName.length() == 0) {
                // 没有name属性就获取interface上的属性
                generatedBeanName = element.getAttribute("interface");
            }

            // 上面指定的都没有，就取class的名称
            if (generatedBeanName == null || generatedBeanName.length() == 0) {
                generatedBeanName = beanClass.getName();
            }

            // 生成id
            id = generatedBeanName;
            int counter = 2;
            while (parserContext.getRegistry().containsBeanDefinition(id)) {
                id = generatedBeanName + (counter++);
            }
        }

        // 如果id存在判断是否存在，如果没有就注册新的bean定义
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id)) {
                throw new IllegalStateException("Duplicate spring bean id " + id);
            }
            parserContext.getRegistry().registerBeanDefinition(id, bd);
        }

        // 向be定义中添加id
        bd.getPropertyValues().addPropertyValue("id", id);

        // 解析属性
        if (ApplicationConfig.class.equals(beanClass)) {
            MangoNamespaceHandler.applicationConfigDefineNames.add(id);
            parseCommonProperty("name", null, element, bd, parserContext);
            parseCommonProperty("manager", null, element, bd, parserContext);
            parseCommonProperty("organization", null, element, bd, parserContext);
            parseCommonProperty("version", null, element, bd, parserContext);
            parseCommonProperty("env", null, element, bd, parserContext);
            parseCommonProperty("default", "isDefault", element, bd, parserContext);

        } else if (ProtocolConfig.class.equals(beanClass)) {
            MangoNamespaceHandler.protocolDefineNames.add(id);

            parseCommonProperty("name", null, element, bd, parserContext);
            parseCommonProperty("host", null, element, bd, parserContext);
            parseCommonProperty("port", null, element, bd, parserContext);
            parseCommonProperty("codec", null, element, bd, parserContext);
            parseCommonProperty("serialization", null, element, bd, parserContext);
            parseCommonProperty("pool-type", "poolType", element, bd, parserContext);
            parseCommonProperty("min-pool-size", "minPoolSize", element, bd, parserContext);
            parseCommonProperty("max-pool-size", "maxPoolSize", element, bd, parserContext);
            parseCommonProperty("charset", null, element, bd, parserContext);
            parseCommonProperty("buffer-size", "bufferSize", element, bd, parserContext);
            parseCommonProperty("payload", null, element, bd, parserContext);
            parseCommonProperty("heartbeat", null, element, bd, parserContext);
            parseCommonProperty("default", "isDefault", element, bd, parserContext);
        } else if (RegistryConfig.class.equals(beanClass)) {
            MangoNamespaceHandler.registryDefineNames.add(id);

            parseCommonProperty("protocol", null, element, bd, parserContext);
            parseCommonProperty("address", null, element, bd, parserContext);
            parseCommonProperty("connect-timeout", "connectTimeout", element, bd, parserContext);
            parseCommonProperty("session-timeout", "sessionTimeout", element, bd, parserContext);
            parseCommonProperty("username", null, element, bd, parserContext);
            parseCommonProperty("password", null, element, bd, parserContext);
            parseCommonProperty("default", "isDefault", element, bd, parserContext);
        } else if (ReferenceConfigBean.class.equals(beanClass)) {
            MangoNamespaceHandler.referenceConfigDefineNames.add(id);

            parseCommonProperty("interface", "interfaceName", element, bd, parserContext);

            String registry = element.getAttribute("registry");
            if (StringUtils.isNotBlank(registry)) {
                parseMultiRef("registries", registry, bd, parserContext);
            }

            parseCommonProperty("group", null, element, bd, parserContext);
            parseCommonProperty("version", null, element, bd, parserContext);

            parseCommonProperty("timeout", null, element, bd, parserContext);
            parseCommonProperty("retries", null, element, bd, parserContext);
            parseCommonProperty("check", null, element, bd, parserContext);

        } else if (ServiceConfigBean.class.equals(beanClass)) {
            MangoNamespaceHandler.serviceConfigDefineNames.add(id);

            parseCommonProperty("interface", "interfaceName", element, bd, parserContext);

            parseSingleRef("ref", element, bd, parserContext);

            String registry = element.getAttribute("registry");
            if (StringUtils.isNotBlank(registry)) {
                parseMultiRef("registries", registry, bd, parserContext);
            }

            String protocol = element.getAttribute("protocol");
            if (StringUtils.isNotBlank(protocol)) {
                parseMultiRef("protocols", protocol, bd, parserContext);
            }

            parseCommonProperty("timeout", null, element, bd, parserContext);
            parseCommonProperty("retries", null, element, bd, parserContext);

            parseCommonProperty("group", null, element, bd, parserContext);
            parseCommonProperty("version", null, element, bd, parserContext);
        }
        return bd;
    }

    /**
     * 解析通用属性
     *
     * @param name
     * @param alias
     * @param element
     * @param bd
     * @param parserContext
     */
    private static void parseCommonProperty(String name, String alias, Element element, BeanDefinition bd,
                                            ParserContext parserContext) {

        String value = element.getAttribute(name);
        // 有别名就使用别名
        if (StringUtils.isNotBlank(value)) {
            String property = alias != null ? alias : name;
            bd.getPropertyValues().addPropertyValue(property, value);
        }
    }

    /**
     * 解析单实例引用
     *
     * @param property
     * @param element
     * @param bd
     * @param parserContext
     */
    private static void parseSingleRef(String property, Element element, BeanDefinition bd,
                                       ParserContext parserContext) {

        String value = element.getAttribute(property);
        if (StringUtils.isNotBlank(value)) {
            // 解析的上下文中已经包含了属性值，就获取引用的Bean定义
            if (parserContext.getRegistry().containsBeanDefinition(value)) {
                BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(value);
                // 引用的bean必需是单例
                if (!refBean.isSingleton()) {
                    throw new IllegalStateException("The exported service ref "
                            + value + " must be singleton! Please set the "
                            + value + " bean scope to singleton, eg: <bean id=\""
                            + value + "\" scope=\"singleton\" ...>");
                }
            }

            // 将引用属性和值对应起来
            bd.getPropertyValues().addPropertyValue(property, new RuntimeBeanReference(value));
        }
    }

    /**
     * 解析多个引用属性
     *
     * @param property
     * @param value
     * @param bd
     * @param parserContext
     */
    private static void parseMultiRef(String property, String value, BeanDefinition bd,
                                      ParserContext parserContext) {
        // 使用逗号分割，获取值传后转换为运行时的Bean引用
        String[] values = value.split("\\s*[,]+\\s*");
        ManagedList<RuntimeBeanReference> list = null;
        for (String v : values) {
            if (v != null && v.length() > 0) {
                if (list == null) {
                    list = new ManagedList<>();
                }
                list.add(new RuntimeBeanReference(v));
            }
        }
        bd.getPropertyValues().addPropertyValue(property, list);
    }

    /**
     * 解析Bean
     *
     * @param element
     * @param parserContext
     * @return
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        try {
            return parse(element, parserContext, beanClass, required);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}