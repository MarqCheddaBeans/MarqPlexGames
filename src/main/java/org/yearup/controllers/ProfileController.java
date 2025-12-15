package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
// only logged in users should have access to these actions
@PreAuthorize("hasAnyRole('USER','ADMIN')")
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {

    //We need to access data from User and Profile dao
    private UserDao userDao;
    private ProfileDao profileDao;

    //Autowire for spring to do its magic
    @Autowired
    public ProfileController(UserDao userDao, ProfileDao profileDao) {
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    //Endpoint to display profile
    @GetMapping("")
    public Profile getProfile(Principal principal){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        return profileDao.getProfileByUserID(userId);
    }

    //Endpoint to update profile
    @PutMapping("")
    public Profile updateProfile(Principal principal, @RequestBody Profile profile){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        return profileDao.update(userId, profile);
    }
}
