package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository

        return userRepository.save(user).getId();
    }

    private int getsubNo(SubscriptionType subscriptionType){

        if(subscriptionType == SubscriptionType.BASIC)
            return 1;

        if(subscriptionType == SubscriptionType.PRO)
            return 2;

        return 3;

    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent()) {
            throw new RuntimeException("User Not found");
        }
        User user = userOptional.get();

        List<WebSeries> seriesList = webSeriesRepository.findAll();

        int count = 0;

        for(WebSeries webSeries: seriesList){
            if(webSeries.getAgeLimit()< user.getAge()
                    && getsubNo(webSeries.getSubscriptionType())<=getsubNo(user.getSubscription().getSubscriptionType())){
                count++;
            }
        }
        return count;
    }


}
