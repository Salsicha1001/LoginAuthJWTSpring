package com.salsicha.demo.LoginAuth.Service;

import com.salsicha.demo.LoginAuth.Model.DTO.CredDto;
import com.salsicha.demo.LoginAuth.Model.User;
import com.salsicha.demo.LoginAuth.Repositoy.UserRepository;
import com.salsicha.demo.LoginAuth.Service.Exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public User insert(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user;
    }

    public User findById(Long id){
        Optional<User> obj= userRepository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + User.class.getName()));
    }

    public User findByEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);

        return user.orElseThrow(()->new ObjectNotFoundException(
                "Email não encontrado"
        ));
    }
    public Object login(CredDto cred){
        User u = findByEmail(cred.getEmail());
        if(bCryptPasswordEncoder.matches (cred.getPassword(),u.getPassword())== true){
            return u;
        }else{
            return null;
        }

    }
}
