package com.library.config;
import com.library.model.AdminUser;
import com.library.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class AdminInitializer implements ApplicationRunner {
 private final AdminUserRepository users; private final PasswordEncoder encoder;
 @Value("${app.initial-admin.username:}") String username;
 @Value("${app.initial-admin.password:}") String password;
 public AdminInitializer(AdminUserRepository users,PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
 @Override public void run(ApplicationArguments args){
   if(!users.findAll().isEmpty())return;
   if(username==null||username.isBlank()||password==null||password.isBlank())throw new IllegalStateException("No admin exists. Set INITIAL_ADMIN_USERNAME and INITIAL_ADMIN_PASSWORD once, then restart.");
   users.save(new AdminUser(username.trim(),encoder.encode(password)));
 }
}
