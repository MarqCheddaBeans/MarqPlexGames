package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao{
    Profile create(Profile profile);

    Profile update(int userId, Profile profile);

    Profile getProfileByUserID(int userID);
}
