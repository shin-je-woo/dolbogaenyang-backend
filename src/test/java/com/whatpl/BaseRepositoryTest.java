package com.whatpl;

import com.whatpl.global.config.JpaConfig;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // @DataJpaTest에서 제공하는 테스트용 DB 사용 X -> application-test.yml에서 정의한 데이터소스 사용
public abstract class BaseRepositoryTest {

    @Autowired
    protected EntityManager em;
}
