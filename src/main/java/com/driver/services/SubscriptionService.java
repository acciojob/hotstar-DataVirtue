package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    private int getCost(SubscriptionType subscriptionType, int numOfScreens){
//        For Basic Plan : 500 + 200noOfScreensSubscribed
//        For PRO Plan : 800 + 250noOfScreensSubscribed
//        For ELITE Plan : 1000 + 350*noOfScreensSubscribed?
        if(subscriptionType.equals(SubscriptionType.BASIC)){
            return 500 + 200* numOfScreens;
        }

        if(subscriptionType.equals(SubscriptionType.PRO)){
            return 800 + 250* numOfScreens;
        }


        return 1000 + 300* numOfScreens;
    }
    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        Optional<User> userOptional = userRepository.findById(subscriptionEntryDto.getUserId());
        if(!userOptional.isPresent()){
            throw new RuntimeException("user not found");
        }
        User user = userOptional.get();

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());

        subscription.setTotalAmountPaid(getCost(subscriptionEntryDto.getSubscriptionType(),subscriptionEntryDto.getNoOfScreensRequired()));
        subscription.setUser(user);
        user.setSubscription(subscription);
        return subscriptionRepository.save(subscription).getId();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Optional<User> userOptional = userRepository.findById(userId);
//        if(!userOptional.isPresent()){
//            throw new RuntimeException("user not found");
//        }
//        System.out.println(userOptional.get());
        User user = userOptional.get();
        Subscription subscription = user.getSubscription();
        int priceDiff = 0;
        if(subscription.getSubscriptionType()== SubscriptionType.BASIC){

            subscription.setSubscriptionType(SubscriptionType.PRO);

             // should we update price?

            priceDiff = getCost(SubscriptionType.PRO,subscription.getNoOfScreensSubscribed())
                    - getCost(SubscriptionType.BASIC,subscription.getNoOfScreensSubscribed());

            subscription.setTotalAmountPaid(subscription.getTotalAmountPaid()+ priceDiff);

        }else if(subscription.getSubscriptionType()== SubscriptionType.PRO){

            subscription.setSubscriptionType(SubscriptionType.ELITE);

            priceDiff = getCost(SubscriptionType.ELITE,subscription.getNoOfScreensSubscribed())
                    - getCost(SubscriptionType.PRO,subscription.getNoOfScreensSubscribed());

            subscription.setTotalAmountPaid(subscription.getTotalAmountPaid()+ priceDiff);

        }else{
            throw new Exception("Already the best Subscription");
        }
        user.setSubscription(subscription);
        userRepository.save(user);

        return priceDiff;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        Integer revenue = 0;

        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        for(Subscription subscription: subscriptionList){
            revenue+= subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
