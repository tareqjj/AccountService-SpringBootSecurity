package account.services;

import account.models.Group;
import account.models.User;
import account.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + email);
        }
        return new UserDetailsImpl(user, getAuthorities(user));
    }

    private List<GrantedAuthority> getAuthorities(User user){
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Group group : user.getUserGroups()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(group.getRole());
            authorities.add(grantedAuthority);
        }
        return authorities;
    }

}
