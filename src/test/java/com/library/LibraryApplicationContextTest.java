package com.library;

import com.library.repository.AdminUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties={"app.initial-admin.username=context-admin","app.initial-admin.password=context-password"})
@TestPropertySource(properties={"spring.datasource.url=jdbc:h2:mem:library_context;DB_CLOSE_DELAY=-1","spring.datasource.username=sa","spring.datasource.password=","spring.jpa.hibernate.ddl-auto=create-drop","spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"})
class LibraryApplicationContextTest {
    @Autowired AdminUserRepository users;

    @Test void applicationStartsAndCreatesInitialAdmin(){
        assertNotNull(users.findByUsername("context-admin").orElse(null));
    }
}
