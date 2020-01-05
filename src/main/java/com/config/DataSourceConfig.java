package com.config;

import com.github.pagehelper.PageInterceptor;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

/**
 * MyBatis基础配置
 * @author Administrator
 *
 */
@Configuration
// 扫描 Mapper 接口并容器管理
@MapperScan(basePackages = DataSourceConfig.PACKAGE, sqlSessionFactoryRef = "testDsSqlSessionFactory")
public class DataSourceConfig {

    // 精确到 cluster 目录，以便跟其他数据源隔离
	//mapper接口所在的包
    static final String PACKAGE = "com.mapper";
    //映射文件目录
    static final String MAPPER_LOCATION = "classpath:mapper/*.xml";
    //哪个包下面的类自动配置别名
    static final String ALIAS = "com.model";

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClass;

    /**
     * 创建数据源对象
     * @return
     * @throws PropertyVetoException
     */
    @Bean(name = "testDataSource")
    public DataSource clusterDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setAutoCommitOnClose(false);
        return dataSource;
    }

    @Bean(name = "testTransactionManager")
    public DataSourceTransactionManager clusterTransactionManager() throws PropertyVetoException {
        return new DataSourceTransactionManager(clusterDataSource());
    }

    @Bean(name = "testDsSqlSessionFactory")
    public SqlSessionFactory testDsSqlSessionFactory(@Qualifier("testDataSource") DataSource clusterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(clusterDataSource);

        
        
        // 加载全局的配置文件
        sessionFactory.setConfigLocation(
                new DefaultResourceLoader().getResource("classpath:mybatis-config.xml")
        		
        );

        // 配置类型别名
        sessionFactory.setTypeAliasesPackage(ALIAS);

        //分页插件
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        interceptor.setProperties(properties);
       
        //添加插件
        sessionFactory.setPlugins(new Interceptor[]{interceptor});

        //设置mapper映射文件所在的地址
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(DataSourceConfig.MAPPER_LOCATION));
        
        return sessionFactory.getObject();
    }
}