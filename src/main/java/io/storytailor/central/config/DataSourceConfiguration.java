package io.storytailor.central.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DataSourceConfiguration {

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() {
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    return dataSourceBuilder.build();
  }

  @Bean
  @Autowired
  public DataSourceTransactionManager dataSourceTransactionManager(
      @Qualifier("dataSource") DataSource datasource) {
    return new DataSourceTransactionManager(datasource);
  }

  @Bean
  @Primary
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource)
      throws Exception {
    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(dataSource);
    bean.setMapperLocations(
        new PathMatchingResourcePatternResolver()
            .getResources("classpath:mapper/*Mapper.xml"));
    return bean.getObject();
  }

  @Bean
  @Primary
  public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }
}
