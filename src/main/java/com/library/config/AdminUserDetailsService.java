package com.library.config;
import com.library.repository.AdminUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
public class AdminUserDetailsService implements UserDetailsService {
 private final AdminUserRepository users;
 public AdminUserDetailsService(AdminUserRepository users){this.users=users;}
 @Override public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
  var user=users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
  return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPasswordHash(),java.util.List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
 }
}
