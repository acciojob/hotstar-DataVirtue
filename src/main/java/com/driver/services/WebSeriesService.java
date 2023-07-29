package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

       WebSeries webSeries =webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
       if(webSeries!=null){
           throw new Exception("Series is already present");
       }
       webSeries = new WebSeries();
       webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
       webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
       webSeries.setRating(webSeriesEntryDto.getRating());
       webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

       Optional<ProductionHouse>productionHouseOptional = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());
//       if(!productionHouseOptional.isPresent()){
//           throw new Exception("Production House not found");
//       }
       ProductionHouse productionHouse = productionHouseOptional.get();
       webSeries.setProductionHouse(productionHouse);
       productionHouse.setRatings(productionHouse.getId() + webSeries.getRating());
        productionHouse.getWebSeriesList().add(webSeries);

       ProductionHouse savedProductionHouse =  productionHouseRepository.save(productionHouse);
       return productionHouse.getWebSeriesList().get(productionHouse.getWebSeriesList().size()-1).getId();
    }

}
